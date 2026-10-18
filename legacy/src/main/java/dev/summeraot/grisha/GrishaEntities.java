package dev.summeraot.grisha;

import daot.AttackTitanEntity;
import daot.AttackTitanNapeEntity;
import daot.AttackTitanEyeEntity;
import daot.AttackTitanGrabEntity;
import dev.summeraot.grisha.entity.GrishaTitanEntity;
import dev.summeraot.grisha.entity.GrishaNapeEntity;
import dev.summeraot.grisha.entity.GrishaEyeEntity;
import dev.summeraot.grisha.entity.GrishaGrabEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

/** Registros exclusivos do Grisha: NUNCA reutilizar o power ID "attack". */
public final class GrishaEntities {
    public static final String POWER_TAG = "grisha";

    public static final EntityType<GrishaTitanEntity> GRISHA_TITAN = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier.of("summeraot", "grisha_titan"),
        EntityType.Builder.create(GrishaTitanEntity::new, SpawnGroup.MISC)
            .dimensions(2.3f, 15.0f)
            .eyeHeight(14.0f)
            .maxTrackingRange(32)
            .trackingTickInterval(2)
            .build("summeraot:grisha_titan")
    );

    public static final EntityType<GrishaNapeEntity> GRISHA_NAPE = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier.of("summeraot", "grisha_titan_nape"),
        EntityType.Builder.create(GrishaNapeEntity::new, SpawnGroup.MISC)
            .dimensions(1.5f, 2.0f)
            .maxTrackingRange(32)
            .trackingTickInterval(2)
            .build("summeraot:grisha_titan_nape")
    );

    public static final EntityType<GrishaEyeEntity> GRISHA_EYE = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier.of("summeraot", "grisha_titan_eye"),
        EntityType.Builder.create(GrishaEyeEntity::new, SpawnGroup.MISC)
            .dimensions(1.5f, 1.2f)
            .maxTrackingRange(32)
            .trackingTickInterval(2)
            .build("summeraot:grisha_titan_eye")
    );

    public static final EntityType<GrishaGrabEntity> GRISHA_GRAB = Registry.register(
        Registries.ENTITY_TYPE,
        Identifier.of("summeraot", "grisha_titan_grab"),
        EntityType.Builder.create(GrishaGrabEntity::new, SpawnGroup.MISC)
            .dimensions(1.5f, 2.0f)
            .maxTrackingRange(32)
            .trackingTickInterval(2)
            .build("summeraot:grisha_titan_grab")
    );

    private GrishaEntities() {}

    public static void initialize() {
        FabricDefaultAttributeRegistry.register(GRISHA_TITAN, AttackTitanEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(GRISHA_NAPE, AttackTitanNapeEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(GRISHA_EYE, AttackTitanEyeEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(GRISHA_GRAB, AttackTitanGrabEntity.createAttributes());
    }
}
