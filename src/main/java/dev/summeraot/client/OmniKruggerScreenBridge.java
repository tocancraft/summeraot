package dev.summeraot.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

/**
 * The embedded OmniShifter still owns packets, data, commands and key binding.
 * Only its selection screen is replaced so all 8 native powers and Krugger
 * share the same grid and the same authoritative selection presentation.
 */
public final class OmniKruggerScreenBridge {
    private static final String OMNI_SCREEN = "com.omnishifter.gui.TitanPowerSelectScreen";
    private OmniKruggerScreenBridge() {}

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(OmniKruggerScreenBridge::replaceMenu);
    }

    private static void replaceMenu(MinecraftClient client) {
        if (client.currentScreen != null
                && OMNI_SCREEN.equals(client.currentScreen.getClass().getName())) {
            client.setScreen(new SummerOmniSelectScreen());
        }
    }
}
