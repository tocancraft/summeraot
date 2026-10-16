package dev.summeraot.mixin;

import daot.ColossalTitanEntity;
import daot.network.ModNetworking;
import dev.summeraot.SummerAOTMod;
import dev.summeraot.SummerAOTPower;
import dev.summeraot.armin.ArminColossalEntities;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Substitui apenas a entidade na variavel local do spawnColossalTitan.
 * A sequencia ORIGINAL do DAOT ainda gera raio, nuke, hitboxes,
 * congelamento temporario, subida do jogador, stamina e achievements.
 * Dependente do ASTORE original do DAOT 2.5.0: revalidar em updates.
 */
@Mixin(value = ModNetworking.class, remap = false)
public abstract class ArminColossalSpawnMixin {
    @ModifyVariable(method = "spawnColossalTitan", at = @At("STORE"), ordinal = 0, require = 1)
    private static ColossalTitanEntity summeraot$spawnArmin(
            ColossalTitanEntity original, ServerPlayerEntity player, ServerWorld world,
            double x, double y, double z, float yaw) {
        if (!SummerAOTPower.isArminSelected(player)) return original;
        ColossalTitanEntity armin = ArminColossalEntities.ARMIN_COLOSSAL.create(world);
        if (armin == null) {
            SummerAOTMod.LOGGER.error("Armin: erro criando entidade; mantendo Colossal vanilla");
            return original;
        }
        return armin;
    }
}
