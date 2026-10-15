package dev.summeraot;

import net.fabricmc.api.ModInitializer;
import dev.summeraot.network.OmniKruggerNetwork;
import dev.summeraot.grisha.GrishaEntities;
import dev.summeraot.grisha.GrishaCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SummerAOTMod implements ModInitializer {
    public static final String MOD_ID = "summeraot";
    public static final Logger LOGGER = LoggerFactory.getLogger("SummerAOT");

    @Override
    public void onInitialize() {
        SummerAOTEntities.register();
        GrishaEntities.initialize();
        OmniKruggerNetwork.register();
        KrugerCommands.register();
        GrishaCommands.register();
        LOGGER.info("SummerAOT 0.0.4: Krugger e Grisha registrados como poderes independentes.");
    }
}
