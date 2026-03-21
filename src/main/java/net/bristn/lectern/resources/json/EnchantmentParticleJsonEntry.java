package net.bristn.lectern.resources.json;

import java.util.ArrayList;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record EnchantmentParticleJsonEntry(String enchantment, String particle, int priority,
        ArrayList<EnchantmentTranslationJsonEntry> parameters) {

    public static final Codec<EnchantmentParticleJsonEntry> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Codec.STRING.fieldOf("enchantment").forGetter(EnchantmentParticleJsonEntry::enchantment),
                    Codec.STRING.fieldOf("particle").forGetter(EnchantmentParticleJsonEntry::particle),
                    Codec.INT.fieldOf("priority").forGetter(EnchantmentParticleJsonEntry::priority))
            .apply(instance, (enchantment, particle, priority) -> new EnchantmentParticleJsonEntry(enchantment, particle,
                    priority, new ArrayList<>())));

    public EnchantmentParticleJsonEntry withParameters(ArrayList<EnchantmentTranslationJsonEntry> parameters) {
        return new EnchantmentParticleJsonEntry(enchantment, particle, priority, parameters);
    }
}