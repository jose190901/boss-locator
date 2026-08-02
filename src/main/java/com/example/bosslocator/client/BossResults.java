package com.example.bosslocator.client;

import com.example.bosslocator.data.BossLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = com.example.bosslocator.BossLocator.MODID, value = Dist.CLIENT)
public final class BossResults {
    private static final List<String> UNLOCKED = new ArrayList<>();
    private static final Map<String, BossLocation> LOCATIONS = new HashMap<>();
    private static boolean waiting = false;
    private static boolean haveData = false;

    private BossResults() {
    }

    public static void beginWait() {
        waiting = true;
    }

    public static boolean isWaiting() {
        return waiting;
    }

    public static void setUnlocked(List<String> unlocked) {
        UNLOCKED.clear();
        UNLOCKED.addAll(unlocked);
        waiting = false;
        haveData = true;
    }

    public static boolean isUnlocked(String structureId) {
        return UNLOCKED.contains(structureId);
    }

    public static void setLocation(String structureId, BossLocation location) {
        LOCATIONS.put(structureId, location);
        waiting = false;
        haveData = true;
    }

    public static BossLocation getLocation(String structureId) {
        return LOCATIONS.get(structureId);
    }

    public static boolean isHaveData() {
        return haveData;
    }

    public static void reset() {
        UNLOCKED.clear();
        LOCATIONS.clear();
        waiting = false;
        haveData = false;
    }
}
