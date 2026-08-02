package com.example.bosslocator.data;

import com.example.bosslocator.BossLocator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BossUnlockData extends SavedData {
    private static final String DATA_NAME = BossLocator.MODID + "_unlocks";
    private static final String TAG_PLAYERS = "players";

    private final Map<UUID, Set<String>> unlockedByPlayer = new HashMap<>();

    public static BossUnlockData get(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        return overworld.getDataStorage().computeIfAbsent(
                BossUnlockData::load,
                BossUnlockData::new,
                DATA_NAME);
    }

    public static BossUnlockData load(CompoundTag tag) {
        BossUnlockData data = new BossUnlockData();
        ListTag players = tag.getList(TAG_PLAYERS, Tag.TAG_COMPOUND);
        for (int i = 0; i < players.size(); i++) {
            CompoundTag playerTag = players.getCompound(i);
            UUID uuid = playerTag.getUUID("uuid");
            Set<String> structures = new HashSet<>();
            ListTag structureList = playerTag.getList("structures", Tag.TAG_STRING);
            for (int j = 0; j < structureList.size(); j++) {
                structures.add(structureList.getString(j));
            }
            data.unlockedByPlayer.put(uuid, structures);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag players = new ListTag();
        for (Map.Entry<UUID, Set<String>> entry : unlockedByPlayer.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putUUID("uuid", entry.getKey());
            ListTag structureList = new ListTag();
            for (String s : entry.getValue()) {
                structureList.add(StringTag.valueOf(s));
            }
            playerTag.put("structures", structureList);
            players.add(playerTag);
        }
        tag.put(TAG_PLAYERS, players);
        return tag;
    }

    public boolean isUnlocked(UUID playerUuid, String structure) {
        Set<String> set = unlockedByPlayer.get(playerUuid);
        return set != null && set.contains(structure);
    }

    public List<String> getAllUnlocked(UUID playerUuid) {
        Set<String> set = unlockedByPlayer.get(playerUuid);
        if (set == null) {
            return List.of();
        }
        return new ArrayList<>(set);
    }

    public void unlock(UUID playerUuid, String structure) {
        unlockedByPlayer.computeIfAbsent(playerUuid, k -> new HashSet<>()).add(structure);
        setDirty();
    }
}
