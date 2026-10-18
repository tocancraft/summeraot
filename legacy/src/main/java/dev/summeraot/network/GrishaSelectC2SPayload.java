package dev.summeraot.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Client request only. Server checks possession and cooldown. */
public record GrishaSelectC2SPayload() implements CustomPayload {
    public static final Id<GrishaSelectC2SPayload> ID =
        new Id<>(Identifier.of("summeraot", "select_grisha"));
    public static final PacketCodec<RegistryByteBuf, GrishaSelectC2SPayload> CODEC =
        PacketCodec.of((packet, buf) -> {}, buf -> new GrishaSelectC2SPayload());
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
