package net.bristn.lectern.resources.json;

import java.util.ArrayList;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record EnchantmentDataJsonEntry(String enchantment, String particle, int priority,
        ArrayList<EnchantmentTranslationJsonEntry> parameters) {

    public static final Codec<EnchantmentDataJsonEntry> CODEC = RecordCodecBuilder.create(instance -> {
        var enchantmentBuilder = Codec.STRING.fieldOf("enchantment").forGetter(EnchantmentDataJsonEntry::enchantment);
        var particleBuilder = Codec.STRING.fieldOf("particle").forGetter(EnchantmentDataJsonEntry::particle);
        var priorityBuilder = Codec.INT.fieldOf("priority").forGetter(EnchantmentDataJsonEntry::priority);

        return instance.group(enchantmentBuilder, particleBuilder, priorityBuilder).apply(instance,
                (enchantment, particle, priority) -> {
                    return new EnchantmentDataJsonEntry(enchantment, particle, priority, new ArrayList<>());
                });
    });

    public EnchantmentDataJsonEntry withParameters(ArrayList<EnchantmentTranslationJsonEntry> parameters) {
        return new EnchantmentDataJsonEntry(enchantment, particle, priority, parameters);
    }
}