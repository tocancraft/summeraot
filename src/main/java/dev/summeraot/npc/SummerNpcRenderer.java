package dev.summeraot.npc;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;

/** Modelo PLAYER: as skins 64x64 nao sao UV de um aldeao vanilla. */
public final class SummerNpcRenderer extends MobEntityRenderer<ShifterVillagerEntity, PlayerEntityModel<ShifterVillagerEntity>> {
    private final Identifier texture;

    public SummerNpcRenderer(EntityRendererFactory.Context context, ShifterVariant variant) {
        super(context, new PlayerEntityModel<ShifterVillagerEntity>(context.getPart(EntityModelLayers.PLAYER), false), 0.5f);
        this.texture = variant.skin();
    }

    @Override
    public Identifier getTexture(ShifterVillagerEntity entity) {
        return texture;
    }
}
