package dev.summeraot.mixin;

import dev.summeraot.client.KruggerAbilityHud;
import dev.summeraot.entity.KrugerTitanEntity;
import dev.summeraot.grisha.entity.GrishaTitanEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * O DAOT renderiza a barra Attack para TODAS as subclasses de AttackTitanEntity.
 * Quando a montaria e o Krugger, substituimos a barra original, em vez de
 * somente bloquear as habilidades (o que deixaria icones incorretos visiveis).
 *
 * Ponto de injecao conferido no DAOT fornecido (jar com Loom 1.14.10).
 */
@Mixin(targets = "daot.ShifterAbilityHUD", remap = false)
public abstract class KruggerHudMixin {
    @Inject(
        method = "lambda$register$0(Lnet/minecraft/class_332;Lnet/minecraft/class_9779;)V",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private static void summeraot$drawOwnHud(DrawContext context, RenderTickCounter tickCounter,
                                             CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        Object mount = client.player.getVehicle();
        if (!(mount instanceof KrugerTitanEntity) && !(mount instanceof GrishaTitanEntity)) {
            return;
        }
        ci.cancel();
        boolean dismounting = mount instanceof KrugerTitanEntity krugger
                ? krugger.isDismounting() : ((GrishaTitanEntity) mount).isDismounting();
        if (!client.options.hudHidden && !dismounting) {
            KruggerAbilityHud.render(context);
        }
    }
}
