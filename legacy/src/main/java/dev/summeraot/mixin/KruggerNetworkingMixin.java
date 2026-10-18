package dev.summeraot.mixin;

import daot.PowerAuthority;
import daot.AttackTitanEntity;
import daot.TitanPowerType;
import daot.network.ModNetworking;
import dev.summeraot.SummerAOTEntities;
import dev.summeraot.grisha.GrishaEntities;
import dev.summeraot.grisha.entity.GrishaTitanEntity;
import dev.summeraot.SummerAOTMod;
import dev.summeraot.SummerAOTPower;
import dev.summeraot.entity.KrugerTitanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Liga o poder independente do SummerAOT ao fluxo NATIVO de transformacao
 * do DAOT sem conceder, falsificar ou modificar a tag "attack" do jogador.
 *
 * O DAOT tem um enum fechado de oito tipos, portanto o ATTACK e usado
 * somente como canal interno de despacho da animacao / stamina.
 * A entidade criada e o poder persistido continuam sendo KRUGGER ou GRISHA.
 *
 * Referencia estrutural: implementacao de ModNetworkingMixin do
 * SpringAOT 3.5.2, adaptada para os poderes independentes do SummerAOT.
 */
@Mixin(value = ModNetworking.class, remap = false)
public abstract class KruggerNetworkingMixin {

    /**
     * O DAOT consulta PowerAuthority.hasShifter(ATTACK) durante o Shift.
     * A resposta local a essa consulta aceita Krugger ou Grisha, sem alterar
     * TitanPowerData nem atribuir o poder Attack ao jogador.
     * Para todos os outros tipos e jogadores, a regra original e preservada.
     */
    @Redirect(
        method = {"handleTitanShift", "handleTeaseShift"},
        at = @At(
            value = "INVOKE",
            target = "Ldaot/PowerAuthority;hasShifter(Lnet/minecraft/entity/player/PlayerEntity;Ldaot/TitanPowerType;)Z"
        )
    )
    private static boolean summeraot$recognizeKruggerPower(PlayerEntity player, TitanPowerType type) {
        if (type == TitanPowerType.ATTACK && player instanceof ServerPlayerEntity serverPlayer
                && (SummerAOTPower.isKruggerSelected(serverPlayer)
                    || SummerAOTPower.isGrishaSelected(serverPlayer))) {
            return true;
        }
        if (type == TitanPowerType.COLOSSAL && player instanceof ServerPlayerEntity serverPlayer
                && SummerAOTPower.isArminSelected(serverPlayer)) {
            return true;
        }
        return PowerAuthority.hasShifter(player, type);
    }

    /**
     * O DAOT ja executou suas verificacoes, consumo de stamina e a fase
     * de transformacao. No instante em que criaria seu Attack Titan,
     * o SummerAOT cria a entidade propria do poder selecionado.
     */
    @Inject(method = "spawnAttackTitan", at = @At("HEAD"), cancellable = true)
    private static void summeraot$spawnKrugger(
            ServerPlayerEntity player,
            ServerWorld world,
            double x,
            double y,
            double z,
            float yaw,
            CallbackInfo ci) {
        boolean grishaSelected = SummerAOTPower.isGrishaSelected(player);
        if (!SummerAOTPower.isKruggerSelected(player) && !grishaSelected) {
            return;
        }
        ci.cancel();

        if (!player.isAlive() || player.isSpectator()) {
            return;
        }
        if (player.getVehicle() instanceof KrugerTitanEntity
                || player.getVehicle() instanceof GrishaTitanEntity) {
            return;
        }

        AttackTitanEntity titan = grishaSelected
                ? GrishaEntities.GRISHA_TITAN.create(world)
                : SummerAOTEntities.KRUGER.create(world);
        if (titan == null) {
            player.sendMessage(Text.literal("SummerAOT: nao foi possivel criar o tita selecionado."), false);
            SummerAOTMod.LOGGER.error("SummerAOT: falha na criacao de tita para {}", player.getName().getString());
            return;
        }

        // Reaproveita a preparacao do titan do Spring/DAOT; a habilidade
        // de endurecimento e Berserk permanecem bloqueadas na entidade.
        titan.refreshPositionAndAngles(x, y, z, yaw, player.getPitch());
        titan.setBodyYaw(yaw);
        titan.setHeadYaw(yaw);
        titan.setTransformationTicks(80);
        titan.setNoGravity(true);
        titan.setShifterUUID(player.getUuid());
        titan.setHardeningMode(0);

        if (!world.spawnEntity(titan)) {
            player.sendMessage(Text.literal("SummerAOT: falha ao inserir tita no mundo."), false);
            return;
        }
        ModNetworking.spawnShiftLightning(world, x, y, z);
        titan.spawnHitboxes();
        titan.onPlayerShift(player);
        SummerAOTMod.LOGGER.info("SummerAOT: {} transformado em {} Titan pelo fluxo nativo do DAOT",
                player.getName().getString(), grishaSelected ? "Grisha" : "Krugger");
    }
}
