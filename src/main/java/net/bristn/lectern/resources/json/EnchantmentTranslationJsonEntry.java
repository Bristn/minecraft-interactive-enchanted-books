package net.bristn.lectern.resources.json;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.bristn.lectern.transformers.ModTransformers;
import net.bristn.lectern.transformers.SimpleValueTransformer;

public record EnchantmentTranslationJsonEntry(String name, SimpleValueTransformer transformer) {

    public static final Codec<EnchantmentTranslationJsonEntry> CODEC = RecordCodecBuilder.create(instance -> {
        var nameBuilder = Codec.STRING.fieldOf("name").forGetter(EnchantmentTranslationJsonEntry::name);
        var transformerBuilder = SimpleValueTransformer.CODEC.optionalFieldOf("transformer", ModTransformers.NONE)
                .forGetter(EnchantmentTranslationJsonEntry::transformer);

        return instance.group(nameBuilder, transformerBuilder).apply(instance, EnchantmentTranslationJsonEntry::new);
    });

}
