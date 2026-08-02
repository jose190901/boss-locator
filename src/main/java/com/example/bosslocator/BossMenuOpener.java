package com.example.bosslocator;

import com.example.bosslocator.client.BossResults;
import com.example.bosslocator.net.BossNetwork;
import com.example.bosslocator.net.UnlockRequestPacket;
import com.example.bosslocator.ui.BossMenuScreen;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
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
        Minecraft.getInstance().player.displayClientMessage(
                Component.translatable("boss_locator.msg.ready").withStyle(ChatFormatting.RED), false);
        BossResults.beginWait();
        BossNetwork.CHANNEL.sendToServer(new UnlockRequestPacket(UnlockRequestPacket.ACTION_OPEN, ""));
        Minecraft.getInstance().setScreen(new BossMenuScreen());
    }
}
