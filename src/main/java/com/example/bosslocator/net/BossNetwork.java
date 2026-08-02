package com.example.bosslocator.net;

import com.example.bosslocator.BossLocator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class BossNetwork {
    private static final String PROTOCOL_VERSION = "2";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BossLocator.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    private BossNetwork() {
    }

    public static void register() {
        CHANNEL.messageBuilder(UnlockRequestPacket.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder(UnlockRequestPacket::encode)
                .decoder(UnlockRequestPacket::decode)
                .consumerMainThread(UnlockRequestPacket::handle)
                .add();

        CHANNEL.messageBuilder(UnlockResponsePacket.class, 1, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(UnlockResponsePacket::encode)
                .decoder(UnlockResponsePacket::decode)
                .consumerMainThread(UnlockResponsePacket::handle)
                .add();
    }
}
