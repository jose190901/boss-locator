package com.example.bosslocator.ui;

import com.example.bosslocator.client.BossResults;
import com.example.bosslocator.data.BossEntry;
import com.example.bosslocator.data.BossLocation;
import com.example.bosslocator.net.BossNetwork;
import com.example.bosslocator.net.UnlockRequestPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class BossMenuScreen extends Screen {
    private static final int ENTRY_HEIGHT = 26;
    private static final int CONTENT_LEFT = 20;
    private static final int CONTENT_TOP = 40;
    private static final int CONTENT_WIDTH = 360;
    private static final int CONTENT_HEIGHT = 170;

    private final List<BossEntry> bosses;
    private int scrollOffset = 0;
    private boolean loading = true;
    private int ticks = 0;
    private String selectedStructure = null;

    public BossMenuScreen() {
        super(Component.translatable("boss_locator.menu.title"));
        bosses = com.example.bosslocator.data.BossCatalog.getBosses();
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(Button.builder(Component.translatable("boss_locator.menu.close"), b -> this.onClose())
                .bounds(this.width / 2 - 50, this.height - 30, 100, 20)
                .build());
    }

    @Override
    public void tick() {
        super.tick();
        ticks++;
        if (!BossResults.isWaiting() || ticks > 200) {
            loading = false;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && !loading) {
            int clicked = hitTest(mouseX, mouseY);
            if (clicked >= 0 && clicked < bosses.size()) {
                selectBoss(bosses.get(clicked));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private int hitTest(double mouseX, double mouseY) {
        if (mouseX < CONTENT_LEFT || mouseX > CONTENT_LEFT + CONTENT_WIDTH) {
            return -1;
        }
        for (int i = 0; i < bosses.size(); i++) {
            int y = CONTENT_TOP + 14 + i * ENTRY_HEIGHT - scrollOffset;
            if (mouseY >= y && mouseY < y + ENTRY_HEIGHT - 2) {
                return i;
            }
        }
        return -1;
    }

    private void selectBoss(BossEntry boss) {
        String id = boss.structure().toString();
        selectedStructure = id;
        BossResults.beginWait();
        loading = true;
        BossNetwork.CHANNEL.sendToServer(new UnlockRequestPacket(UnlockRequestPacket.ACTION_UNLOCK, id));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 0xFFFFFF);
        graphics.drawCenteredString(this.font,
                Component.translatable("boss_locator.menu.subtitle").withStyle(ChatFormatting.GRAY),
                this.width / 2, 28, 0xAAAAAA);

        if (loading) {
            graphics.drawCenteredString(this.font,
                    Component.translatable("boss_locator.menu.loading").withStyle(ChatFormatting.YELLOW),
                    this.width / 2, this.height / 2, 0xFFFFFF);
            return;
        }

        fill(graphics, CONTENT_LEFT - 5, CONTENT_TOP - 5, CONTENT_LEFT + CONTENT_WIDTH + 5, CONTENT_TOP + CONTENT_HEIGHT + 5, 0x90000000);
        graphics.drawCenteredString(this.font,
                Component.translatable("boss_locator.menu.header").withStyle(ChatFormatting.GOLD),
                CONTENT_LEFT + CONTENT_WIDTH / 2, CONTENT_TOP, 0xFFFFFF);

        for (int i = 0; i < bosses.size(); i++) {
            int y = CONTENT_TOP + 14 + i * ENTRY_HEIGHT - scrollOffset;
            if (y < CONTENT_TOP + 10 || y > CONTENT_TOP + CONTENT_HEIGHT - ENTRY_HEIGHT) {
                continue;
            }
            renderEntry(graphics, bosses.get(i), CONTENT_LEFT, y);
        }
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderEntry(GuiGraphics graphics, BossEntry boss, int x, int y) {
        String id = boss.structure().toString();
        boolean unlocked = BossResults.isUnlocked(id);
        fill(graphics, x, y, x + CONTENT_WIDTH, y + ENTRY_HEIGHT - 2, unlocked ? 0x55119944 : 0x55555555);

        String lock = unlocked ? "\u2714 " : "\uD83D\uDD12 ";
        graphics.drawString(this.font, lock + boss.displayName(), x + 6, y + 4, unlocked ? 0xAAFFAA : 0xFFFFFF);

        BossLocation loc = BossResults.getLocation(id);
        if (unlocked && loc != null) {
            String dim = switch (loc.getDimension()) {
                case "minecraft:the_nether" -> "Nether";
                case "minecraft:the_end" -> "End";
                default -> "Overworld";
            };
            String coords = String.format("X: %d  Y: %d  Z: %d", loc.getPos().getX(), loc.getPos().getY(), loc.getPos().getZ());
            graphics.drawString(this.font, Component.literal(dim + "  " + coords).withStyle(ChatFormatting.GRAY), x + 6, y + 14, 0xAAAAAA);
            if (Minecraft.getInstance().player != null) {
                double dist = Math.sqrt(Minecraft.getInstance().player.distanceToSqr(loc.getPos().getX(), loc.getPos().getY(), loc.getPos().getZ()));
                String distText = (int) dist + "m";
                graphics.drawString(this.font, Component.literal(distText).withStyle(ChatFormatting.GOLD), x + CONTENT_WIDTH - this.font.width(distText) - 6, y + 4, 0xFFAA00);
            }
        } else if (!unlocked) {
            String cost = boss.eyeItem().toString() + " + " + boss.eyeXpLevels() + " XP";
            graphics.drawString(this.font, Component.translatable("boss_locator.menu.cost", cost).withStyle(ChatFormatting.YELLOW), x + 6, y + 14, 0xFFAA00);
        }

        if (id.equals(selectedStructure)) {
            fill(graphics, x, y, x + 3, y + ENTRY_HEIGHT - 2, 0xFFFFFFFF);
        }
    }

    private void fill(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        graphics.fill(x1, y1, x2, y2, color);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int maxScroll = Math.max(0, bosses.size() * ENTRY_HEIGHT - (CONTENT_HEIGHT - 14));
        scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - delta * 10));
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
