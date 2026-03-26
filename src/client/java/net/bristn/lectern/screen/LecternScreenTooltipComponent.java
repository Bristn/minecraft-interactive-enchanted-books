package net.bristn.lectern.screen;

import java.util.List;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class LecternScreenTooltipComponent implements ClientTooltipComponent, TooltipComponent {
    private static final int PADDING = 8;
    private static final int SIZE = 16;
    private static final int ICONS_PER_ROW = 5;
    private static final int TEXT_OFFSET = 8 + PADDING;

    private final List<Item> items;
    private final String title;

    public LecternScreenTooltipComponent(String title, List<Item> items) {
        this.items = items;
        this.title = title;
    }

    @Override
    public int getHeight(Font font) {
        var columns = items.size() / LecternScreenTooltipComponent.ICONS_PER_ROW;
        columns += 1;
        return columns * SIZE + (columns - 1) * PADDING + PADDING + PADDING / 2 + TEXT_OFFSET;
    }

    @Override
    public int getWidth(Font font) {
        var rows = ICONS_PER_ROW;
        return rows * SIZE + (rows - 1) * PADDING + PADDING;
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        var collector = graphics.textRenderer(GuiGraphicsExtractor.HoveredTextEffects.TOOLTIP_AND_CURSOR);
        collector.accept(TextAlignment.LEFT, x, y + PADDING / 2, Component.literal(title));
        y += TEXT_OFFSET;

        for (var i = 0; i < items.size(); i++) {
            var col = i % LecternScreenTooltipComponent.ICONS_PER_ROW;
            var row = (int) (Math.floor(i / LecternScreenTooltipComponent.ICONS_PER_ROW));

            graphics.item(new ItemStack(items.get(i)), x + col * (SIZE + PADDING) + PADDING / 2,
                    y + row * (SIZE + PADDING) + PADDING / 2);
        }
    }
}