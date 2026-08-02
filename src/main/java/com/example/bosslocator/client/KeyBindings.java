package com.example.bosslocator.client;

import com.example.bosslocator.BossLocator;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BossLocator.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class KeyBindings {
    public static final KeyMapping OPEN_MENU = new KeyMapping(
            "key.boss_locator.open",
            InputConstants.Type.KEYSYM,
            org.lwjgl.glfw.GLFW.GLFW_KEY_G,
            "key.categories.boss_locator");

    private KeyBindings() {
    }

    @SubscribeEvent
    public static void registerKeyMappings(final RegisterKeyMappingsEvent event) {
        event.register(OPEN_MENU);
    }
}
