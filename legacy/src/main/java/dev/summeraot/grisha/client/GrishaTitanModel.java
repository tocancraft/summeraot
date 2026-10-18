package dev.summeraot.grisha.client;

import dev.summeraot.grisha.entity.GrishaTitanEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

/** Os 37 clipes originais de Attack4Titan do SpringAOT. */
public final class GrishaTitanModel extends GeoModel<GrishaTitanEntity> {
    private static final Identifier MODEL = Identifier.of("summeraot", "geo/grisha_bestial_tom_pele_referencia_v14.geo.json");
    private static final Identifier TEXTURE = Identifier.of("summeraot", "textures/entity/grisha_bestial_tom_pele_referencia_v14.png");
    private static final Identifier ANIMATIONS = Identifier.of("summeraot", "animations/attack4titan.animation.json");

    @Override
    public Identifier getModelResource(GrishaTitanEntity entity) {
        return MODEL;
    }

    @Override
    public Identifier getTextureResource(GrishaTitanEntity entity) {
        return TEXTURE;
    }

    @Override
    public Identifier getAnimationResource(GrishaTitanEntity entity) {
        return ANIMATIONS;
    }
}
