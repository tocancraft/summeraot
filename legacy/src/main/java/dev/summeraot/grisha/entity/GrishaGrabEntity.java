package dev.summeraot.grisha.entity;

import daot.AttackTitanGrabEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

/** Hitbox registrada exclusivamente para Grisha Titan. */
public final class GrishaGrabEntity extends AttackTitanGrabEntity {
    public GrishaGrabEntity(EntityType<? extends MobEntity> type, World world) {
        super(type, world);
    }
}
