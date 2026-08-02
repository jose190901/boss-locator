package com.example.bosslocator.data;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class BossEntry {
    private final ResourceLocation structure;
    private final String displayName;
    private final ResourceKey<Level> dimensionKey;

    public BossEntry(ResourceLocation structure, String displayName, ResourceKey<Level> dimensionKey) {
        this.structure = structure;
        this.displayName = displayName;
        this.dimensionKey = dimensionKey;
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
}
