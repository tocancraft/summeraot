package dev.summeraot.network;

import dev.summeraot.SummerAOTMod;
import dev.summeraot.SummerAOTPower;
import daot.TitanPowerType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Bridges independent SummerAOT powers with the embedded OmniShifter 1.3.0. */
public final class OmniKruggerNetwork {
    private static final Set<UUID> NATIVE_SWITCHES = new HashSet<>();
    private static final long COOLDOWN_MS = 30_000L; // OmniShifter 1.3.0 server cooldown
    private OmniKruggerNetwork() {}

    public static void register() {
        PayloadTypeRegistry.playC2S().register(KruggerSelectC2SPayload.ID, KruggerSelectC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(GrishaSelectC2SPayload.ID, GrishaSelectC2SPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(KruggerStateS2CPayload.ID, KruggerStateS2CPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(KruggerSelectC2SPayload.ID, (payload, context) ->
                context.server().execute(() -> selectKrugger(context.player())));
        ServerPlayNetworking.registerGlobalReceiver(GrishaSelectC2SPayload.ID, (payload, context) ->
                context.server().execute(() -> selectGrisha(context.player())));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                server.execute(() -> sync(handler.player)));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> sync(newPlayer));
        // Native selections are marked only after Omni has ACCEPTED them (see OmniNativeSelectMixin).
        ServerTickEvents.END_SERVER_TICK.register(OmniKruggerNetwork::completeNativeSwitches);
    }

    /**
     * Called from OmniShifter's accepted native selection, immediately BEFORE
     * its TitanPowerTracker.syncPlayer(...). The DAOT command has already changed
     * the player's active native tag, but Omni's persisted lastActivePower can
     * still contain the PREVIOUS titan until its periodic tracker runs.
     *
     * Updating the original Omni state here makes BOTH Omni's SyncPowersS2C
     * and our own KruggerStateS2C report the same committed power.
     */
    public static void markNativeSelected(ServerPlayerEntity player, Object omniState) {
        TitanPowerType accepted = null;
        for (TitanPowerType candidate : TitanPowerType.values()) {
            if (player.getCommandTags().contains(candidate.getTagName())) {
                accepted = candidate;
                break;
            }
        }
        if (accepted == null) {
            SummerAOTMod.LOGGER.warn("Omni aceitou uma selecao, mas nenhuma tag nativa ativa foi encontrada para {}", player.getName().getString());
        } else {
            try {
                Method setter = omniState.getClass().getMethod("setLastActivePower", UUID.class, TitanPowerType.class);
                setter.invoke(omniState, player.getUuid(), accepted);
            } catch (ReflectiveOperationException | LinkageError ex) {
                SummerAOTMod.LOGGER.error("Nao foi possivel confirmar o poder nativo no estado do OmniShifter", ex);
            }
        }
        NATIVE_SWITCHES.add(player.getUuid());
    }

    private static void completeNativeSwitches(MinecraftServer server) {
        if (NATIVE_SWITCHES.isEmpty()) return;
        Set<UUID> accepted = new HashSet<>(NATIVE_SWITCHES);
        NATIVE_SWITCHES.clear();
        for (UUID id : accepted) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(id);
            if (player == null) continue;
            SummerAOTPower.selectVanilla(player);
            sync(player);
        }
    }

    private static void selectKrugger(ServerPlayerEntity player) {
        if (!SummerAOTPower.hasKrugger(player)) {
            player.sendMessage(Text.literal("SummerAOT: voce nao possui Krugger Titan."), true);
            sync(player);
            return;
        }
        if (SummerAOTPower.isKruggerSelected(player)) {
            sync(player);
            return;
        }
        if (!player.isAlive() || player.isSpectator() || player.hasVehicle()) {
            player.sendMessage(Text.literal("SummerAOT: nao e possivel trocar enquanto transformado."), true);
            sync(player);
            return;
        }
        long now = System.currentTimeMillis();
        long previous = nativeCooldownSince(player.getUuid());
        if (!player.isCreative() && now - previous < COOLDOWN_MS) {
            long remaining = Math.max(1, (COOLDOWN_MS - (now - previous) + 999) / 1000);
            player.sendMessage(Text.literal("SummerAOT: aguarde " + remaining + "s para trocar de tita."), true);
            sync(player);
            return;
        }
        SummerAOTPower.selectKrugger(player);
        if (!player.isCreative()) setNativeCooldownSince(player.getUuid(), now);
        // The OmniShifter's native selection creates a cosmetic lightning bolt
        // and plays thunder/impact sounds. Krugger must have the same feedback.
        strikeCosmeticLightning(player);
        player.sendMessage(Text.literal("Poder ativo: Krugger Titan"), true);
        sync(player);
    }

    private static void selectGrisha(ServerPlayerEntity player) {
        if (!SummerAOTPower.hasGrisha(player)) {
            player.sendMessage(Text.literal("SummerAOT: voce nao possui Grisha Titan."), true);
            sync(player);
            return;
        }
        if (SummerAOTPower.isGrishaSelected(player)) {
            sync(player);
            return;
        }
        if (!player.isAlive() || player.isSpectator() || player.hasVehicle()) {
            player.sendMessage(Text.literal("SummerAOT: nao e possivel trocar enquanto transformado."), true);
            sync(player);
            return;
        }
        long now = System.currentTimeMillis();
        long previous = nativeCooldownSince(player.getUuid());
        if (!player.isCreative() && now - previous < COOLDOWN_MS) {
            long remaining = Math.max(1, (COOLDOWN_MS - (now - previous) + 999) / 1000);
            player.sendMessage(Text.literal("SummerAOT: aguarde " + remaining + "s para trocar de tita."), true);
            sync(player);
            return;
        }
        SummerAOTPower.selectGrisha(player);
        if (!player.isCreative()) setNativeCooldownSince(player.getUuid(), now);
        strikeCosmeticLightning(player);
        player.sendMessage(Text.literal("Poder ativo: Grisha Titan"), true);
        sync(player);
    }

    private static void strikeCosmeticLightning(ServerPlayerEntity player) {
        if (!(player.getWorld() instanceof ServerWorld world)) return;
        LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
        if (lightning == null) return;
        lightning.refreshPositionAfterTeleport(player.getPos());
        lightning.setCosmetic(true); // FX only, no damage or fire.
        world.spawnEntity(lightning);
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
                SoundCategory.WEATHER, 10000.0f, 0.8f + world.random.nextFloat() * 0.2f);
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT,
                SoundCategory.WEATHER, 2.0f, 0.5f + world.random.nextFloat() * 0.2f);
    }

    public static void sync(ServerPlayerEntity player) {
        if (player == null || player.isDisconnected()) return;
        long since = nativeCooldownSince(player.getUuid());
        long ends = since == 0 || player.isCreative() ? 0 : since + COOLDOWN_MS;
        ServerPlayNetworking.send(player, new KruggerStateS2CPayload(
                SummerAOTPower.hasKrugger(player),
                SummerAOTPower.isKruggerSelected(player),
                nativeSelectedOrdinal(player), ends,
                SummerAOTPower.hasGrisha(player), SummerAOTPower.isGrishaSelected(player)));
    }

    /** Read the authoritative native active power from the Omni persistent state. */
    private static int nativeSelectedOrdinal(ServerPlayerEntity player) {
        try {
            Class<?> stateClass = Class.forName("com.omnishifter.data.PlayerTitanPowersState");
            Object state = stateClass.getMethod("getServerState", MinecraftServer.class)
                    .invoke(null, player.getServer());
            Object last = stateClass.getMethod("getLastActivePower", UUID.class)
                    .invoke(state, player.getUuid());
            return last instanceof TitanPowerType power ? power.ordinal() : -1;
        } catch (ReflectiveOperationException | LinkageError ex) {
            SummerAOTMod.LOGGER.error("Nao foi possivel ler a selecao nativa do OmniShifter", ex);
            return -1;
        }
    }

    /* OmniShifter is embedded as a Fabric nested JAR. Its public cooldown map is
       accessed reflectively so it need not be a compilation dependency. */
    @SuppressWarnings("unchecked")
    private static Map<UUID, Long> nativeCooldowns() {
        try {
            Class<?> omni = Class.forName("com.omnishifter.OmniShifter");
            Field field = omni.getField("playerCooldowns");
            return (Map<UUID, Long>) field.get(null);
        } catch (ReflectiveOperationException | LinkageError ex) {
            SummerAOTMod.LOGGER.error("OmniShifter cooldowns indisponiveis", ex);
            return Map.of();
        }
    }
    private static long nativeCooldownSince(UUID id) {
        return nativeCooldowns().getOrDefault(id, 0L);
    }
    private static void setNativeCooldownSince(UUID id, long now) {
        try { nativeCooldowns().put(id, now); }
        catch (UnsupportedOperationException ex) {
            SummerAOTMod.LOGGER.error("OmniShifter cooldowns nao permitem gravacao", ex);
        }
    }
}
