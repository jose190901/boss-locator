package com.example.bosslocator.net;

import com.example.bosslocator.data.BossLocation;
import com.example.bosslocator.client.BossResults;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class UnlockResponsePacket {
    private final List<String> unlocked;
    private final String structureId;
    private final BossLocation location;

    public UnlockResponsePacket(List<String> unlocked, String structureId, BossLocation location) {
        this.unlocked = unlocked;
        this.structureId = structureId;
        this.location = location;
    }

    public static void encode(UnlockResponsePacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.unlocked.size());
        for (String s : packet.unlocked) {
            buf.writeUtf(s);
        }
        boolean hasLoc = packet.structureId != null && packet.location != null;
        buf.writeBoolean(hasLoc);
        if (hasLoc) {
            buf.writeUtf(packet.structureId);
            buf.writeUtf(packet.location.getDimension());
            buf.writeInt(packet.location.getPos().getX());
            buf.writeInt(packet.location.getPos().getY());
            buf.writeInt(packet.location.getPos().getZ());
        }
    }

    public static UnlockResponsePacket decode(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        List<String> unlocked = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            unlocked.add(buf.readUtf());
        }
        String structureId = null;
        BossLocation location = null;
        if (buf.readBoolean()) {
            structureId = buf.readUtf();
            location = new BossLocation(
                    new net.minecraft.resources.ResourceLocation(structureId),
                    buf.readUtf(),
                    new net.minecraft.core.BlockPos(buf.readInt(), buf.readInt(), buf.readInt()));
        }
        return new UnlockResponsePacket(unlocked, structureId, location);
    }

    public static void handle(UnlockResponsePacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            BossResults.setUnlocked(packet.unlocked);
            if (packet.structureId != null && packet.location != null) {
                BossResults.setLocation(packet.structureId, packet.location);
            }
        }));
        ctx.setPacketHandled(true);
    }
}
