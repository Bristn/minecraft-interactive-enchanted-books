package net.bristn.interactive_enchanted_books.screen.data;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

/**
 * Contains all data needed to render one of the menu pages
 */
public record LecternScreenPageData( //
        LecternScreenSupportedData supported, //
        MutableComponent title, //
        List<MutableComponent> leftHeaders, //
        List<MutableComponent> leftTexts, //
        MutableComponent redstoneSignal, //
        boolean isEcho, //
        Identifier particleId//
) {

    public static LecternScreenPageData EMPTY = new LecternScreenPageData( //
            LecternScreenSupportedData.EMPTY, //
            Component.empty(), //
            new ArrayList<>(), //
            new ArrayList<>(), //
            Component.empty(), //
            false, //
            null //
    );

    public List<Component> getNarrationMessage() {
        var result = new ArrayList<Component>();
        result.add(title);

        // Add the left page content
        for (var i = 0; i < leftHeaders.size(); i++) {
            result.add(leftHeaders.get(i));
            result.add(leftTexts.get(i));
        }

        // Add the comparator signal message
        var comparator = Component.translatable("gui.interactive_enchanted_books.signal");
        result.addAll(List.of(comparator, redstoneSignal));

        // Add the supported items message
        var applicableTo = Component.translatable("gui.interactive_enchanted_books.applicable");
        result.add(applicableTo);

        for (var component : supported().icons()) {
            result.add(Component.literal(component.title()));
        }

        return result;
    }
}
