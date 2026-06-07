package net.bristn.interactive_enchanted_books.screen.info;

import java.util.List;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;

public record ItemWithTooltip(Item item, List<MutableComponent> tooltips) {

    public static ItemWithTooltip of(Item item, MutableComponent... tooltip) {
        return new ItemWithTooltip(item, List.of(tooltip));
    }
}