package dev.summeraot.mixin;

import daot.ColossalTitanEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Permite ativar a sequencia nuclear nativa sem vincular um jogador ao NPC. */
@Mixin(value = ColossalTitanEntity.class, remap = false)
public interface ColossalNukeAccessor {
    @Accessor("explosionCenterX") void summeraot$setExplosionCenterX(double x);
    @Accessor("explosionCenterY") void summeraot$setExplosionCenterY(double y);
    @Accessor("explosionCenterZ") void summeraot$setExplosionCenterZ(double z);
    @Accessor("explosionPhase") void summeraot$setExplosionPhase(int phase);
    @Accessor("nukeDebrisSpawned") void summeraot$setNukeDebrisSpawned(int count);
}
