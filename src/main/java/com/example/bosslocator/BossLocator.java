package com.example.bosslocator;

import com.example.bosslocator.net.BossNetwork;
import com.mojang.logging.LogUtils;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(BossLocator.MODID)
public class BossLocator {
    public static final String MODID = "boss_locator";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BossLocator() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        BossNetwork.register();

        LOGGER.info("Boss Locator mod loaded");
    }
}
