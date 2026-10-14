package dev.summeraot.entity;

import daot.AttackTitanNapeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

/** Usa dano, posição, persistência e interação da hitbox já mantida pelo DAOT. */
public final class KrugerNapeEntity extends AttackTitanNapeEntity {
    public KrugerNapeEntity(EntityType<? extends MobEntity> type, World world) {
        super(type, world);
    }
}
