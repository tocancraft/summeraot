package dev.summeraot.client;

import daot.AttackTitanEyeRenderer;
import daot.ShiftParticleHelper;
import dev.summeraot.network.NpcColossalShiftS2CPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import daot.AttackTitanGrabRenderer;
import daot.AttackTitanNapeRenderer;
import dev.summeraot.SummerAOTEntities;
import dev.summeraot.npc.SummerNpcRegistry;
import dev.summeraot.npc.SummerNpcRenderer;
import dev.summeraot.npc.NativeNpcRenderer;
import dev.summeraot.npc.NativeNpcRegistry;
import dev.summeraot.npc.NativeNpcVariant;
import dev.summeraot.npc.ShifterVariant;
import dev.summeraot.grisha.GrishaEntities;
import dev.summeraot.armin.ArminColossalEntities;
import dev.summeraot.armin.client.ArminColossalTitanRenderer;
import dev.summeraot.grisha.client.GrishaTitanRenderer;
import dev.summeraot.grisha.entity.GrishaNapeEntity;
import dev.summeraot.grisha.entity.GrishaEyeEntity;
import dev.summeraot.grisha.entity.GrishaGrabEntity;
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
        dev.summeraot.SummerAOTMod.LOGGER.info("SUMMERAOT_CLIENT_VISUAL_ALPHA4_READY: Grisha canonicamente substituido");
        // Mesmo efeito nuclear Effekseer da transformacao Colossal do DAOT.
        ClientPlayNetworking.registerGlobalReceiver(NpcColossalShiftS2CPayload.ID,
                (payload, context) -> context.client().execute(() -> {
                    if (context.client().world != null) {
                        ShiftParticleHelper.spawnColossalNukeParticle(context.client().world,
                                payload.x(), payload.y(), payload.z());
                    }
                }));
        EntityRendererRegistry.register(SummerNpcRegistry.KRUGGER_VILLAGER, context -> new SummerNpcRenderer(context, ShifterVariant.KRUGGER));
        EntityRendererRegistry.register(SummerNpcRegistry.GRISHA_VILLAGER, context -> new SummerNpcRenderer(context, ShifterVariant.GRISHA));
        EntityRendererRegistry.register(SummerNpcRegistry.ARMIN_VILLAGER, context -> new SummerNpcRenderer(context, ShifterVariant.ARMIN));
        for (NativeNpcVariant variant : NativeNpcVariant.values()) {
            EntityRendererRegistry.register(NativeNpcRegistry.villagerType(variant),
                    context -> new NativeNpcRenderer(context, variant));
        }
        OmniKruggerClientState.register();
        OmniKruggerScreenBridge.register();
        EntityRendererRegistry.register(SummerAOTEntities.KRUGER, KrugerTitanRenderer::new);
        EntityRendererRegistry.register(GrishaEntities.GRISHA_TITAN, GrishaTitanRenderer::new);
        EntityRendererRegistry.register(ArminColossalEntities.ARMIN_COLOSSAL, ArminColossalTitanRenderer::new);
        EntityRendererRegistry.register(GrishaEntities.GRISHA_NAPE,
            context -> (EntityRenderer<GrishaNapeEntity>)(EntityRenderer<?>) new AttackTitanNapeRenderer(context));
        EntityRendererRegistry.register(GrishaEntities.GRISHA_EYE,
            context -> (EntityRenderer<GrishaEyeEntity>)(EntityRenderer<?>) new AttackTitanEyeRenderer(context));
        EntityRendererRegistry.register(GrishaEntities.GRISHA_GRAB,
            context -> (EntityRenderer<GrishaGrabEntity>)(EntityRenderer<?>) new AttackTitanGrabRenderer(context));
        EntityRendererRegistry.register(SummerAOTEntities.KRUGER_NAPE,
            context -> (EntityRenderer<KrugerNapeEntity>)(EntityRenderer<?>) new AttackTitanNapeRenderer(context));
        EntityRendererRegistry.register(SummerAOTEntities.KRUGER_EYE,
            context -> (EntityRenderer<KrugerEyeEntity>)(EntityRenderer<?>) new AttackTitanEyeRenderer(context));
        EntityRendererRegistry.register(SummerAOTEntities.KRUGER_GRAB,
            context -> (EntityRenderer<KrugerGrabEntity>)(EntityRenderer<?>) new AttackTitanGrabRenderer(context));
    }
}
