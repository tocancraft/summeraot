package dev.summeraot;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SummerAOTMod implements ModInitializer {
    public static final String MOD_ID = "summeraot";
    public static final Logger LOGGER = LoggerFactory.getLogger("SummerAOT");

    @Override
    public void onInitialize() {
        SummerAOTEntities.register();
        KrugerCommands.register();
        LOGGER.info("SummerAOT: Kruger registrou entidades e seleção de variante.");
    }
}
