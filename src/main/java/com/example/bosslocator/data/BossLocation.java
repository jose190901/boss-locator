package com.example.bosslocator.data;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class BossLocation {
    private final ResourceLocation structure;
    private final String dimension;
    private final BlockPos pos;

    public BossLocation(ResourceLocation structure, String dimension, BlockPos pos) {
        this.structure = structure;
        this.dimension = dimension;
        this.pos = pos;
    }

    public ResourceLocation getStructure() {
        return structure;
    }

    public String getDimension() {
        return dimension;
    }

    public BlockPos getPos() {
        return pos;
    }
}
