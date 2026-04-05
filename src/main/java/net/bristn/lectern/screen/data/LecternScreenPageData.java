package net.bristn.lectern.screen.data;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Contains all data needed to render one of the menu pages
 */
public record LecternScreenPageData(LecternScreenSupportedData supported, MutableComponent title,
        List<MutableComponent> leftHeaders, List<MutableComponent> leftTexts, MutableComponent redstoneSignal) {

    public static LecternScreenPageData EMPTY = new LecternScreenPageData(LecternScreenSupportedData.EMPTY, Component.empty(),
            new ArrayList<>(), new ArrayList<>(), Component.empty());
}
