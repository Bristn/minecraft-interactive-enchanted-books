package net.bristn.lectern.resources.json;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record EnchantmentTranslationJsonEntry(String name) {

    public static final Codec<EnchantmentTranslationJsonEntry> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(Codec.STRING.fieldOf("name").forGetter(EnchantmentTranslationJsonEntry::name))
                    .apply(instance, EnchantmentTranslationJsonEntry::new));

}
