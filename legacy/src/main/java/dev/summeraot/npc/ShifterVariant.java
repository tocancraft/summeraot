package dev.summeraot.npc;

import dev.summeraot.SummerAOTEntities;
import dev.summeraot.armin.ArminColossalEntities;
import dev.summeraot.grisha.GrishaEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

/** Identidades independentes: nao altera TitanPowerType do DAOT. */
public enum ShifterVariant {
    KRUGGER("krugger", "Eren Krugger", "krugger_villager"),
    GRISHA("grisha", "Grisha Yeager", "grisha_villager"),
    ARMIN("armin", "Armin Arlert", "armin_villager");

    public final String key;
    public final String label;
    public final String villagerPath;

    ShifterVariant(String key, String label, String villagerPath) {
        this.key = key;
        this.label = label;
        this.villagerPath = villagerPath;
    }

    public Identifier skin() {
        return Identifier.of("summeraot", "textures/entity/npc/" + key + ".png");
    }

    public EntityType<? extends HostileEntity> titanType() {
        return switch (this) {
            case KRUGGER -> SummerAOTEntities.KRUGER;
            case GRISHA -> GrishaEntities.GRISHA_TITAN;
            case ARMIN -> ArminColossalEntities.ARMIN_COLOSSAL;
        };
    }

    public HostileEntity newTitan(ServerWorld world) {
        return titanType().create(world);
    }

    public static ShifterVariant fromVillagerType(EntityType<?> type) {
        if (type == SummerNpcRegistry.KRUGGER_VILLAGER) return KRUGGER;
        if (type == SummerNpcRegistry.GRISHA_VILLAGER) return GRISHA;
        if (type == SummerNpcRegistry.ARMIN_VILLAGER) return ARMIN;
        return null;
    }

    public static ShifterVariant fromTitanType(EntityType<?> type) {
        if (type == SummerAOTEntities.KRUGER) return KRUGGER;
        if (type == GrishaEntities.GRISHA_TITAN) return GRISHA;
        if (type == ArminColossalEntities.ARMIN_COLOSSAL) return ARMIN;
        return null;
    }
}
