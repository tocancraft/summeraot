package dev.summeraot.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/**
 * Hotbar exclusiva do Krugger. Sem endurecimento de nuca, endurecimento das
 * maos, Berserk ou icones herdados que nao correspondam ao seu repertorio.
 *
 * IDs preservados do DAOT: 1=pisao, 2=chute, 3=defesa, 7=agarrar.
 * Nenhuma tecla e capturada: continua usando os controles do DAOT.
 */
public final class KruggerAbilityHud {
    private static final Identifier SLOT = Identifier.of("dannys-aot", "textures/gui/abilities_slot_unselected.png");
    private static final Identifier STOMP = Identifier.of("dannys-aot", "textures/gui/ability_titan_ground_stomp.png");
    private static final Identifier KICK = Identifier.of("dannys-aot", "textures/gui/ability_titan_kickback.png");
    private static final Identifier GRAB = Identifier.of("dannys-aot", "textures/gui/ability_titan_grab_female.png");

    private record Action(String key, String label, Identifier icon) {}
    private static final Action[] ACTIONS = {
        new Action("1", "PISÃO", STOMP),
        new Action("2", "CHUTE", KICK),
        new Action("3", "DEFESA", null),
        new Action("7", "AGARRAR", GRAB)
    };

    private KruggerAbilityHud() {}

    public static void render(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        int spacing = 28;
        int startX = (context.getScaledWindowWidth() - (ACTIONS.length - 1) * spacing - 22) / 2;
        int y = context.getScaledWindowHeight() - 26;

        for (int i = 0; i < ACTIONS.length; i++) {
            Action action = ACTIONS[i];
            int x = startX + i * spacing;
            context.drawTexture(SLOT, x, y, 22, 22, 0f, 0f, 48, 46, 48, 46);
            if (action.icon != null) {
                context.drawTexture(action.icon, x + 3, y + 3, 16, 16, 0f, 0f, 32, 32, 32, 32);
            } else {
                context.drawCenteredTextWithShadow(client.textRenderer, "DEF", x + 11, y + 7, 0xFFFFFF);
            }
            context.drawCenteredTextWithShadow(client.textRenderer, action.key, x + 11, y - 11, 0xFFFFFF);
        }
    }
}
