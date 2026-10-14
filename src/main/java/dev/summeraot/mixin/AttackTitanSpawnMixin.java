package dev.summeraot.mixin;

import daot.AttackTitanEntity;
import daot.network.ModNetworking;
import dev.summeraot.SummerAOTEntities;
import dev.summeraot.entity.KrugerTitanEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Porta a seleção de forma de Kruger sem substituir o fluxo de shift do DAOT.
 * A rotina original do Danny valida o poder, gera raio/explosão, efeitos,
 * avanço, regen/stamina e demais eventos; apenas a entidade criada é trocada.
 *
 * Ponto de integração identificado no DAOT compilado (versão interna 2.4.1):
 * método privado static spawnAttackTitan(...), ASTORE do AttackTitanEntity.
 * A injeção é sensível a mudanças nesse bytecode e requer revalidação ao atualizar.
 */
@Mixin(value = ModNetworking.class, remap = false)
public abstract class AttackTitanSpawnMixin {
    @ModifyVariable(method = "spawnAttackTitan", at = @At("STORE"), ordinal = 0, require = 1)
    private static AttackTitanEntity summeraot$selectKruger(
            AttackTitanEntity original, ServerPlayerEntity player, ServerWorld world,
            double x, double y, double z, float yaw) {
        // Não atribui poderes: o próprio DAOT já determinou que é um Attack shifter.
        if (!player.getCommandTags().contains("attack")
                || !player.getCommandTags().contains("attack_3")) {
            return original;
        }
        KrugerTitanEntity kruger = SummerAOTEntities.KRUGER.create(world);
        if (kruger == null) {
            return original;
        }
        return kruger;
    }
}
