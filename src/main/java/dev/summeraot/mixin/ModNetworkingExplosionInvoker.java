package dev.summeraot.mixin;

import daot.network.ModNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Reutiliza a onda de dano, empurrao e shake da transformacao nativa do DAOT. */
@Mixin(value = ModNetworking.class, remap = false)
public interface ModNetworkingExplosionInvoker {
    @Invoker("triggerTransformationExplosion")
    static void summeraot$triggerExplosion(ServerWorld world, double x, double y,
            double z, ServerPlayerEntity player, double radius, float damage,
            double knockback) {
        throw new AssertionError("Mixin invoker not applied");
    }
}
