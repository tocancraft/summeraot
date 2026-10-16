package dev.summeraot.npc;

import daot.AttackTitanEntity;
import daot.ColossalTitanEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IA e bossbar exclusivamente para titas independentes (ovo, /summon e NPC).
 * Os titas que possuem shifter jogador continuam sob controle do DAOT.
 */
public final class SummerTitanCombat {
    private static final Map<UUID, ServerBossBar> BARS = new ConcurrentHashMap<>();
    private static final double ATTACK_SPEED = 1.05D;

    private SummerTitanCombat() {}

    public static boolean isIndependent(HostileEntity titan) {
        if (titan instanceof AttackTitanEntity attack) {
            return attack.getShifterUUID() == null;
        }
        if (titan instanceof ColossalTitanEntity colossal) {
            return colossal.getShifterUUID() == null;
        }
        return false;
    }

    public static class PlayerTargetGoal extends ActiveTargetGoal<PlayerEntity> {
        private final HostileEntity titan;

        public PlayerTargetGoal(HostileEntity titan) {
            // Os olhos do Colossal ficam muito acima do jogador e a verificacao
            // de linha de visada do ActiveTargetGoal nao o detecta no solo.
            super(titan, PlayerEntity.class, !(titan instanceof ColossalTitanEntity));
            this.titan = titan;
        }

        @Override
        public boolean canStart() {
            return isIndependent(titan) && super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            return isIndependent(titan) && super.shouldContinue();
        }
    }

    public static class RetaliateGoal extends RevengeGoal {
        private final HostileEntity titan;

        public RetaliateGoal(HostileEntity titan) {
            super(titan);
            this.titan = titan;
        }

        @Override
        public boolean canStart() {
            return isIndependent(titan) && super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            return isIndependent(titan) && super.shouldContinue();
        }
    }

    /** Ativa os ataques e animacoes do proprio DAOT, nunca dano artificial. */
    public static class AssaultGoal extends Goal {
        private final HostileEntity titan;
        private int colossalAttackCycle;

        public AssaultGoal(HostileEntity titan) {
            this.titan = titan;
            setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        }

        private boolean hasTarget() {
            LivingEntity target = titan.getTarget();
            return isIndependent(titan) && target != null && target.isAlive()
                    && !target.isRemoved() && !titan.isAiDisabled();
        }

        @Override
        public boolean canStart() {
            return hasTarget();
        }

        @Override
        public boolean shouldContinue() {
            return hasTarget();
        }

        @Override
        public void tick() {
            LivingEntity target = titan.getTarget();
            if (target == null) {
                return;
            }
            titan.getLookControl().lookAt(target, 30.0f, 30.0f);
            if (titan instanceof ColossalTitanEntity colossal) {
                // A caixa de colisao gigante impede frequentemente a criacao de
                // caminhos pela navegacao terrestre vanilla. Avancamos com
                // velocidade horizontal limitada, mantendo colisao e gravidade.
                pursueColossal(colossal, target);
                // Ataques do proprio DAOT: animacao, efeito e cooldown nativos.
                // Alterna punhos e chute; a classe-base rejeita golpes ocupados.
                if (!colossal.isTransforming() && !colossal.isDefeated()
                        && titan.age % 40 == 0) {
                    double horizontal = horizontalDistance(titan, target);
                    if (horizontal <= 32.0D) {
                        if (horizontal <= 18.0D && (colossalAttackCycle++ % 3) == 1) {
                            colossal.triggerKickAbility();
                        } else {
                            colossal.triggerAttack();
                        }
                    }
                }
                return;
            }
            if (titan.age % 10 == 0) {
                titan.getNavigation().startMovingTo(target, ATTACK_SPEED);
            }
            if (titan.squaredDistanceTo(target) <= 7.0D * 7.0D
                    && titan.age % 20 == 0
                    && titan instanceof AttackTitanEntity attack && !attack.isDefeated()) {
                attack.triggerAttack();
            }
        }

        private static double horizontalDistance(HostileEntity titan, LivingEntity target) {
            return Math.hypot(target.getX() - titan.getX(), target.getZ() - titan.getZ());
        }

        private static void pursueColossal(ColossalTitanEntity colossal, LivingEntity target) {
            double dx = target.getX() - colossal.getX();
            double dz = target.getZ() - colossal.getZ();
            double distance = Math.hypot(dx, dz);
            if (distance > 0.001D) {
                // Ataques nativos usam o yaw do corpo: olhar apenas com a
                // cabeca pode fazer os punhos golpearem a direcao errada.
                float facing = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0D);
                colossal.setYaw(facing);
                colossal.setBodyYaw(facing);
                colossal.setHeadYaw(facing);
            }
            if (colossal.isTransforming() || colossal.isDefeated()
                    || colossal.isTitanAttacking() || colossal.isKicking()
                    || colossal.isArmDragging() || colossal.isVentingAny()) {
                return;
            }
            if (distance > 16.0D) {
                // addVelocity marca a velocidade como alterada para sincronizacao.
                // Limitamos a velocidade a ~0.20 blocos/tick: movimento pesado.
                Vec3d current = colossal.getVelocity();
                double intendedX = 0.20D * dx / distance;
                double intendedZ = 0.20D * dz / distance;
                colossal.addVelocity(intendedX - current.x, 0.0D,
                        intendedZ - current.z);
            } else {
                // Impede que o Colossal empurre o jogador com movimento continuo.
                Vec3d current = colossal.getVelocity();
                colossal.addVelocity(-current.x, 0.0D, -current.z);
            }
        }

        @Override
        public void stop() {
            titan.getNavigation().stop();
        }
    }

    /** Barra de vida apenas para entidades autonomas; a barra do jogador e do DAOT. */
    public static void tick(HostileEntity titan) {
        if (!(titan.getWorld() instanceof ServerWorld world)) {
            return;
        }
        if (!isIndependent(titan) || titan.isRemoved() || !titan.isAlive()) {
            cleanup(titan);
            return;
        }
        ShifterVariant variant = ShifterVariant.fromTitanType(titan.getType());
        if (variant == null) {
            return;
        }
        // Fallback para o Colossal: a selecao vanilla depende de verificacoes
        // de visao e distancia que falham com a altura e largura deste mob.
        if (titan instanceof ColossalTitanEntity && titan.age % 20 == 0
                && (titan.getTarget() == null || !titan.getTarget().isAlive())) {
            LivingEntity nearest = null;
            double bestDistanceSquared = 96.0D * 96.0D;
            for (ServerPlayerEntity player : world.getPlayers()) {
                if (!player.isAlive() || player.isSpectator() || player.isCreative()) {
                    continue;
                }
                double distanceSquared = titan.squaredDistanceTo(player);
                if (distanceSquared < bestDistanceSquared) {
                    bestDistanceSquared = distanceSquared;
                    nearest = player;
                }
            }
            if (nearest != null) {
                titan.setTarget(nearest);
            }
        }
        ServerBossBar bar = BARS.computeIfAbsent(titan.getUuid(), id ->
                new ServerBossBar(Text.literal(variant.label + " - Titã"),
                        BossBar.Color.RED, BossBar.Style.PROGRESS));
        bar.setPercent(MathHelper.clamp(titan.getHealth() / Math.max(1.0f, titan.getMaxHealth()), 0.0f, 1.0f));

        if (titan.age % 10 != 0) {
            return;
        }
        double radius = variant == ShifterVariant.ARMIN ? 160.0D : 96.0D;
        for (ServerPlayerEntity player : new ArrayList<>(bar.getPlayers())) {
            if (player.getWorld() != world || player.squaredDistanceTo(titan) > radius * radius
                    || player.isSpectator()) {
                bar.removePlayer(player);
            }
        }
        for (ServerPlayerEntity player : world.getPlayers()) {
            if (!player.isSpectator() && player.squaredDistanceTo(titan) <= radius * radius) {
                bar.addPlayer(player);
            }
        }
    }

    public static void cleanup(HostileEntity titan) {
        ServerBossBar bar = BARS.remove(titan.getUuid());
        if (bar != null) {
            bar.clearPlayers();
        }
    }
}
