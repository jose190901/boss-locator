package com.example.bosslocator.net;

import com.example.bosslocator.BossLocator;
import com.example.bosslocator.data.BossCatalog;
import com.example.bosslocator.data.BossEntry;
import com.example.bosslocator.data.BossLocation;
import com.example.bosslocator.data.BossUnlockData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UnlockRequestPacket {
    public static final int ACTION_OPEN = 0;
    public static final int ACTION_UNLOCK = 1;

    private final int action;
    private final String structureId;

    public UnlockRequestPacket(int action, String structureId) {
        this.action = action;
        this.structureId = structureId;
    }

    public static void encode(UnlockRequestPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.action);
        buf.writeUtf(packet.structureId);
    }

    public static UnlockRequestPacket decode(FriendlyByteBuf buf) {
        return new UnlockRequestPacket(buf.readVarInt(), buf.readUtf());
    }

    public static void handle(UnlockRequestPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            if (ctx.getSender() == null) {
                return;
            }
            ServerPlayer player = ctx.getSender();
            BossUnlockData data = BossUnlockData.get(player.server);

            if (packet.action == ACTION_OPEN) {
                sendUnlocks(player, data);
            } else if (packet.action == ACTION_UNLOCK) {
                handleUnlock(player, data, packet.structureId);
            }
        });
        ctx.setPacketHandled(true);
    }

    private static void handleUnlock(ServerPlayer player, BossUnlockData data, String structureId) {
        if (structureId.isEmpty()) {
            sendUnlocks(player, data);
            return;
        }
        ResourceLocation id = new ResourceLocation(structureId);
        BossEntry boss = BossCatalog.getByStructure(id);
        if (boss == null) {
            return;
        }

        if (data.isUnlocked(player.getUUID(), id.toString())) {
            sendLocate(player, data, id, BossCatalog.locateOne(boss, player.blockPosition()));
            return;
        }

        if (tryPayEye(player, boss)) {
            data.unlock(player.getUUID(), id.toString());
            player.displayClientMessage(Component.translatable("boss_locator.msg.unlocked",
                    boss.displayName()).withStyle(net.minecraft.ChatFormatting.GREEN), false);
            sendLocate(player, data, id, BossCatalog.locateOne(boss, player.blockPosition()));
        } else if (tryPayFallback(player, boss)) {
            data.unlock(player.getUUID(), id.toString());
            player.displayClientMessage(Component.translatable("boss_locator.msg.unlocked",
                    boss.displayName()).withStyle(net.minecraft.ChatFormatting.GREEN), false);
            sendLocate(player, data, id, BossCatalog.locateOne(boss, player.blockPosition()));
        } else {
            player.displayClientMessage(Component.translatable("boss_locator.msg.cannot_unlock",
                    boss.displayName()).withStyle(net.minecraft.ChatFormatting.RED), false);
        }
    }

    private static boolean tryPayEye(ServerPlayer player, BossEntry boss) {
        ItemStack eye = new ItemStack(BuiltInRegistries.ITEM.get(boss.eyeItem()));
        if (eye.isEmpty()) {
            return false;
        }
        int count = player.getInventory().countItem(eye.getItem());
        if (count < 1 || player.experienceLevel < boss.eyeXpLevels()) {
            return false;
        }
        player.getInventory().clearOrCountMatchingItems(s -> s.is(eye.getItem()), 1, player.getInventory());
        player.giveExperienceLevels(-boss.eyeXpLevels());
        return true;
    }

    private static boolean tryPayFallback(ServerPlayer player, BossEntry boss) {
        ItemStack item = new ItemStack(BuiltInRegistries.ITEM.get(boss.fallbackItem()));
        if (item.isEmpty()) {
            return false;
        }
        int count = player.getInventory().countItem(item.getItem());
        if (count < boss.fallbackItemCount() || player.experienceLevel < boss.fallbackXpLevels()) {
            return false;
        }
        player.getInventory().clearOrCountMatchingItems(s -> s.is(item.getItem()), boss.fallbackItemCount(), player.getInventory());
        player.giveExperienceLevels(-boss.fallbackXpLevels());
        return true;
    }

    private static void sendUnlocks(ServerPlayer player, BossUnlockData data) {
        sendLocate(player, data, null, null);
    }

    private static void sendLocate(ServerPlayer player, BossUnlockData data, ResourceLocation structureId, BossLocation location) {
        BossNetwork.CHANNEL.send(net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new UnlockResponsePacket(data.getAllUnlocked(player.getUUID()),
                        structureId != null ? structureId.toString() : null, location));
    }
}
