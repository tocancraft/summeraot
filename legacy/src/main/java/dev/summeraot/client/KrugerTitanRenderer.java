package dev.summeraot.client;

import daot.AttackTitanEyeEntity;
import daot.AttackTitanGrabEntity;
import daot.AttackTitanNapeEntity;
import daot.TitanBoneCache;
import dev.summeraot.entity.KrugerTitanEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

/**
 * Renderiza o modelo Spring. Reposiciona hitboxes no CLIENTE pelos bones,
 * usando a API de cache e as hitboxes que já existem no DAOT.
 * No servidor, o DAOT continua sendo responsável por fallback de posição.
 * A sincronização por pacote Spring ainda não foi portada nesta versão.
 */
public final class KrugerTitanRenderer extends GeoEntityRenderer<KrugerTitanEntity> {
    public KrugerTitanRenderer(EntityRendererFactory.Context context) {
        super(context, new KrugerTitanModel());
        this.shadowRadius = 2.0f;
    }

    @Override
    public void render(KrugerTitanEntity entity, float entityYaw, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, entityYaw, tickDelta, matrices, vertexConsumers, light);
        moveClientHitboxes(entity, tickDelta);
    }

    private void moveClientHitboxes(KrugerTitanEntity entity, float tickDelta) {
        int parent = entity.getId();
        Vec3d origin = entity.getLerpedPos(tickDelta);
        float yaw = MathHelper.lerp(tickDelta, entity.prevBodyYaw, entity.bodyYaw);
        GeoBone nape = getGeoModel().getAnimationProcessor().getBone("nape_hitbox");
        GeoBone eye = getGeoModel().getAnimationProcessor().getBone("eye_hitbox");
        GeoBone grab = getGeoModel().getAnimationProcessor().getBone("grab_hitbox");
        AttackTitanNapeEntity n = AttackTitanNapeEntity.getClientInstance(parent);
        AttackTitanEyeEntity e = AttackTitanEyeEntity.getClientInstance(parent);
        AttackTitanGrabEntity g = AttackTitanGrabEntity.getClientInstance(parent);
        if (nape != null && n != null) {
            Vec3d pos = TitanBoneCache.getAnimatedBoneWorldPos(nape, origin, yaw);
            n.setRendererPosition(pos.x, pos.y, pos.z);
        }
        if (eye != null && e != null) {
            Vec3d pos = TitanBoneCache.getAnimatedBoneWorldPos(eye, origin, yaw);
            e.setRendererPosition(pos.x, pos.y, pos.z);
        }
        if (grab != null && g != null) {
            Vec3d pos = TitanBoneCache.getAnimatedBoneWorldPos(grab, origin, yaw);
            g.setRendererPosition(pos.x, pos.y, pos.z);
        }
    }
}
