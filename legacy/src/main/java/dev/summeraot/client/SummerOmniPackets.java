package dev.summeraot.client;

import daot.TitanPowerType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.packet.CustomPayload;

import java.lang.reflect.Constructor;

/** Sends the existing OmniShifter C2S payloads; there is no duplicate protocol. */
final class SummerOmniPackets {
    private SummerOmniPackets() {}

    static boolean sendSelect(TitanPowerType power) {
        return send("SelectPowerC2SPayload", new Class<?>[] { TitanPowerType.class }, power);
    }

    static boolean sendSwap(TitanPowerType one, TitanPowerType two) {
        return send("SwapPowersC2SPayload",
                new Class<?>[] { TitanPowerType.class, TitanPowerType.class }, one, two);
    }

    static boolean relinquishRoyal() { return send("RelinquishRoyalC2SPayload", new Class<?>[0]); }
    static boolean relinquishAckerman() { return send("RelinquishAckermanC2SPayload", new Class<?>[0]); }
    static boolean becomeEldian() { return send("BecomeEldianC2SPayload", new Class<?>[0]); }

    private static boolean send(String name, Class<?>[] signature, Object... args) {
        try {
            Class<?> type = Class.forName("com.omnishifter.network." + name);
            Constructor<?> constructor = type.getConstructor(signature);
            ClientPlayNetworking.send((CustomPayload) constructor.newInstance(args));
            return true;
        } catch (ReflectiveOperationException | LinkageError | ClassCastException exception) {
            dev.summeraot.SummerAOTMod.LOGGER.error("OmniShifter payload indisponivel: " + name, exception);
            return false;
        }
    }
}
