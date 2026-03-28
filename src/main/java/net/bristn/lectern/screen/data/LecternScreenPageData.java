package net.bristn.lectern.screen.data;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public record LecternScreenPageData(LecternScreenSupportedData supported, MutableComponent title,
        List<MutableComponent> leftHeaders, List<MutableComponent> leftTexts) {

    public static LecternScreenPageData EMPTY = new LecternScreenPageData(LecternScreenSupportedData.EMPTY, Component.empty(),
            new ArrayList<>(), new ArrayList<>());
}
