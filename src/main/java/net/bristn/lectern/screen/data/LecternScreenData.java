package net.bristn.lectern.screen.data;

import java.util.List;

import net.minecraft.network.chat.Component;

public record LecternScreenData(LecternScreenSupportedData supported, Component title, List<Component> descriptions,
        List<Component> exclusive) {

}
