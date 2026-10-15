package dev.summeraot.npc;

import daot.ArmoredTitanEntity;
import daot.AttackTitanEntity;
import daot.ColossalTitanEntity;
import daot.FemaleTitanEntity;
import daot.WarhammerTitanEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** IA e barra apenas dos titas gerados PELOS aldeoes nativos do SummerAOT.
 * Os titas controlados por jogadores e os NPCs vanilla do DAOT nao mudam.
 */
public final class NativeNpcCombat {
    public static final String TAG = "summeraot.native_npc_titan";
    private static final String POWER = "summeraot.native_power.";
    private static final String PROF = "summeraot.native_prof.";
    private static final String LEVEL = "summeraot.native_level.";
    private static final Map<UUID, HostileEntity> ACTIVE = new ConcurrentHashMap<>();
    private static final Map<UUID, ServerBossBar> BARS = new ConcurrentHashMap<>();

    private NativeNpcCombat() {}

    public static void initialize() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof HostileEntity titan && titan.getCommandTags().contains(TAG)) {
                ACTIVE.put(entity.getUuid(), titan);
            }
        });
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            ACTIVE.remove(entity.getUuid());
            cleanup(entity.getUuid());
        });
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.getTime() % 5L != 0L) return;
            for (HostileEntity titan : new ArrayList<>(ACTIVE.values())) {
                if (titan.getWorld() != world || !titan.getCommandTags().contains(TAG)) continue;
                if (titan.isRemoved() || !titan.isAlive()) {
                    ACTIVE.remove(titan.getUuid());
                    cleanup(titan.getUuid());
                    continue;
                }
                tick(world, titan);
            }
        });
    }

    public static void track(HostileEntity titan) {
        if (titan.getCommandTags().contains(TAG)) ACTIVE.put(titan.getUuid(), titan);
    }

    private static NativeNpcVariant powerOf(HostileEntity titan) {
        for (NativeNpcVariant variant : NativeNpcVariant.values()) {
            if (titan.getCommandTags().contains(POWER + variant.key)) return variant;
        }
        return null;
    }

    private static void tick(ServerWorld world, HostileEntity titan) {
        NativeNpcVariant variant = powerOf(titan);
        if (variant == null) return;
        if (titan.age >= 20 * 80 || titan.getHealth() <= 1.0f) {
            restore(world, titan, variant);
            return;
        }

        LivingEntity target = titan.getTarget();
        if (target == null || !target.isAlive() || target.isRemoved()
                || (target instanceof ServerPlayerEntity p && (p.isCreative() || p.isSpectator()))) {
            target = findTarget(world, titan, variant.isColossal() ? 120.0D : 72.0D);
            titan.setTarget(target);
        }
        if (target != null && !titan.isAiDisabled()) {
            final double dx = target.getX() - titan.getX();
            final double dz = target.getZ() - titan.getZ();
            final double distance = Math.hypot(dx, dz);
            if (distance > 0.01D) {
                float facing = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0D);
                titan.setYaw(facing);
                titan.setBodyYaw(facing);
                titan.setHeadYaw(facing);
            }
            if (variant.isColossal()) {
                if (distance > 22.0D && distance > 0.01D) {
                    Vec3d vel = titan.getVelocity();
                    titan.addVelocity(dx / distance * 0.18D - vel.x, 0.0D,
                            dz / distance * 0.18D - vel.z);
                }
            } else if (distance > 4.0D) {
                titan.getNavigation().startMovingTo(target, 1.05D);
            }
            if (titan.age % 40 < 5) {
                attack(titan, variant, distance);
            }
        }
        showBossBar(world, titan, variant);
    }

    private static LivingEntity findTarget(ServerWorld world, HostileEntity titan, double radius) {
        LivingEntity best = null;
        double bestDist = radius * radius;
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (!player.isAlive() || player.isSpectator() || player.isCreative()) continue;
            double dsq = titan.squaredDistanceTo(player);
            if (dsq < bestDist) {
                best = player;
                bestDist = dsq;
            }
        }
        return best;
    }

    private static void attack(HostileEntity titan, NativeNpcVariant variant, double distance) {
        if (titan instanceof ColossalTitanEntity t) {
            if (distance <= 32.0D && !t.isDefeated()) t.triggerAttack();
        } else if (titan instanceof AttackTitanEntity t) {
            if (distance <= 8.0D && !t.isDefeated()) t.triggerAttack();
        } else if (titan instanceof ArmoredTitanEntity t) {
            if (distance <= 8.0D && !t.isDefeated()) t.triggerAttack();
        } else if (titan instanceof FemaleTitanEntity t) {
            if (distance <= 8.0D && !t.isDefeated()) t.triggerAttack();
        } else if (titan instanceof WarhammerTitanEntity t) {
            if (distance <= 12.0D && !t.isDefeated()) t.triggerAttack();
        }
    }

    private static void showBossBar(ServerWorld world, HostileEntity titan, NativeNpcVariant variant) {
        ServerBossBar bar = BARS.computeIfAbsent(titan.getUuid(), id ->
                new ServerBossBar(Text.literal(variant.label + " - NPC"), BossBar.Color.RED, BossBar.Style.PROGRESS));
        bar.setPercent(MathHelper.clamp(titan.getHealth() / Math.max(1.0f, titan.getMaxHealth()), 0.0f, 1.0f));
        double radius = variant.isColossal() ? 160.0D : 96.0D;
        for (ServerPlayerEntity player : new ArrayList<>(bar.getPlayers())) {
            if (player.getWorld() != world || player.isSpectator() || player.squaredDistanceTo(titan) > radius * radius) {
                bar.removePlayer(player);
            }
        }
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (!player.isSpectator() && player.squaredDistanceTo(titan) <= radius * radius) bar.addPlayer(player);
        }
    }

    private static String tagValue(Entity entity, String prefix) {
        for (String tag : entity.getCommandTags()) {
            if (tag.startsWith(prefix)) return tag.substring(prefix.length());
        }
        return null;
    }

    private static void restore(ServerWorld world, HostileEntity titan, NativeNpcVariant variant) {
        NativeShifterVillagerEntity villager = NativeNpcRegistry.newVillager(variant, world);
        if (villager == null) return;
        villager.refreshPositionAndAngles(titan.getX(), titan.getY(), titan.getZ(), titan.getYaw(), 0.0f);
        String profession = tagValue(titan, PROF);
        if (profession != null) {
            Identifier id = Identifier.tryParse(profession);
            if (id != null && Registries.VILLAGER_PROFESSION.containsId(id)) {
                villager.setVillagerData(villager.getVillagerData().withProfession(Registries.VILLAGER_PROFESSION.get(id)));
            }
        }
        String level = tagValue(titan, LEVEL);
        if (level != null) {
            try {
                int lv = Integer.parseInt(level);
                villager.setVillagerData(villager.getVillagerData().withLevel(Math.max(1, Math.min(5, lv))));
            } catch (NumberFormatException ignored) {}
        }
        if (world.spawnEntity(villager)) {
            ACTIVE.remove(titan.getUuid());
            cleanup(titan.getUuid());
            titan.discard();
        }
    }

    private static void cleanup(UUID uuid) {
        ServerBossBar bar = BARS.remove(uuid);
        if (bar != null) bar.clearPlayers();
    }
}
