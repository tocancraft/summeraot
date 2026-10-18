package dev.summeraot.grisha;

import net.minecraft.util.Identifier;

/**
 * Pontos de integracao com o seletor/persistencia ja existentes no SummerAOT.
 * Esta classe define as chaves; o codigo de selecao do Alpha 7 deve usa-las.
 */
public final class GrishaPowerIds {
    public static final String POWER_TAG = "grisha";
    public static final boolean ALLOW_HARDENING = false;
    public static final boolean ALLOW_BERSERKER = false;
    public static final Identifier POWER_ID = Identifier.of("summeraot", "grisha");
    public static final Identifier SELECT_CHANNEL = Identifier.of("summeraot", "select_grisha");
    public static final Identifier TITAN_ENTITY = Identifier.of("summeraot", "grisha_titan");

    private GrishaPowerIds() {}
}
