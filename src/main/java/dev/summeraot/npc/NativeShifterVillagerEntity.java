package dev.summeraot.npc;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

/** Aldeao funcional com skin humana, profissao e comportamento vanilla. */
public final class NativeShifterVillagerEntity extends VillagerEntity {
    private int pendingShiftTicks;
    private java.util.UUID lastAttacker;

    public NativeShifterVillagerEntity(EntityType<? extends VillagerEntity> type, World world) {
        super(type, world);
    }

    public NativeNpcVariant variant() {
        return NativeNpcVariant.byVillagerType(getType());
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!getWorld().isClient && getWorld() instanceof ServerWorld
                && amount > 0.0f && variant() != null) {
            if (pendingShiftTicks > 0) return false;
            float remaining = getHealth() - amount;
            if (remaining <= Math.max(1.0f, getMaxHealth() * 0.70f)) {
                pendingShiftTicks = 40;
                lastAttacker = source.getAttacker() instanceof LivingEntity living ? living.getUuid() : null;
                setHealth(Math.max(1.0f, remaining));
                return false;
            }
        }
        return super.damage(source, amount);
    }

    @Override
    public void tick() {
        super.tick();
        NativeNpcVariant variant = variant();
        if (variant == null) return;
        if (getWorld().isClient && isAlive()) {
            DustParticleEffect dust = new DustParticleEffect(variant.particleColor(), 1.0f);
            for (int i = 0; i < 4; i++) {
                double dx = (random.nextDouble() - 0.5D) * 0.8D;
                double dy = (random.nextDouble() - 0.5D) * 1.2D;
                double dz = (random.nextDouble() - 0.5D) * 0.8D;
                getWorld().addParticle(dust, getX() + dx,
                        getY() + getHeight() * 0.5D + dy, getZ() + dz,
                        0.0D, 0.02D, 0.0D);
            }
        }
        if (pendingShiftTicks > 0 && getWorld() instanceof ServerWorld world) {
            if (--pendingShiftTicks == 0) {
                NativeNpcTransform.complete(this, variant, world, lastAttacker);
            }
        }
    }
}
