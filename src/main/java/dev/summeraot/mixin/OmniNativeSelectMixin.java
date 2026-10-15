package dev.summeraot.mixin;

import dev.summeraot.network.OmniKruggerNetwork;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;


/**
 * Listen only at the point OmniShifter 1.3.0 commits a successful native power
 * selection (after the server accepts the Omni packet). Rejected clicks skip this call.
 * No enum modifications and no direct dependency on Omni's intermediary classes.
 */
@Mixin(targets = "com.omnishifter.OmniShifter", remap = false)
public abstract class OmniNativeSelectMixin {
    @ModifyArgs(method = "lambda$onInitialize$2",
            at = @At(value = "INVOKE",
                    target = "Lcom/omnishifter/TitanPowerTracker;syncPlayer(Lnet/minecraft/class_3222;Lcom/omnishifter/data/PlayerTitanPowersState;)V"),
            remap = false)
    private static void summeraot$onNativePowerAccepted(Args args) {
        OmniKruggerNetwork.markNativeSelected(
                (net.minecraft.server.network.ServerPlayerEntity) args.get(0), args.get(1));
    }
}
