package dev.summeraot.client;

import daot.AttackTitanEyeRenderer;
import daot.AttackTitanGrabRenderer;
import daot.AttackTitanNapeRenderer;
import dev.summeraot.SummerAOTEntities;
import dev.summeraot.entity.KrugerEyeEntity;
import dev.summeraot.entity.KrugerGrabEntity;
import dev.summeraot.entity.KrugerNapeEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.EntityRenderer;

/**
 * Renderizadores das hitboxes do Danny operam sobre as nossas subclasses.
 * A conversão de generics é necessária porque a API registra o tipo exato.
 */
@SuppressWarnings("unchecked")
public final class SummerAOTClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(SummerAOTEntities.KRUGER, KrugerTitanRenderer::new);
        EntityRendererRegistry.register(SummerAOTEntities.KRUGER_NAPE,
            context -> (EntityRenderer<KrugerNapeEntity>)(EntityRenderer<?>) new AttackTitanNapeRenderer(context));
        EntityRendererRegistry.register(SummerAOTEntities.KRUGER_EYE,
            context -> (EntityRenderer<KrugerEyeEntity>)(EntityRenderer<?>) new AttackTitanEyeRenderer(context));
        EntityRendererRegistry.register(SummerAOTEntities.KRUGER_GRAB,
            context -> (EntityRenderer<KrugerGrabEntity>)(EntityRenderer<?>) new AttackTitanGrabRenderer(context));
    }
}
