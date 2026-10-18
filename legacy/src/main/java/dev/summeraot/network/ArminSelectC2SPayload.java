package dev.summeraot.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Requisicao cliente; posse/cooldown validados apenas no servidor. */
public record ArminSelectC2SPayload() implements CustomPayload {
    public static final Id<ArminSelectC2SPayload> ID =
            new Id<>(Identifier.of("summeraot", "select_armin"));
    public static final PacketCodec<RegistryByteBuf, ArminSelectC2SPayload> CODEC =
            PacketCodec.of((packet, buf) -> {}, buf -> new ArminSelectC2SPayload());
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
