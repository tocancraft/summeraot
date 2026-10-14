package dev.summeraot;

import daot.AttackTitanEntity;
import daot.AttackTitanEyeEntity;
import daot.AttackTitanGrabEntity;
import daot.AttackTitanNapeEntity;
import dev.summeraot.entity.KrugerEyeEntity;
import dev.summeraot.entity.KrugerGrabEntity;
import dev.summeraot.entity.KrugerNapeEntity;
import dev.summeraot.entity.KrugerTitanEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class SummerAOTEntities {
    private SummerAOTEntities() {}

    // Dimensões extraídas de SpringDAOTEntities (3x15, 1x1, 1x1, 2x2).
    public static final EntityType<KrugerTitanEntity> KRUGER = Registry.register(
        Registries.ENTITY_TYPE, Identifier.of(SummerAOTMod.MOD_ID, "krugger_titan"),
        EntityType.Builder.create(KrugerTitanEntity::new, SpawnGroup.MISC)
            .dimensions(3.0f, 15.0f).eyeHeight(14.0f)
            .maxTrackingRange(160).trackingTickInterval(1)
            .build("summeraot:krugger_titan")
    );
    public static final EntityType<KrugerNapeEntity> KRUGER_NAPE = Registry.register(
        Registries.ENTITY_TYPE, Identifier.of(SummerAOTMod.MOD_ID, "krugger_titan_nape"),
        EntityType.Builder.create(KrugerNapeEntity::new, SpawnGroup.MISC)
            .dimensions(1.0f, 1.0f).maxTrackingRange(160).trackingTickInterval(1)
            .build("summeraot:krugger_titan_nape")
    );
    public static final EntityType<KrugerEyeEntity> KRUGER_EYE = Registry.register(
        Registries.ENTITY_TYPE, Identifier.of(SummerAOTMod.MOD_ID, "krugger_titan_eye"),
        EntityType.Builder.create(KrugerEyeEntity::new, SpawnGroup.MISC)
            .dimensions(1.0f, 1.0f).maxTrackingRange(160).trackingTickInterval(1)
            .build("summeraot:krugger_titan_eye")
    );
    public static final EntityType<KrugerGrabEntity> KRUGER_GRAB = Registry.register(
        Registries.ENTITY_TYPE, Identifier.of(SummerAOTMod.MOD_ID, "krugger_titan_grab"),
        EntityType.Builder.create(KrugerGrabEntity::new, SpawnGroup.MISC)
            .dimensions(2.0f, 2.0f).maxTrackingRange(160).trackingTickInterval(1)
            .build("summeraot:krugger_titan_grab")
    );

    public static void register() {
        FabricDefaultAttributeRegistry.register(KRUGER, AttackTitanEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(KRUGER_NAPE, AttackTitanNapeEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(KRUGER_EYE, AttackTitanEyeEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(KRUGER_GRAB, AttackTitanGrabEntity.createAttributes());
    }
}
