package dev.summeraot.entity;

import daot.AttackTitanEyeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

/** Usa dano, posição, persistência e interação da hitbox já mantida pelo DAOT. */
public final class KrugerEyeEntity extends AttackTitanEyeEntity {
    public KrugerEyeEntity(EntityType<? extends MobEntity> type, World world) {
        super(type, world);
    }
}
