package dev.summeraot.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Server-authoritative state so the Omni UI shows only powers actually owned. */
public record KruggerStateS2CPayload(boolean owned, boolean selected, int nativePowerOrdinal, long cooldownEndsAt)
        implements CustomPayload {
    public static final Id<KruggerStateS2CPayload> ID =
            new Id<>(Identifier.of("summeraot", "krugger_state"));
    public static final PacketCodec<RegistryByteBuf, KruggerStateS2CPayload> CODEC =
            PacketCodec.of((packet, buf) -> {
                buf.writeBoolean(packet.owned());
                buf.writeBoolean(packet.selected());
                buf.writeVarInt(packet.nativePowerOrdinal());
                buf.writeLong(packet.cooldownEndsAt());
            }, buf -> new KruggerStateS2CPayload(buf.readBoolean(), buf.readBoolean(), buf.readVarInt(), buf.readLong()));
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
