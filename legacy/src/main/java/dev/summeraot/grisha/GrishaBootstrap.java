package dev.summeraot.grisha;

import net.fabricmc.api.ModInitializer;

/** Entrada adicional da mesma mod summeraot, sem criar outro mod id. */
public final class GrishaBootstrap implements ModInitializer {
    @Override
    public void onInitialize() {
        GrishaEntities.initialize();
    }
}
