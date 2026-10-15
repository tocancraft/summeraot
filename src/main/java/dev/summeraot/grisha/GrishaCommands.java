package dev.summeraot.grisha;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import daot.HandcuffsTracker;
import dev.summeraot.SummerAOTPower;
import dev.summeraot.grisha.entity.GrishaTitanEntity;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import java.util.Collection;

/** Same independent command lifecycle as Krugger, no DAOT attack tag. */
public final class GrishaCommands {
    private GrishaCommands() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registry, environment) -> {
            dispatcher.register(CommandManager.literal("summeraot")
                .then(CommandManager.literal("grisha")
                    .then(CommandManager.literal("status").executes(ctx -> status(ctx.getSource())))
                    .then(CommandManager.literal("select").executes(ctx -> select(ctx.getSource())))
                    .then(CommandManager.literal("shift").executes(ctx -> shift(ctx.getSource())))
                    .then(CommandManager.literal("grant").requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                            .executes(ctx -> grant(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "player")))))
                    .then(CommandManager.literal("revoke").requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                            .executes(ctx -> revoke(ctx.getSource(), EntityArgumentType.getPlayer(ctx, "player")))))
                    .then(CommandManager.literal("summon").requires(source -> source.hasPermissionLevel(2))
                        .executes(ctx -> summon(ctx.getSource())))));
            registerDaot(dispatcher);
        });
    }

    private static void registerDaot(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("daot")
            .then(CommandManager.literal("shifter")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("set")
                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .then(CommandManager.literal("grisha").executes(ctx -> {
                            Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(ctx, "targets");
                            int count = 0;
                            for (ServerPlayerEntity p : targets) if (SummerAOTPower.grantGrisha(p)) count++;
                            return count;
                        }))))
                .then(CommandManager.literal("remove")
                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .then(CommandManager.literal("grisha").executes(ctx -> {
                            Collection<ServerPlayerEntity> targets = EntityArgumentType.getPlayers(ctx, "targets");
                            int count = 0;
                            for (ServerPlayerEntity p : targets) if (SummerAOTPower.revokeGrisha(p)) count++;
                            return count;
                        }))))));
    }

    private static int status(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity p = source.getPlayerOrThrow();
        source.sendFeedback(() -> Text.literal("SummerAOT Grisha: "
            + (SummerAOTPower.hasGrisha(p) ? "concedido" : "nao concedido")
            + ", selecionado=" + SummerAOTPower.isGrishaSelected(p)), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int select(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity p = source.getPlayerOrThrow();
        if (!SummerAOTPower.hasGrisha(p) || !p.isAlive() || p.isSpectator() || p.hasVehicle()) {
            source.sendError(Text.literal("Grisha indisponivel para selecao."));
            return 0;
        }
        SummerAOTPower.selectGrisha(p);
        source.sendFeedback(() -> Text.literal("Grisha Titan selecionado."), false);
        return Command.SINGLE_SUCCESS;
    }
    private static int grant(ServerCommandSource source, ServerPlayerEntity target) {
        boolean changed = SummerAOTPower.grantGrisha(target);
        source.sendFeedback(() -> Text.literal("Grisha " + (changed ? "concedido" : "ja concedido")
            + ": " + target.getName().getString()), true);
        return Command.SINGLE_SUCCESS;
    }
    private static int revoke(ServerCommandSource source, ServerPlayerEntity target) {
        boolean changed = SummerAOTPower.revokeGrisha(target);
        source.sendFeedback(() -> Text.literal("Grisha " + (changed ? "removido" : "nao presente")
            + ": " + target.getName().getString()), true);
        return Command.SINGLE_SUCCESS;
    }
    private static int shift(ServerCommandSource source) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayerOrThrow();
        if (!SummerAOTPower.hasGrisha(player) || !player.isAlive() || player.isSpectator()
                || player.hasVehicle() || HandcuffsTracker.isCuffed(player.getUuid())) {
            source.sendError(Text.literal("Nao e possivel transformar em Grisha neste estado."));
            return 0;
        }
        GrishaTitanEntity titan = GrishaEntities.GRISHA_TITAN.create(player.getServerWorld());
        if (titan == null) return 0;
        titan.refreshPositionAndAngles(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
        titan.setHardeningMode(0);
        if (!player.getServerWorld().spawnEntity(titan)) return 0;
        titan.spawnHitboxes();
        titan.onPlayerShift(player);
        return player.getVehicle() == titan ? Command.SINGLE_SUCCESS : 0;
    }
    private static int summon(ServerCommandSource source) {
        ServerWorld world = source.getWorld();
        GrishaTitanEntity titan = GrishaEntities.GRISHA_TITAN.create(world);
        if (titan == null) return 0;
        titan.refreshPositionAndAngles(source.getPosition().x, source.getPosition().y,
            source.getPosition().z, source.getRotation().y, 0.0f);
        if (!world.spawnEntity(titan)) return 0;
        titan.spawnHitboxes();
        return Command.SINGLE_SUCCESS;
    }
}
