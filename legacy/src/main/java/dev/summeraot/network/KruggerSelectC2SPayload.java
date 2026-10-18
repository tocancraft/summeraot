package dev.summeraot.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** A selection request, never an authoritative grant of a power. */
public record KruggerSelectC2SPayload() implements CustomPayload {
    public static final Id<KruggerSelectC2SPayload> ID =
            new Id<>(Identifier.of("summeraot", "select_krugger"));
    public static final PacketCodec<RegistryByteBuf, KruggerSelectC2SPayload> CODEC =
            PacketCodec.of((packet, buf) -> {}, buf -> new KruggerSelectC2SPayload());
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
