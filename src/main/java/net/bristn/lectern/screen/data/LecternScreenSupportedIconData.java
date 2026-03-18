package net.bristn.lectern.screen.data;

import java.util.List;

import net.minecraft.resources.Identifier;

public record LecternScreenSupportedIconData(String tooltipTitle, List<Identifier> tooltipIcons,
        Identifier texture) {
}
