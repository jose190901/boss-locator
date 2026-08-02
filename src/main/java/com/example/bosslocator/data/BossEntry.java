package com.example.bosslocator.data;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class BossEntry {
    private final ResourceLocation structure;
    private final String displayName;
    private final ResourceKey<Level> dimensionKey;
    private final ResourceLocation eyeItem;
    private final int eyeXpLevels;
    private final ResourceLocation fallbackItem;
    private final int fallbackItemCount;
    private final int fallbackXpLevels;

    public BossEntry(ResourceLocation structure, String displayName, ResourceKey<Level> dimensionKey,
                     ResourceLocation eyeItem, int eyeXpLevels) {
        this.structure = structure;
        this.displayName = displayName;
        this.dimensionKey = dimensionKey;
        this.eyeItem = eyeItem;
        this.eyeXpLevels = eyeXpLevels;
        this.fallbackItem = new ResourceLocation("minecraft", "netherite_ingot");
        this.fallbackItemCount = 2;
        this.fallbackXpLevels = 20;
    }

    public ResourceLocation structure() {
        return structure;
    }

    public String displayName() {
        return displayName;
    }

    public ResourceKey<Level> dimensionKey() {
        return dimensionKey;
    }

    public ResourceLocation eyeItem() {
        return eyeItem;
    }

    public int eyeXpLevels() {
        return eyeXpLevels;
    }

    public ResourceLocation fallbackItem() {
        return fallbackItem;
    }

    public int fallbackItemCount() {
        return fallbackItemCount;
    }

    public int fallbackXpLevels() {
        return fallbackXpLevels;
    }
}
