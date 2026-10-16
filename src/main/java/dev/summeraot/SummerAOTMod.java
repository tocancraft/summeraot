package dev.summeraot;

import net.fabricmc.api.ModInitializer;
import dev.summeraot.network.OmniKruggerNetwork;
import dev.summeraot.network.NpcColossalShiftS2CPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import dev.summeraot.grisha.GrishaEntities;
import dev.summeraot.grisha.GrishaCommands;
import dev.summeraot.armin.ArminColossalEntities;
import dev.summeraot.armin.ArminCommands;
import dev.summeraot.npc.SummerNpcRegistry;
import dev.summeraot.npc.SummerNpcLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SummerAOTMod implements ModInitializer {
    public static final String MOD_ID = "summeraot";
    public static final Logger LOGGER = LoggerFactory.getLogger("SummerAOT");

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playS2C().register(NpcColossalShiftS2CPayload.ID,
                NpcColossalShiftS2CPayload.CODEC);
        SummerAOTEntities.register();
        GrishaEntities.initialize();
        ArminColossalEntities.initialize();
        SummerNpcRegistry.initialize();
        SummerNpcLogic.initialize();
        OmniKruggerNetwork.register();
        KrugerCommands.register();
        GrishaCommands.register();
        ArminCommands.register();
        LOGGER.info("SummerAOT 0.0.6-alpha.3: Colossal ofensivo e transformacoes NPC completas.");
    }
}
