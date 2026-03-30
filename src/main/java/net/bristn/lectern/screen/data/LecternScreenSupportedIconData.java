package net.bristn.lectern.screen.data;

import java.util.List;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public record LecternScreenSupportedIconData(String title, List<Item> tooltipItems, Identifier texture) {
}
