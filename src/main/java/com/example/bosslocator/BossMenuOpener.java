package com.example.bosslocator;

import com.example.bosslocator.net.BossNetwork;
import com.example.bosslocator.net.LocateRequestPacket;
import com.example.bosslocator.ui.BossMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BossLocator.MODID, value = Dist.CLIENT)
public final class BossMenuOpener {
    private BossMenuOpener() {
    }

    public static void open() {
        if (Minecraft.getInstance().player == null) {
            return;
        }
        BossNetwork.CHANNEL.sendToServer(new LocateRequestPacket());
        Minecraft.getInstance().setScreen(new BossMenuScreen());
    }
}
