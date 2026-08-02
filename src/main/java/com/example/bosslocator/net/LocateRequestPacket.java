package com.example.bosslocator.net;

import com.example.bosslocator.BossLocator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LocateRequestPacket {
    public LocateRequestPacket() {
    }

    public static void encode(LocateRequestPacket packet, FriendlyByteBuf buf) {
    }

    public static LocateRequestPacket decode(FriendlyByteBuf buf) {
        return new LocateRequestPacket();
    }

    public static void handle(LocateRequestPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            if (ctx.getSender() != null) {
                var response = LocateHandler.handle(ctx.getSender());
                BossNetwork.CHANNEL.send(
                        net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> ctx.getSender()),
                        response);
            }
        });
        ctx.setPacketHandled(true);
    }
}
