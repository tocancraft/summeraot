package dev.summeraot.armin.client;

import daot.ColossalTitanEyeEntity;
import daot.ColossalTitanNapeEntity;
import daot.TitanBoneCache;
import daot.network.HitboxBoneSyncPayload;
import dev.summeraot.armin.entity.ArminColossalTitanEntity;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.HashMap;
import java.util.Map;

/**
 * Renderer do SpringAOT portado, incluindo hitboxes animadas de nuca e olho.
 * Pacote de sincronizacao ja pertence ao DAOT. Envia no maximo uma vez por tick
 * quando o Armin esta dentro de 64 blocos do jogador local.
 */
public final class ArminColossalTitanRenderer extends GeoEntityRenderer<ArminColossalTitanEntity> {
    private static final Map<Integer, Long> LAST_SEND_TICK = new HashMap<>();

    public ArminColossalTitanRenderer(EntityRendererFactory.Context context) {
        super(context, new ArminColossalTitanModel());
        this.shadowRadius = 4.0f;
    }

    @Override
    public void render(ArminColossalTitanEntity entity, float entityYaw, float tickDelta,
                       MatrixStack matrices, VertexConsumerProvider vertices, int light) {
        super.render(entity, entityYaw, tickDelta, matrices, vertices, light);
        syncHitboxBones(entity, tickDelta);
    }

    private void syncHitboxBones(ArminColossalTitanEntity entity, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        if (client.player.squaredDistanceTo(entity) > 4096.0) return;

        long tick = entity.getWorld().getTime();
        Long last = LAST_SEND_TICK.get(entity.getId());
        if (last != null && last == tick) return;
        LAST_SEND_TICK.put(entity.getId(), tick);

        GeoBone nape = getGeoModel().getAnimationProcessor().getBone("nape_hitbox");
        GeoBone eye = getGeoModel().getAnimationProcessor().getBone("eye_hitbox");
        if (nape == null && eye == null) return;

        Vec3d origin = entity.getLerpedPos(tickDelta);
        float yaw = MathHelper.lerp(tickDelta, entity.prevBodyYaw, entity.bodyYaw);
        double nx = 0, ny = 0, nz = 0, ex = 0, ey = 0, ez = 0;

        if (nape != null) {
            Vec3d pos = TitanBoneCache.getAnimatedBoneWorldPos(nape, origin, yaw);
            nx = pos.x; ny = pos.y; nz = pos.z;
            ColossalTitanNapeEntity hitbox = ColossalTitanNapeEntity.getClientInstance(entity.getId());
            if (hitbox != null) hitbox.setRendererPosition(nx, ny, nz);
        }
        if (eye != null) {
            Vec3d pos = TitanBoneCache.getAnimatedBoneWorldPos(eye, origin, yaw);
            ex = pos.x; ey = pos.y; ez = pos.z;
            ColossalTitanEyeEntity hitbox = ColossalTitanEyeEntity.getClientInstance(entity.getId());
            if (hitbox != null) hitbox.setRendererPosition(ex, ey, ez);
        }
        ClientPlayNetworking.send(new HitboxBoneSyncPayload(entity.getId(), nx, ny, nz, ex, ey, ez));
    }
}
