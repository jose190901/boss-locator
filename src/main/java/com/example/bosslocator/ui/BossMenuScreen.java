package com.example.bosslocator.ui;

import com.example.bosslocator.client.BossResults;
import com.example.bosslocator.data.BossLocation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BossMenuScreen extends Screen {
    private static final int ENTRY_HEIGHT = 22;
    private static final int CONTENT_LEFT = 20;
    private static final int CONTENT_TOP = 40;
    private static final int CONTENT_WIDTH = 360;
    private static final int CONTENT_HEIGHT = 160;

    private int scrollOffset = 0;
    private final List<BossLocation> results = BossResults.getResults();
    private final Map<String, String> bossNames = new HashMap<>();
    private boolean loading = true;
    private int ticks = 0;

    public BossMenuScreen() {
        super(Component.translatable("boss_locator.menu.title"));
        bossNames.put("cataclysm:burning_arena", "The Ignis");
        bossNames.put("cataclysm:sunken_city", "The Leviathan");
        bossNames.put("cataclysm:ancient_factory", "The Harbinger");
        bossNames.put("cataclysm:cursed_pyramid", "Ancient Remnant");
        bossNames.put("cataclysm:soul_black_smith", "Netherite Monstrosity");
        bossNames.put("cataclysm:acropolis", "Scylla");
        bossNames.put("cataclysm:ruined_citadel", "Ender Golem");
        bossNames.put("cataclysm:frosted_prison", "Maledictus");
        BossResults.beginWait();
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

        if (results.isEmpty()) {
            graphics.drawCenteredString(this.font,
                    Component.translatable("boss_locator.menu.none").withStyle(ChatFormatting.RED),
                    this.width / 2, this.height / 2, 0xFFFFFF);
            return;
        }

        // background panel
        fill(graphics, CONTENT_LEFT - 5, CONTENT_TOP - 5, CONTENT_LEFT + CONTENT_WIDTH + 5, CONTENT_TOP + CONTENT_HEIGHT + 5, 0x90000000);
        graphics.drawCenteredString(this.font,
                Component.translatable("boss_locator.menu.header").withStyle(ChatFormatting.GOLD),
                CONTENT_LEFT + CONTENT_WIDTH / 2, CONTENT_TOP, 0xFFFFFF);

        int y = CONTENT_TOP + 14;
        for (int i = 0; i < results.size(); i++) {
            int itemY = CONTENT_TOP + 14 + i * ENTRY_HEIGHT - scrollOffset;
            if (itemY < CONTENT_TOP + 10 || itemY > CONTENT_TOP + CONTENT_HEIGHT - ENTRY_HEIGHT) {
                continue;
            }
            renderEntry(graphics, results.get(i), CONTENT_LEFT, itemY, mouseX, mouseY);
        }
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void renderEntry(GuiGraphics graphics, BossLocation loc, int x, int y, int mouseX, int mouseY) {
        fill(graphics, x, y, x + CONTENT_WIDTH, y + ENTRY_HEIGHT - 2, 0x66FFFFFF);
        String name = bossNames.getOrDefault(loc.getStructure().toString(), loc.getStructure().toString());
        graphics.drawString(this.font, name, x + 6, y + 4, 0xFFFFFF);

        String dim = switch (loc.getDimension()) {
            case "minecraft:the_nether" -> "Nether";
            case "minecraft:the_end" -> "End";
            default -> "Overworld";
        };
        String coords = String.format("X: %d  Y: %d  Z: %d", loc.getPos().getX(), loc.getPos().getY(), loc.getPos().getZ());
        graphics.drawString(this.font, Component.literal(dim + "  " + coords).withStyle(ChatFormatting.GRAY), x + 6, y + 12, 0xAAAAAA);

        if (Minecraft.getInstance().player != null) {
            double dist = Math.sqrt(Minecraft.getInstance().player.distanceToSqr(loc.getPos().getX(), loc.getPos().getY(), loc.getPos().getZ()));
            int blocks = (int) dist;
            String distText = blocks + "m";
            int textWidth = this.font.width(distText);
            graphics.drawString(this.font, Component.literal(distText).withStyle(ChatFormatting.GOLD), x + CONTENT_WIDTH - textWidth - 6, y + 4, 0xFFAA00);
        }
    }

    private void fill(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        graphics.fill(x1, y1, x2, y2, color);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int maxScroll = Math.max(0, results.size() * ENTRY_HEIGHT - (CONTENT_HEIGHT - 14));
        scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - delta * 10));
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
