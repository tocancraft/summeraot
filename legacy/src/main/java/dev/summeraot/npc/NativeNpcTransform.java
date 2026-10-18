package dev.summeraot.npc;

import daot.ArmoredTitanEntity;
import daot.AttackTitanEntity;
import daot.ColossalTitanEntity;
import daot.FemaleTitanEntity;
import daot.WarhammerTitanEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.UUID;

/** Transforma o aldeao no tipo ORIGINAL do DAOT, nao numa copia do titã. */
public final class NativeNpcTransform {
    private NativeNpcTransform() {}

    public static void complete(NativeShifterVillagerEntity human, NativeNpcVariant variant,
                                ServerWorld world, UUID attackerId) {
        if (!human.isAlive() || human.isRemoved()) return;
        HostileEntity titan = variant.titanType().create(world);
        if (titan == null) return;
        titan.refreshPositionAndAngles(human.getX(), human.getY(), human.getZ(), human.getYaw(), 0.0f);
        titan.setCustomName(Text.literal(variant.label + " (aldeão shifter)"));
        titan.setCustomNameVisible(true);
        titan.setAiDisabled(false);
        titan.addCommandTag(NativeNpcCombat.TAG);
        titan.addCommandTag("summeraot.native_power." + variant.key);
        titan.addCommandTag("summeraot.native_prof." +
                Registries.VILLAGER_PROFESSION.getId(human.getVillagerData().getProfession()));
        titan.addCommandTag("summeraot.native_level." + human.getVillagerData().getLevel());
        if (attackerId != null) {
            Entity attacker = world.getEntity(attackerId);
            if (attacker instanceof LivingEntity living && living.isAlive()) {
                titan.setTarget(living);
            }
        }
        if (titan instanceof AttackTitanEntity t) t.setTransformationTicks(80);
        else if (titan instanceof ColossalTitanEntity t) t.setTransformationTicks(80);
        else if (titan instanceof ArmoredTitanEntity t) t.setTransformationTicks(80);
        else if (titan instanceof FemaleTitanEntity t) t.setTransformationTicks(80);
        else if (titan instanceof WarhammerTitanEntity t) t.setTransformationTicks(80);
        if (!world.spawnEntity(titan)) return;
        if (titan instanceof AttackTitanEntity t) t.spawnHitboxes();
        else if (titan instanceof ColossalTitanEntity t) t.spawnHitboxes();
        else if (titan instanceof ArmoredTitanEntity t) t.spawnHitboxes();
        else if (titan instanceof FemaleTitanEntity t) t.spawnHitboxes();
        else if (titan instanceof WarhammerTitanEntity t) t.spawnHitboxes();
        human.discard();
        NativeNpcCombat.track(titan);
        SummerNpcShiftEffects.onNpcShift(world, titan, variant);
    }
}
