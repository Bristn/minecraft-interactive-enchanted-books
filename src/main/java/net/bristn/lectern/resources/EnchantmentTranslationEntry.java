package net.bristn.lectern.resources;

import java.util.ArrayList;
import java.util.List;

import net.bristn.lectern.resources.json.EnchantmentTranslationJsonEntry;

public class EnchantmentTranslationEntry {
    public final String name;

    public EnchantmentTranslationEntry(String name) {
        this.name = name;
    }

    public static EnchantmentTranslationEntry fromJson(EnchantmentTranslationJsonEntry json) {
        return new EnchantmentTranslationEntry(json.name());
    }

    public static List<EnchantmentTranslationEntry> fromJsonList(List<EnchantmentTranslationJsonEntry> json) {
        var result = new ArrayList<EnchantmentTranslationEntry>();

        for (var entry : json) {
            result.add(fromJson(entry));
        }

        return result;
    }
}
