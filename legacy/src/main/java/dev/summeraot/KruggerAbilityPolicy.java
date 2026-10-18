package dev.summeraot;

import java.util.List;
import java.util.Locale;

/**
 * Capacidades exclusivas do Krugger Titan.
 * A heranca de AttackTitanEntity e uma reutilizacao tecnica, nao a
 * identidade do poder nem uma permissao para obter skills do Attack Titan.
 */
public final class KruggerAbilityPolicy {
    // SpringAOT TitanSkillCatalog: harden=6, berserk=9.
    public static final int HARDEN_ABILITY_ID = 6;
    public static final int BERSERK_ABILITY_ID = 9;

    // Catalogo do SUMMERAOT; nao altera os unlocks dos jogadores do DAOT.
    // heavy_punch ainda requer portabilidade funcional da versao Spring.
    private static final List<String> ALLOWED_SKILL_IDS = List.of(
        "ground_stomp", "attack_kick", "attack_protect", "heavy_punch", "grab"
    );

    private KruggerAbilityPolicy() {}

    /** Invocada na entrada de habilidade da entidade. */
    public static boolean isAllowedAbility(int abilityId) {
        return abilityId != HARDEN_ABILITY_ID && abilityId != BERSERK_ABILITY_ID;
    }

    /** Filtro para os proximos HUD, skill tree e loadout proprios do SummerAOT. */
    public static boolean isAllowedSkill(String skillId) {
        if (skillId == null || skillId.isBlank()) {
            return false;
        }
        String name = skillId.toLowerCase(Locale.ROOT);
        return ALLOWED_SKILL_IDS.contains(name);
    }

    public static List<String> availableSkills() {
        return ALLOWED_SKILL_IDS;
    }
}
