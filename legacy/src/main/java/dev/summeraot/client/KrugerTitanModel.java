package dev.summeraot.client;

import dev.summeraot.entity.KrugerTitanEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

/** A mesma geometria e 37 sequências GeckoLib do SpringAOT, novo namespace. */
public final class KrugerTitanModel extends GeoModel<KrugerTitanEntity> {
    private static final Identifier MODEL = Identifier.of("summeraot", "geo/attack3titan.geo.json");
    private static final Identifier TEXTURE = Identifier.of("summeraot", "textures/entity/krugger_titan.png");
    private static final Identifier ANIMATION = Identifier.of("summeraot", "animations/attack3titan.animation.json");

    @Override
    public Identifier getModelResource(KrugerTitanEntity entity) { return MODEL; }
    @Override
    public Identifier getTextureResource(KrugerTitanEntity entity) { return TEXTURE; }
    @Override
    public Identifier getAnimationResource(KrugerTitanEntity entity) { return ANIMATION; }
}
