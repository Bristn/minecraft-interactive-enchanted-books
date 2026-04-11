package net.bristn.lectern.resources.json;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.bristn.lectern.transformers.ModTransformers;
import net.bristn.lectern.transformers.ValueTransformer;

public record EnchantmentNamedParameterJson(String name, Function<Float, Float> transformer) {

    public static final Codec<EnchantmentNamedParameterJson> CODEC = RecordCodecBuilder.create(instance -> {
        var nameBuilder = Codec.STRING.fieldOf("name").forGetter(EnchantmentNamedParameterJson::name);
        var codec = ValueTransformer.getCodec();
        var transformerBuilder = codec.optionalFieldOf("transformer", ModTransformers.NONE)
                .forGetter(EnchantmentNamedParameterJson::transformer);

        return instance.group(nameBuilder, transformerBuilder).apply(instance, EnchantmentNamedParameterJson::new);
    });

}
