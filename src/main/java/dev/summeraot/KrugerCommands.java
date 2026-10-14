package dev.summeraot;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.summeraot.entity.KrugerTitanEntity;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

/**
 * Seleção da variante exige que o jogador já seja Attack shifter do DAOT.
 * Comandos de teste não concedem poder nem alteram flags de autorização do DAOT.
 */
public final class KrugerCommands {
    private KrugerCommands() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("summeraot")
                .then(CommandManager.literal("kruger")
                    .then(CommandManager.literal("select").executes(ctx -> select(ctx.getSource(), true)))
                    .then(CommandManager.literal("vanilla").executes(ctx -> select(ctx.getSource(), false)))
                    .then(CommandManager.literal("summon")
                        .requires(source -> source.hasPermissionLevel(2))
                        .executes(ctx -> summon(ctx.getSource())))));
        });
    }

    private static int select(ServerCommandSource source, boolean kruger) {
        ServerPlayerEntity player;
        try { player = source.getPlayerOrThrow(); }
        catch (Exception e) { return 0; }
        if (!player.getCommandTags().contains("attack")) {
            source.sendError(Text.literal("SummerAOT: o DAOT deve reconhecer seu poder Attack primeiro."));
            return 0;
        }
        if (kruger) { player.addCommandTag("attack_3"); }
        else { player.removeCommandTag("attack_3"); }
        source.sendFeedback(() -> Text.literal(
            kruger ? "Kruger selecionado para o próximo shift Attack." : "Forma original Attack selecionada."
        ), false);
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
        source.sendFeedback(() -> Text.literal("Kruger criado para teste visual."), true);
        return Command.SINGLE_SUCCESS;
    }
}
