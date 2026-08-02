package com.example.bosslocator.client;

import com.example.bosslocator.data.BossLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = com.example.bosslocator.BossLocator.MODID, value = Dist.CLIENT)
public final class BossResults {
    private static final List<BossLocation> RESULTS = new ArrayList<>();
    private static boolean waiting = false;

    private BossResults() {
    }

    public static void beginWait() {
        waiting = true;
    }

    public static boolean isWaiting() {
        return waiting;
    }

    public static void setResults(List<BossLocation> locations) {
        RESULTS.clear();
        RESULTS.addAll(locations);
        waiting = false;
    }

    public static List<BossLocation> getResults() {
        return RESULTS;
    }
}
