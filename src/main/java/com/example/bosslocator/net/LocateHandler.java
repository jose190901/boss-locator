package com.example.bosslocator.net;

import com.example.bosslocator.data.BossCatalog;
import net.minecraft.server.level.ServerPlayer;

public class LocateHandler {
    private LocateHandler() {
    }

    public static LocateResultPacket handle(ServerPlayer player) {
        var locations = BossCatalog.locateAll(player.blockPosition());
        return new LocateResultPacket(locations);
    }
}
