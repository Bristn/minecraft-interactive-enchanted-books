package net.bristn.lectern.resources;

import java.util.ArrayList;
import java.util.List;

import net.bristn.lectern.resources.json.EnchantmentTranslationJsonEntry;
import net.bristn.lectern.transformers.SimpleValueTransformer;

public class EnchantmentTranslationEntry {
    public final String name;
    public final SimpleValueTransformer transformer;

    public EnchantmentTranslationEntry(String name, SimpleValueTransformer transformer) {
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
