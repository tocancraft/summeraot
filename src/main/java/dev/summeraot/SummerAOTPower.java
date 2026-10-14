package dev.summeraot;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Poder proprio do SummerAOT. Nao atribui a tag 'attack' do DAOT.
 * A tag Minecraft persiste com os dados do jogador entre sessoes.
 * Concessao/revogacao sao restritas a operadores nos comandos do addon.
 */
public final class SummerAOTPower {
    public static final String KRUGGER_TAG = "summeraot.power.krugger";

    private SummerAOTPower() {}

    public static boolean hasKrugger(ServerPlayerEntity player) {
        return player.getCommandTags().contains(KRUGGER_TAG);
    }

    public static boolean grantKrugger(ServerPlayerEntity player) {
        return player.addCommandTag(KRUGGER_TAG);
    }

    public static boolean revokeKrugger(ServerPlayerEntity player) {
        return player.removeCommandTag(KRUGGER_TAG);
    }
}
