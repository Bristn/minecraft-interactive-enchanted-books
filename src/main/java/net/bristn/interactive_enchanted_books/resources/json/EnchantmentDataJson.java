package net.bristn.interactive_enchanted_books.resources.json;

import java.util.ArrayList;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record EnchantmentDataJson(String enchantment, String particle, int priority,
        ArrayList<ArrayList<EnchantmentNamedParameterJson>> parameters, boolean isCurse) {

    public static final Codec<EnchantmentDataJson> CODEC = RecordCodecBuilder.create(instance -> {
        var enchantmentBuilder = Codec.STRING.fieldOf("enchantment").forGetter(EnchantmentDataJson::enchantment);
        var priorityBuilder = Codec.INT.optionalFieldOf("priority", 0).forGetter(EnchantmentDataJson::priority);
        var isCurseBoolean = Codec.BOOL.optionalFieldOf("isCurse", false).forGetter(EnchantmentDataJson::isCurse);
        var particleBuilder = Codec.STRING.optionalFieldOf("particle", "").forGetter(EnchantmentDataJson::particle);

        return instance.group(enchantmentBuilder, particleBuilder, priorityBuilder, isCurseBoolean).apply(instance,
                (enchantment, particle, priority, isCurse) -> {

                    // If no particle is defined, use the enchant or curse particles
                    if (particle.length() == 0) {
                        particle = isCurse ? "interactive_enchanted_books:curse_particle"
                                : "interactive_enchanted_books:enchant_particle";
                    }

                    return new EnchantmentDataJson(enchantment, particle, priority, new ArrayList<>(), isCurse);
                });
    });

    public EnchantmentDataJson withParameters(ArrayList<ArrayList<EnchantmentNamedParameterJson>> parameters) {
        return new EnchantmentDataJson(enchantment, particle, priority, parameters, isCurse);
    }
}