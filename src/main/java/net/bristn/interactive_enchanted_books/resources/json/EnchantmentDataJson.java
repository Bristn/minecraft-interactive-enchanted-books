package net.bristn.interactive_enchanted_books.resources.json;

import java.util.ArrayList;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record EnchantmentDataJson(String enchantment, String particle, int priority,
        ArrayList<ArrayList<EnchantmentNamedParameterJson>> parameters) {

    public static final Codec<EnchantmentDataJson> CODEC = RecordCodecBuilder.create(instance -> {
        var enchantmentBuilder = Codec.STRING.fieldOf("enchantment").forGetter(EnchantmentDataJson::enchantment);
        var priorityBuilder = Codec.INT.optionalFieldOf("priority", 0).forGetter(EnchantmentDataJson::priority);
        var particleBuilder = Codec.STRING.optionalFieldOf("particle", "interactive_enchanted_books:enchant_particle")
                .forGetter(EnchantmentDataJson::particle);

        return instance.group(enchantmentBuilder, particleBuilder, priorityBuilder).apply(instance,
                (enchantment, particle, priority) -> {
                    return new EnchantmentDataJson(enchantment, particle, priority, new ArrayList<>());
                });
    });

    public EnchantmentDataJson withParameters(ArrayList<ArrayList<EnchantmentNamedParameterJson>> parameters) {
        return new EnchantmentDataJson(enchantment, particle, priority, parameters);
    }
}