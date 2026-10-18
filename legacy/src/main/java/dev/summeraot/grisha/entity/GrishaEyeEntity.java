package dev.summeraot.grisha.entity;

import daot.AttackTitanEyeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

/** Hitbox registrada exclusivamente para Grisha Titan. */
public final class GrishaEyeEntity extends AttackTitanEyeEntity {
    public GrishaEyeEntity(EntityType<? extends MobEntity> type, World world) {
        super(type, world);
    }
}
