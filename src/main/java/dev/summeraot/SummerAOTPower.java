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

    public static final String GRISHA_TAG = "summeraot.power.grisha";
    public static final String GRISHA_ACTIVE_TAG = "summeraot.selection.grisha";

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
        if (!hasKrugger(player)) return false;
        boolean changed = player.addCommandTag(KRUGGER_ACTIVE_TAG);
        changed |= player.removeCommandTag(GRISHA_ACTIVE_TAG);
        OmniKruggerNetwork.sync(player);
        return changed;
    }

    public static boolean hasGrisha(ServerPlayerEntity player) {
        return player.getCommandTags().contains(GRISHA_TAG);
    }

    public static boolean isGrishaSelected(ServerPlayerEntity player) {
        return hasGrisha(player) && player.getCommandTags().contains(GRISHA_ACTIVE_TAG);
    }

    public static boolean grantGrisha(ServerPlayerEntity player) {
        boolean added = player.addCommandTag(GRISHA_TAG);
        OmniKruggerNetwork.sync(player);
        return added;
    }

    public static boolean revokeGrisha(ServerPlayerEntity player) {
        player.removeCommandTag(GRISHA_ACTIVE_TAG);
        boolean removed = player.removeCommandTag(GRISHA_TAG);
        OmniKruggerNetwork.sync(player);
        return removed;
    }

    public static boolean selectGrisha(ServerPlayerEntity player) {
        if (!hasGrisha(player)) return false;
        boolean changed = player.addCommandTag(GRISHA_ACTIVE_TAG);
        changed |= player.removeCommandTag(KRUGGER_ACTIVE_TAG);
        OmniKruggerNetwork.sync(player);
        return changed;
    }

    public static boolean selectVanilla(ServerPlayerEntity player) {
        boolean changed = player.removeCommandTag(KRUGGER_ACTIVE_TAG);
        changed |= player.removeCommandTag(GRISHA_ACTIVE_TAG);
        OmniKruggerNetwork.sync(player);
        return changed;
    }
}
