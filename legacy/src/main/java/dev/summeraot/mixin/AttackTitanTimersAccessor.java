package dev.summeraot.mixin;

import daot.AttackTitanEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Leitura/escrita apenas dos temporizadores de ataque do DAOT 2.5.0. */
@Mixin(value = AttackTitanEntity.class, remap = false)
public interface AttackTitanTimersAccessor {
    @Accessor("attackCooldown") int summeraot$getAttackCooldown();
    @Accessor("attackCooldown") void summeraot$setAttackCooldown(int value);
    @Accessor("attackAnimationTicks") int summeraot$getAttackAnimationTicks();
    @Accessor("attackAnimationTicks") void summeraot$setAttackAnimationTicks(int value);
    @Accessor("attackEffectTimer") int summeraot$getAttackEffectTimer();
    @Accessor("attackEffectTimer") void summeraot$setAttackEffectTimer(int value);
    @Accessor("attackEffectTriggered") boolean summeraot$isAttackEffectTriggered();
}
