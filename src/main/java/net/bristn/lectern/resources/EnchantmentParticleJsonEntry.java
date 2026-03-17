package net.bristn.lectern.resources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record EnchantmentParticleJsonEntry(String enchantment, String particle, int priority) {

        public static final Codec<EnchantmentParticleJsonEntry> CODEC = RecordCodecBuilder.create(instance -> instance
                        .group(
                                        Codec.STRING.fieldOf("enchantment")
                                                        .forGetter(EnchantmentParticleJsonEntry::enchantment),
                                        Codec.STRING.fieldOf("particle")
                                                        .forGetter(EnchantmentParticleJsonEntry::particle),
                                        Codec.INT.fieldOf("priority").forGetter(EnchantmentParticleJsonEntry::priority))
                        .apply(instance, EnchantmentParticleJsonEntry::new));
}