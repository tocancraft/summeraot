package dev.summeraot.grisha.client;

import daot.AttackTitanEyeRenderer;
import daot.AttackTitanGrabRenderer;
import daot.AttackTitanNapeRenderer;
import dev.summeraot.grisha.GrishaEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

/** Entrypoint client-only da mesma mod SummerAOT. */
public final class GrishaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(GrishaEntities.GRISHA_TITAN, GrishaTitanRenderer::new);
        EntityRendererRegistry.register(GrishaEntities.GRISHA_NAPE, AttackTitanNapeRenderer::new);
        EntityRendererRegistry.register(GrishaEntities.GRISHA_EYE, AttackTitanEyeRenderer::new);
        EntityRendererRegistry.register(GrishaEntities.GRISHA_GRAB, AttackTitanGrabRenderer::new);
    }
}
