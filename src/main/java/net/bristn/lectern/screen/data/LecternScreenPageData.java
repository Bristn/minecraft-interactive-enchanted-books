package net.bristn.lectern.screen.data;

import java.util.List;

import net.minecraft.network.chat.MutableComponent;

public record LecternScreenPageData(LecternScreenSupportedData supported, MutableComponent title,
        List<MutableComponent> leftHeaders, List<MutableComponent> leftTexts) {
}
