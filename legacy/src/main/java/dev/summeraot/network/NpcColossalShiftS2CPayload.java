package dev.summeraot.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Posicao do efeito Effekseer nuclear original do DAOT na transformacao do NPC Armin. */
public record NpcColossalShiftS2CPayload(double x, double y, double z) implements CustomPayload {
    public static final Id<NpcColossalShiftS2CPayload> ID =
            new Id<>(Identifier.of("summeraot", "npc_colossal_shift"));
    public static final PacketCodec<RegistryByteBuf, NpcColossalShiftS2CPayload> CODEC =
            PacketCodec.of((packet, buffer) -> {
                buffer.writeDouble(packet.x());
                buffer.writeDouble(packet.y());
                buffer.writeDouble(packet.z());
            }, buffer -> new NpcColossalShiftS2CPayload(
                    buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}
