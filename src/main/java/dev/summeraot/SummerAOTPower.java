package dev.summeraot;

import net.minecraft.server.network.ServerPlayerEntity;
import dev.summeraot.network.OmniKruggerNetwork;

/**
 * Estado de Krugger separado dos oito TitanPowerType nativos do DAOT.
 * Tag de posse e tag de seleção são persistidas no jogador.
 * Um jogador pode possuir Attack e Krugger simultaneamente.
 */
public final class SummerAOTPower {
    public static final String KRUGGER_TAG = "summeraot.power.krugger";
    public static final String KRUGGER_ACTIVE_TAG = "summeraot.selection.krugger";

    private SummerAOTPower() {}

    public static boolean hasKrugger(ServerPlayerEntity player) {
        return player.getCommandTags().contains(KRUGGER_TAG);
    }

    public static boolean isKruggerSelected(ServerPlayerEntity player) {
        return hasKrugger(player) && player.getCommandTags().contains(KRUGGER_ACTIVE_TAG);
    }

    public static boolean grantKrugger(ServerPlayerEntity player) {
        boolean granted = player.addCommandTag(KRUGGER_TAG);
        // Conceder nao implica selecionar: a escolha pertence ao OmniShifter.
        // Quem ja tinha Krugger selecionado em uma versao anterior mantem sua escolha.
        OmniKruggerNetwork.sync(player);
        return granted;
    }

    public static boolean revokeKrugger(ServerPlayerEntity player) {
        player.removeCommandTag(KRUGGER_ACTIVE_TAG);
        boolean changed = player.removeCommandTag(KRUGGER_TAG);
        OmniKruggerNetwork.sync(player);
        return changed;
    }

    public static boolean selectKrugger(ServerPlayerEntity player) {
        boolean changed = hasKrugger(player) && player.addCommandTag(KRUGGER_ACTIVE_TAG);
        OmniKruggerNetwork.sync(player);
        return changed;
    }

    public static boolean selectVanilla(ServerPlayerEntity player) {
        boolean changed = player.removeCommandTag(KRUGGER_ACTIVE_TAG);
        OmniKruggerNetwork.sync(player);
        return changed;
    }
}
