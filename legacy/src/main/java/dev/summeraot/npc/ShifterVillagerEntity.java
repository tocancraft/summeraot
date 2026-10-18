package dev.summeraot.npc;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.particle.DustParticleEffect;
import org.joml.Vector3f;

/** Aldeao funcional (comercio, profissoes, IA vanilla) com identidade fixa. */
public final class ShifterVillagerEntity extends VillagerEntity {
    private int pendingShiftTicks;
    // Cores e tamanho identicos ao indicador dos aldeoes do DAOT 2.5.0.
    private static final DustParticleEffect ATTACK_PARTICLE =
            new DustParticleEffect(new Vector3f(0.2f, 0.9f, 0.2f), 1.0f);
    private static final DustParticleEffect COLOSSAL_PARTICLE =
            new DustParticleEffect(new Vector3f(0.9f, 0.2f, 0.2f), 1.0f);

    public ShifterVillagerEntity(EntityType<? extends VillagerEntity> type, World world) {
        super(type, world);
    }

    public ShifterVariant variant() {
        return ShifterVariant.fromVillagerType(getType());
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!getWorld().isClient && getWorld() instanceof ServerWorld world
                && amount > 0.0f) {
            if (pendingShiftTicks > 0) {
                return false;
            }
            if (getHealth() - amount <= Math.max(1.0f, getMaxHealth() * 0.70f)) {
                pendingShiftTicks = 40;
                setHealth(Math.max(1.0f, getHealth() - amount));
                LivingEntity attacker = source.getAttacker() instanceof LivingEntity living
                        ? living : null;
                SummerNpcLogic.onStartTransform(this, world, attacker);
                return false;
            }
        }
        return super.damage(source, amount);
    }

    @Override
    public void tick() {
        super.tick();
        // Sem rede adicional: cada cliente ja conhece o tipo do aldeao.
        // Repete o efeito Dust do PoweredVillagerParticleHandler do DAOT.
        if (getWorld().isClient && isAlive() && variant() != null) {
            DustParticleEffect particle = variant() == ShifterVariant.ARMIN
                    ? COLOSSAL_PARTICLE : ATTACK_PARTICLE;
            for (int i = 0; i < 4; i++) {
                double dx = (random.nextDouble() - 0.5D) * 0.8D;
                double dy = (random.nextDouble() - 0.5D) * 1.2D;
                double dz = (random.nextDouble() - 0.5D) * 0.8D;
                getWorld().addParticle(particle, getX() + dx,
                        getY() + getHeight() * 0.5D + dy, getZ() + dz,
                        0.0D, 0.02D, 0.0D);
            }
        }
        if (pendingShiftTicks > 0 && getWorld() instanceof ServerWorld world) {
            pendingShiftTicks--;
            if (pendingShiftTicks == 0) {
                SummerNpcLogic.completeTransform(this, world);
            }
        }
    }
}
