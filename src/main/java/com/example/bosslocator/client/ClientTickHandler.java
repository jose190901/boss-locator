package com.example.bosslocator.client;

import com.example.bosslocator.BossLocator;
import com.example.bosslocator.BossMenuOpener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BossLocator.MODID, value = Dist.CLIENT)
public final class ClientTickHandler {
    private ClientTickHandler() {
    }

    @SubscribeEvent
    public static void onClientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (KeyBindings.OPEN_MENU.consumeClick()) {
            BossMenuOpener.open();
        }
    }
}
