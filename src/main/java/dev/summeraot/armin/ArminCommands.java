package dev.summeraot.armin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.summeraot.SummerAOTPower;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import java.util.Collection;

/** Comandos administrativos e de diagnostico. O shift permanece no DAOT nativo. */
public final class ArminCommands {
    private ArminCommands() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, environment) -> {
            dispatcher.register(CommandManager.literal("summeraot")
                .then(CommandManager.literal("armin")
                    .then(CommandManager.literal("status").executes(ctx -> status(ctx.getSource())))
                    .then(CommandManager.literal("select").executes(ctx -> select(ctx.getSource())))
                    .then(CommandManager.literal("grant").requires(src -> src.hasPermissionLevel(2))
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                            .executes(ctx -> grant(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "player")))))
                    .then(CommandManager.literal("revoke").requires(src -> src.hasPermissionLevel(2))
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                            .executes(ctx -> revoke(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "player")))))));
            registerDaot(dispatcher);
        });
    }

    private static void registerDaot(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("daot")
            .then(CommandManager.literal("shifter").requires(src -> src.hasPermissionLevel(2))
                .then(CommandManager.literal("set")
                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .then(CommandManager.literal("armin").executes(ctx -> {
                            Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(ctx, "targets");
                            int count = 0;
                            for (ServerPlayerEntity p : targets) if (SummerAOTPower.grantArmin(p)) count++;
                            return count;
                        }))))
                .then(CommandManager.literal("remove")
                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .then(CommandManager.literal("armin").executes(ctx -> {
                            Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(ctx, "targets");
                            int count = 0;
                            for (ServerPlayerEntity p : targets) if (SummerAOTPower.revokeArmin(p)) count++;
                            return count;
                        }))))));
    }

    private static int status(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity p = source.getPlayerOrThrow();
        source.sendFeedback(() -> Text.literal("SummerAOT Armin: "
            + (SummerAOTPower.hasArmin(p) ? "concedido" : "nao concedido")
            + ", selecionado=" + SummerAOTPower.isArminSelected(p)), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int select(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity p = source.getPlayerOrThrow();
        if (!SummerAOTPower.hasArmin(p) || !p.isAlive() || p.isSpectator() || p.hasVehicle()) {
            source.sendError(Text.literal("Armin indisponivel para selecao."));
            return 0;
        }
        SummerAOTPower.selectArmin(p);
        source.sendFeedback(() -> Text.literal("Colossal do Armin selecionado. Use a tecla nativa de transformacao do DAOT."), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int grant(ServerCommandSource source, ServerPlayerEntity target) {
        boolean changed = SummerAOTPower.grantArmin(target);
        source.sendFeedback(() -> Text.literal("Armin " + (changed ? "concedido" : "ja concedido")
            + ": " + target.getName().getString()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int revoke(ServerCommandSource source, ServerPlayerEntity target) {
        boolean changed = SummerAOTPower.revokeArmin(target);
        source.sendFeedback(() -> Text.literal("Armin " + (changed ? "removido" : "nao presente")
            + ": " + target.getName().getString()), true);
        return Command.SINGLE_SUCCESS;
    }
}
