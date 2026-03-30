package net.bristn.lectern.resources.json;

import java.util.ArrayList;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record EnchantmentDataJsonEntry(String enchantment, String particle, int priority,
        ArrayList<EnchantmentTranslationJsonEntry> parameters) {

    public static final Codec<EnchantmentDataJsonEntry> CODEC = RecordCodecBuilder.create(instance -> {
        var enchantmentBuilder = Codec.STRING.fieldOf("enchantment").forGetter(EnchantmentDataJsonEntry::enchantment);
        var priorityBuilder = Codec.INT.fieldOf("priority").forGetter(EnchantmentDataJsonEntry::priority);
        var particleBuilder = Codec.STRING.optionalFieldOf("particle", "lectern-enchanted-books:enchant_particle")
                .forGetter(EnchantmentDataJsonEntry::particle);

        return instance.group(enchantmentBuilder, particleBuilder, priorityBuilder).apply(instance,
                (enchantment, particle, priority) -> {
                    return new EnchantmentDataJsonEntry(enchantment, particle, priority, new ArrayList<>());
                });
    });

    public EnchantmentDataJsonEntry withParameters(ArrayList<EnchantmentTranslationJsonEntry> parameters) {
        return new EnchantmentDataJsonEntry(enchantment, particle, priority, parameters);
    }
}