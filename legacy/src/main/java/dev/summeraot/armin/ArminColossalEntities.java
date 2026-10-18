package dev.summeraot.armin;

import daot.ColossalTitanEntity;
import dev.summeraot.armin.entity.ArminColossalTitanEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/** Entidade nova: preserva os hitboxes nativos da classe ColossalTitanEntity. */
public final class ArminColossalEntities {
    public static final EntityType<ArminColossalTitanEntity> ARMIN_COLOSSAL = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier.of("summeraot", "armin_colossal_titan"),
        EntityType.Builder.create(ArminColossalTitanEntity::new, SpawnGroup.MISC)
            // Exatamente a escala registrada no SpringAOT 3.5.2: 16 x 60 blocos.
            .dimensions(16.0f, 60.0f)
            .maxTrackingRange(160) // equivalente a 10 chunks do SpringAOT.
            .trackingTickInterval(1)
            .build("summeraot:armin_colossal_titan")
    );

    private ArminColossalEntities() {}

    public static void initialize() {
        FabricDefaultAttributeRegistry.register(ARMIN_COLOSSAL, ColossalTitanEntity.createAttributes());
    }
}
