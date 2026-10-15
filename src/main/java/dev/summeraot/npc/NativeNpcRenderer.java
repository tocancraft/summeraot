package dev.summeraot.npc;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.util.Identifier;

/** UV de jogador: as imagens de 64x64 nao usam UV de aldeao vanilla. */
public final class NativeNpcRenderer extends MobEntityRenderer<NativeShifterVillagerEntity, PlayerEntityModel<NativeShifterVillagerEntity>> {
    private final Identifier texture;

    public NativeNpcRenderer(EntityRendererFactory.Context context, NativeNpcVariant variant) {
        super(context, new PlayerEntityModel<>(context.getPart(EntityModelLayers.PLAYER), false), 0.5f);
        texture = variant.skin();
    }

    @Override
    public Identifier getTexture(NativeShifterVillagerEntity entity) {
        return texture;
    }
}
