package com.example.bosslocator.net;

import com.example.bosslocator.data.BossLocation;
import com.example.bosslocator.data.BossCatalog;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class LocateResultPacket {
    private final List<BossLocation> locations;

    public LocateResultPacket(List<BossLocation> locations) {
        this.locations = locations;
    }

    public static void encode(LocateResultPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.locations.size());
        for (BossLocation loc : packet.locations) {
            buf.writeUtf(loc.getStructure().toString());
            buf.writeUtf(loc.getDimension());
            buf.writeInt(loc.getPos().getX());
            buf.writeInt(loc.getPos().getY());
            buf.writeInt(loc.getPos().getZ());
        }
    }

    public static LocateResultPacket decode(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        List<BossLocation> locations = new java.util.ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            locations.add(new BossLocation(
                    new net.minecraft.resources.ResourceLocation(buf.readUtf()),
                    buf.readUtf(),
                    new net.minecraft.core.BlockPos(buf.readInt(), buf.readInt(), buf.readInt())));
        }
        return new LocateResultPacket(locations);
    }

    public static void handle(LocateResultPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHandler.apply(packet)));
        ctx.setPacketHandled(true);
    }

    private static class ClientHandler {
        private static void apply(LocateResultPacket packet) {
            com.example.bosslocator.client.BossResults.setResults(packet.locations);

            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.player == null) {
                return;
            }
            for (com.example.bosslocator.data.BossLocation loc : packet.locations) {
                mc.player.sendSystemMessage(net.minecraft.network.chat.Component
                        .translatable("boss_locator.msg.ready")
                        .withStyle(net.minecraft.ChatFormatting.RED));
                mc.player.sendSystemMessage(net.minecraft.network.chat.Component
                        .literal("§7  " + loc.getStructure().toString() + " §e["
                                + loc.getPos().getX() + ", " + loc.getPos().getY() + ", " + loc.getPos().getZ() + "]"));
            }
        }
    }
}
