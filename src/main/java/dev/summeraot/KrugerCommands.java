package dev.summeraot;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import daot.HandcuffsTracker;
import dev.summeraot.entity.KrugerTitanEntity;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import java.util.Collection;
import net.minecraft.text.Text;

/**
 * Comandos da primeira versao independente do Krugger.
 * Os poderes NAO exigem nem concedem a tag 'attack' do DAOT.
 * grant/revoke/summon exigem operador; shift exige posse do poder SummerAOT.
 */
public final class KrugerCommands {
    private static CommandDispatcher<ServerCommandSource> activeDispatcher;

    private KrugerCommands() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            activeDispatcher = dispatcher;
            dispatcher.register(CommandManager.literal("summeraot")
                .then(CommandManager.literal("krugger")
                    .then(CommandManager.literal("status")
                        .executes(ctx -> status(ctx.getSource())))
                    .then(CommandManager.literal("daotcheck")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(ctx -> daotCheck(ctx.getSource())))
                    .then(CommandManager.literal("shift")
                        .executes(ctx -> shift(ctx.getSource())))
                    .then(CommandManager.literal("grant")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                            .executes(ctx -> grant(ctx.getSource(),
                                EntityArgumentType.getPlayer(ctx, "player")))))
                    .then(CommandManager.literal("revoke")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                            .executes(ctx -> revoke(ctx.getSource(),
                                EntityArgumentType.getPlayer(ctx, "player")))))
                    .then(CommandManager.literal("summon")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(ctx -> summon(ctx.getSource())))));
            // Fabric callback: registration does not depend on intercepting DAOT bytecode.
            // Brigadier merges these nodes with DAOT's existing root and subcommands.
            registerDaotKrugger(dispatcher);
        });
    }

    private static void registerDaotKrugger(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("daot")
            .then(CommandManager.literal("shifter")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("set")
                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .then(CommandManager.literal("krugger")
                            .executes(ctx -> daotGrant(ctx)))))
                .then(CommandManager.literal("remove")
                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .then(CommandManager.literal("krugger")
                            .executes(ctx -> daotRevoke(ctx)))))));
        boolean registered = isDaotKruggerAvailable(dispatcher, "set")
            && isDaotKruggerAvailable(dispatcher, "remove");
        SummerAOTMod.LOGGER.info("SummerAOT: DAOT bridge registered={}, set={}, remove={}",
            registered, isDaotKruggerAvailable(dispatcher, "set"),
            isDaotKruggerAvailable(dispatcher, "remove"));
    }

    private static boolean isDaotKruggerAvailable(
            CommandDispatcher<ServerCommandSource> dispatcher, String action) {
        if (dispatcher == null) { return false; }
        CommandNode<ServerCommandSource> root = dispatcher.getRoot().getChild("daot");
        CommandNode<ServerCommandSource> shifter = root == null ? null : root.getChild("shifter");
        CommandNode<ServerCommandSource> operation = shifter == null ? null : shifter.getChild(action);
        CommandNode<ServerCommandSource> targets = operation == null ? null : operation.getChild("targets");
        return targets != null && targets.getChild("krugger") != null;
    }

    private static int daotCheck(ServerCommandSource source) {
        boolean set = isDaotKruggerAvailable(activeDispatcher, "set");
        boolean remove = isDaotKruggerAvailable(activeDispatcher, "remove");
        source.sendFeedback(() -> Text.literal(
            "SummerAOT /daot: set=" + set + ", remove=" + remove
                + ". Se ambos forem true e o comando falhar, envie latest.log e o erro do chat."), false);
        return (set && remove) ? 1 : 0;
    }

    private static int daotGrant(CommandContext<ServerCommandSource> ctx)
            throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "targets");
        int changed = 0;
        for (ServerPlayerEntity player : players) {
            if (SummerAOTPower.grantKrugger(player)) { changed++; }
        }
        int count = changed;
        ctx.getSource().sendFeedback(() -> Text.literal(
            "SummerAOT: Krugger Titan concedido a " + count + " jogador(es)."), true);
        return changed;
    }

    private static int daotRevoke(CommandContext<ServerCommandSource> ctx)
            throws CommandSyntaxException {
        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(ctx, "targets");
        int changed = 0;
        for (ServerPlayerEntity player : players) {
            if (SummerAOTPower.revokeKrugger(player)) { changed++; }
        }
        int count = changed;
        ctx.getSource().sendFeedback(() -> Text.literal(
            "SummerAOT: Krugger Titan removido de " + count + " jogador(es)."), true);
        return changed;
    }

    private static int status(ServerCommandSource source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        boolean owns = SummerAOTPower.hasKrugger(player);
        source.sendFeedback(() -> Text.literal(
            "SummerAOT Krugger Titan: " + (owns ? "poder ativo" : "poder nao concedido")), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int grant(ServerCommandSource source, ServerPlayerEntity target) {
        boolean added = SummerAOTPower.grantKrugger(target);
        source.sendFeedback(() -> Text.literal("SummerAOT: poder Krugger " +
            (added ? "concedido a " : "ja presente em ") + target.getName().getString()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int revoke(ServerCommandSource source, ServerPlayerEntity target) {
        boolean removed = SummerAOTPower.revokeKrugger(target);
        source.sendFeedback(() -> Text.literal("SummerAOT: poder Krugger " +
            (removed ? "revogado de " : "nao estava presente em ") + target.getName().getString()), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int shift(ServerCommandSource source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        if (!SummerAOTPower.hasKrugger(player)) {
            source.sendError(Text.literal("Voce nao possui o poder Krugger do SummerAOT."));
            return 0;
        }
        if (!player.isAlive() || player.isSpectator() || player.hasVehicle()) {
            source.sendError(Text.literal("Nao e possivel se transformar neste estado."));
            return 0;
        }
        if (HandcuffsTracker.isCuffed(player.getUuid())) {
            source.sendError(Text.literal("Nao e possivel se transformar enquanto estiver algemado."));
            return 0;
        }
        ServerWorld world = player.getServerWorld();
        KrugerTitanEntity titan = SummerAOTEntities.KRUGER.create(world);
        if (titan == null) {
            source.sendError(Text.literal("Falha ao criar a entidade Krugger."));
            return 0;
        }
        titan.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
        titan.setHardeningMode(0);
        if (!world.spawnEntity(titan)) {
            source.sendError(Text.literal("Nao foi possivel invocar o Krugger Titan."));
            return 0;
        }
        titan.spawnHitboxes();
        // Usa a inicializacao de shifter do DAOT, mas somente APOS a verificacao
        // do nosso poder independente. Nao altera a flag 'attack' do DAOT.
        titan.onPlayerShift(player);
        if (player.getVehicle() != titan) {
            source.sendError(Text.literal("Krugger criado, mas o jogador nao conseguiu montar. Verifique latest.log."));
            return 0;
        }
        source.sendFeedback(() -> Text.literal("Voce se transformou no Krugger Titan."), false);
        return Command.SINGLE_SUCCESS;
    }

    private static int summon(ServerCommandSource source) {
        ServerWorld world = source.getWorld();
        KrugerTitanEntity titan = SummerAOTEntities.KRUGER.create(world);
        if (titan == null) { return 0; }
        titan.refreshPositionAndAngles(source.getPosition().x, source.getPosition().y,
            source.getPosition().z, source.getRotation().y, 0.0f);
        if (!world.spawnEntity(titan)) { return 0; }
        titan.spawnHitboxes();
        source.sendFeedback(() -> Text.literal("Krugger Titan invocado para teste."), true);
        return Command.SINGLE_SUCCESS;
    }
}
