package dev.summeraot.grisha.entity;

import daot.AttackTitanEntity;
import dev.summeraot.grisha.GrishaEntities;
import dev.summeraot.mixin.AttackTitanTimersAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;
import dev.summeraot.npc.SummerNpcLogic;
import dev.summeraot.npc.SummerTitanCombat;

/**
 * O Grisha eh um shifter novo, independente do Attack e do Krugger.
 * Herda os ataques normais do AttackTitanEntity do DAOT 2.5.0.
 * Sem endurecimento (habilidade 6), escalada endurecida ou modo Berserker.
 */
public final class GrishaTitanEntity extends AttackTitanEntity {
    // IDs da política Krugger/Spring: 6=endurecimento, 9=Berserker.
    private static final int ABILITY_HARDENING = 6;
    private static final int ABILITY_BERSERKER = 9;

    // 70% da velocidade original; danos normais e habilidades = 160%.
    private static final float ATTACK_DAMAGE_MULTIPLIER = 1.60F;
    private static final int SLOW_TICKS_PER_TEN = 3;

    @Override
    protected float getAttackDamageMultiplier() {
        return super.getAttackDamageMultiplier() * ATTACK_DAMAGE_MULTIPLIER;
    }

    /**
     * O DAOT sincroniza a janela de ataque, o cooldown e o impacto por tres
     * temporizadores privados. Pausar 3 em cada 10 incrementos/decrementos
     * torna os ataques 30% mais lentos de ponta a ponta, sem desacelerar
     * movimento, salto ou a recuperacao de vida.
     */
    private void applyGrishaAttackPacing() {
        if (getWorld().isClient || this.age % 10 >= SLOW_TICKS_PER_TEN) {
            return;
        }
        AttackTitanTimersAccessor timers = (AttackTitanTimersAccessor) (Object) this;
        // A defesa (numero 5) usa Integer.MAX_VALUE como cooldown infinito.
        if (getAttackNumber() == 5) {
            return;
        }
        int cooldown = timers.summeraot$getAttackCooldown();
        if (cooldown > 0 && cooldown < Integer.MAX_VALUE - 1) {
            timers.summeraot$setAttackCooldown(cooldown + 1);
        }
        int animTicks = timers.summeraot$getAttackAnimationTicks();
        if (animTicks > 0 && animTicks < Integer.MAX_VALUE - 1) {
            timers.summeraot$setAttackAnimationTicks(animTicks + 1);
        }
        if (isTitanAttacking() && !timers.summeraot$isAttackEffectTriggered()) {
            int effectTimer = timers.summeraot$getAttackEffectTimer();
            if (effectTimer > 0) {
                timers.summeraot$setAttackEffectTimer(effectTimer - 1);
            }
        }
    }

    public GrishaTitanEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void triggerAbility(int ability) {
        if (ability == ABILITY_HARDENING || ability == ABILITY_BERSERKER) {
            return;
        }
        super.triggerAbility(ability);
    }

    @Override
    public void setHardeningMode(int hardeningMode) {
        // Inclui restauracao de NBT e comandos externos; sempre desabilitado.
        super.setHardeningMode(0);
    }

    @Override
    public boolean isArmsHardened() {
        return false;
    }

    @Override
    public void setTitanClimbing(boolean climbing) {
        // No DAOT a escalada exige ativacao do endurecimento.
        super.setTitanClimbing(false);
    }

    @Override
    public boolean isTitanClimbing() {
        return false;
    }

    @Override
    public void onPlayerShift(PlayerEntity player) {
        super.onPlayerShift(player);
        if (bossBar != null) {
            String label = player.getCommandTags().contains("titan_stealth")
                ? "Grisha Titan" : player.getName().getString() + " - Grisha Titan";
            bossBar.setName(Text.literal(label));
        }
    }


    @Override
    protected void initGoals() {
        // O DAOT deixa os goals vazios para o titan controlado pelo jogador.
        // Estes goals sao automaticamente inativos enquanto houver shifter.
        this.goalSelector.add(1, new SummerTitanCombat.AssaultGoal(this));
        this.targetSelector.add(1, new SummerTitanCombat.RetaliateGoal(this));
        this.targetSelector.add(2, new SummerTitanCombat.PlayerTargetGoal(this));
    }

    @Override
    public void remove(net.minecraft.entity.Entity.RemovalReason reason) {
        SummerTitanCombat.cleanup(this);
        super.remove(reason);
    }

    @Override
    public void tick() {
        applyGrishaAttackPacing();
        super.tick();
        if (getHardeningMode() != 0) {
            super.setHardeningMode(0);
        }
        if (isTitanClimbing()) {
            super.setTitanClimbing(false);
        }
        SummerNpcLogic.tickNpcTitan(this);
        SummerTitanCombat.tick(this);
    }

    @Override
    public void spawnHitboxes() {
        if (getWorld().isClient) {
            return;
        }
        if (napeEntity != null && napeEntity.isAlive()
            && eyeEntity != null && eyeEntity.isAlive()
            && grabHitboxEntity != null && grabHitboxEntity.isAlive()) {
            return;
        }

        // Recria um trio inteiro se alguma hitbox foi destruida/descarregada.
        if (napeEntity != null && !napeEntity.isRemoved()) {
            napeEntity.discard();
        }
        if (eyeEntity != null && !eyeEntity.isRemoved()) {
            eyeEntity.discard();
        }
        if (grabHitboxEntity != null && !grabHitboxEntity.isRemoved()) {
            grabHitboxEntity.discard();
        }
        napeEntity = null;
        eyeEntity = null;
        grabHitboxEntity = null;

        GrishaNapeEntity nape = new GrishaNapeEntity(GrishaEntities.GRISHA_NAPE, getWorld());
        GrishaEyeEntity eye = new GrishaEyeEntity(GrishaEntities.GRISHA_EYE, getWorld());
        GrishaGrabEntity grab = new GrishaGrabEntity(GrishaEntities.GRISHA_GRAB, getWorld());

        nape.setParentTitan(this);
        eye.setParentTitan(this);
        grab.setParentTitan(this);

        nape.refreshPositionAndAngles(getX(), getY(), getZ(), getYaw(), getPitch());
        eye.refreshPositionAndAngles(getX(), getY(), getZ(), getYaw(), getPitch());
        grab.refreshPositionAndAngles(getX(), getY(), getZ(), getYaw(), getPitch());

        boolean napeSpawned = getWorld().spawnEntity(nape);
        boolean eyeSpawned = napeSpawned && getWorld().spawnEntity(eye);
        boolean grabSpawned = eyeSpawned && getWorld().spawnEntity(grab);
        if (!grabSpawned) {
            if (napeSpawned) {
                nape.discard();
            }
            if (eyeSpawned) {
                eye.discard();
            }
            return;
        }
        napeEntity = nape;
        eyeEntity = eye;
        grabHitboxEntity = grab;
    }
}
