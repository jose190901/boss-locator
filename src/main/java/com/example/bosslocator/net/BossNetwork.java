package com.example.bosslocator.net;

import com.example.bosslocator.BossLocator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class BossNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BossLocator.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    private BossNetwork() {
    }

    public static void register() {
        CHANNEL.messageBuilder(LocateRequestPacket.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder(LocateRequestPacket::encode)
                .decoder(LocateRequestPacket::decode)
                .consumerMainThread(LocateRequestPacket::handle)
                .add();

        CHANNEL.messageBuilder(LocateResultPacket.class, 1, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(LocateResultPacket::encode)
                .decoder(LocateResultPacket::decode)
                .consumerMainThread(LocateResultPacket::handle)
                .add();
    }
}
