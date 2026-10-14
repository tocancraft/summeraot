package dev.summeraot.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import dev.summeraot.SummerAOTMod;
import dev.summeraot.SummerAOTPower;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

/**
 * Acrescenta uma opcao REAL na arvore Brigadier do /daot, incluindo Tab.
 *
 * O DAOT define /daot shifter set/remove <targets> <type> e uma lista
 * fixa de sugestoes. Injetar somente em setShifter/removeShifter nao
 * acrescenta "krugger" ao autocompletar; criar um literal como irmao
 * do argumento <type> corrige isso sem alterar o registro de poderes
 * originais do Danny's AoT.
 *
 * A arvore e estendida ao final do registro original. Preserva as
 * verificacoes de permissao do no /daot shifter e nao concede 'attack'.
 */
@Mixin(targets = "daot.ModCommands", remap = false)
public abstract class DaotShifterCommandMixin {

    @Inject(method = "register", at = @At("TAIL"), remap = false)
    private static void summeraot$registerKruggerInDaot(
            CommandDispatcher<ServerCommandSource> dispatcher, CallbackInfo ci) {

        CommandNode<ServerCommandSource> daot = dispatcher.getRoot().getChild("daot");
        CommandNode<ServerCommandSource> shifter = child(daot, "shifter");
        CommandNode<ServerCommandSource> setTargets = child(child(shifter, "set"), "targets");
        CommandNode<ServerCommandSource> removeTargets = child(child(shifter, "remove"), "targets");

        if (setTargets == null || removeTargets == null) {
            SummerAOTMod.LOGGER.error(
                    "SummerAOT: estrutura /daot shifter alterada; nao foi possivel registrar krugger.");
            return;
        }

        if (setTargets.getChild("krugger") == null) {
            setTargets.addChild(CommandManager.literal("krugger")
                    .executes(DaotShifterCommandMixin::grant)
                    .build());
        }
        if (removeTargets.getChild("krugger") == null) {
            removeTargets.addChild(CommandManager.literal("krugger")
                    .executes(DaotShifterCommandMixin::revoke)
                    .build());
        }

        SummerAOTMod.LOGGER.info(
                "SummerAOT: krugger registrado em /daot shifter set/remove (com autocompletar).");
    }

    private static CommandNode<ServerCommandSource> child(
            CommandNode<ServerCommandSource> parent, String name) {
        return parent == null ? null : parent.getChild(name);
    }

    private static int grant(CommandContext<ServerCommandSource> context)
            throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "targets");
        int changed = 0;
        for (ServerPlayerEntity player : targets) {
            if (SummerAOTPower.grantKrugger(player)) {
                changed++;
            }
        }
        int result = changed;
        context.getSource().sendFeedback(() -> Text.literal(
                "SummerAOT: Krugger Titan concedido a " + result + " jogador(es)."), true);
        return changed;
    }

    private static int revoke(CommandContext<ServerCommandSource> context)
            throws CommandSyntaxException {
        Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(context, "targets");
        int changed = 0;
        for (ServerPlayerEntity player : targets) {
            if (SummerAOTPower.revokeKrugger(player)) {
                changed++;
            }
        }
        int result = changed;
        context.getSource().sendFeedback(() -> Text.literal(
                "SummerAOT: Krugger Titan removido de " + result + " jogador(es)."), true);
        return changed;
    }
}
