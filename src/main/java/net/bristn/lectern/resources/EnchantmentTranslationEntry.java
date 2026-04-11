package net.bristn.lectern.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.bristn.lectern.resources.json.EnchantmentTranslationJsonEntry;

public class EnchantmentTranslationEntry {
    public final String name;
    public final Function<Float, Float> transformer;

    public EnchantmentTranslationEntry(String name, Function<Float, Float> transformer) {
        this.name = name;
        this.transformer = transformer;
    }

    public static EnchantmentTranslationEntry fromJson(EnchantmentTranslationJsonEntry json) {
        return new EnchantmentTranslationEntry(json.name(), json.transformer());
    }

    public static List<EnchantmentTranslationEntry> fromJsonList(List<EnchantmentTranslationJsonEntry> json) {
        var result = new ArrayList<EnchantmentTranslationEntry>();

        for (var entry : json) {
            result.add(fromJson(entry));
        }

        return result;
    }
}
