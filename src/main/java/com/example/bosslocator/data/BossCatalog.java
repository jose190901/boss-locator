package com.example.bosslocator.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;

public class BossCatalog {
    private static final List<BossEntry> BOSSES = new ArrayList<>();

    static {
        BOSSES.add(new BossEntry(new ResourceLocation("cataclysm", "burning_arena"), "The Ignis", Level.NETHER,
                new ResourceLocation("cataclysm", "flame_eye"), 5));
        BOSSES.add(new BossEntry(new ResourceLocation("cataclysm", "sunken_city"), "The Leviathan", Level.OVERWORLD,
                new ResourceLocation("cataclysm", "abyss_eye"), 5));
        BOSSES.add(new BossEntry(new ResourceLocation("cataclysm", "ancient_factory"), "The Harbinger", Level.OVERWORLD,
                new ResourceLocation("cataclysm", "mech_eye"), 5));
        BOSSES.add(new BossEntry(new ResourceLocation("cataclysm", "cursed_pyramid"), "Ancient Remnant", Level.OVERWORLD,
                new ResourceLocation("cataclysm", "desert_eye"), 5));
        BOSSES.add(new BossEntry(new ResourceLocation("cataclysm", "soul_black_smith"), "Netherite Monstrosity", Level.NETHER,
                new ResourceLocation("cataclysm", "monstrous_eye"), 5));
        BOSSES.add(new BossEntry(new ResourceLocation("cataclysm", "acropolis"), "Scylla", Level.OVERWORLD,
                new ResourceLocation("cataclysm", "storm_eye"), 5));
        BOSSES.add(new BossEntry(new ResourceLocation("cataclysm", "ruined_citadel"), "Ender Golem", Level.END,
                new ResourceLocation("cataclysm", "void_eye"), 5));
        BOSSES.add(new BossEntry(new ResourceLocation("cataclysm", "frosted_prison"), "Maledictus", Level.OVERWORLD,
                new ResourceLocation("cataclysm", "cursed_eye"), 5));
    }

    private BossCatalog() {
    }

    public static List<BossEntry> getBosses() {
        return BOSSES;
    }

    public static BossEntry getByStructure(ResourceLocation structure) {
        for (BossEntry boss : BOSSES) {
            if (boss.structure().equals(structure)) {
                return boss;
            }
        }
        return null;
    }

    public static List<BossLocation> locateAll(BlockPos origin) {
        List<BossLocation> results = new ArrayList<>();
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return results;
        }

        for (BossEntry boss : BOSSES) {
            ServerLevel level = server.getLevel(boss.dimensionKey());
            if (level == null) {
                continue;
            }
            BlockPos found = findStructure(level, boss, origin);
            if (found != null) {
                results.add(new BossLocation(boss.structure(), boss.dimensionKey().location().toString(), found));
            }
        }
        return results;
    }

    public static BossLocation locateOne(BossEntry boss, BlockPos origin) {
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return null;
        }
        ServerLevel level = server.getLevel(boss.dimensionKey());
        if (level == null) {
            return null;
        }
        BlockPos found = findStructure(level, boss, origin);
        if (found == null) {
            return null;
        }
        return new BossLocation(boss.structure(), boss.dimensionKey().location().toString(), found);
    }

    private static BlockPos findStructure(ServerLevel level, BossEntry boss, BlockPos origin) {
        var registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        var holder = registry.getHolder(ResourceKey.create(Registries.STRUCTURE, boss.structure()));
        if (holder.isEmpty()) {
            return null;
        }
        var pair = level.getChunkSource().getGenerator()
                .findNearestMapStructure(level, HolderSet.direct(holder.get()), origin, 100, false);
        return pair != null ? pair.getFirst() : null;
    }
}
