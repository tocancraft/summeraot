package dev.summeraot.armin.client;

import dev.summeraot.armin.entity.ArminColossalTitanEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

/** Modelo, textura e 12 animacoes originais do Armin + arm_drag do DAOT 2.5.0 no SpringAOT 3.5.2. */
public final class ArminColossalTitanModel extends GeoModel<ArminColossalTitanEntity> {
    private static final Identifier MODEL = Identifier.of("summeraot", "geo/armincolossal.geo.json");
    private static final Identifier TEXTURE = Identifier.of("summeraot", "textures/entity/armincolossal.png");
    private static final Identifier ANIMATIONS = Identifier.of("summeraot", "animations/armincolossal.animation.json");

    @Override public Identifier getModelResource(ArminColossalTitanEntity entity) { return MODEL; }
    @Override public Identifier getTextureResource(ArminColossalTitanEntity entity) { return TEXTURE; }
    @Override public Identifier getAnimationResource(ArminColossalTitanEntity entity) { return ANIMATIONS; }
}
