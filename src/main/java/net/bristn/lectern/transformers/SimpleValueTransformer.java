package net.bristn.lectern.transformers;

import com.mojang.serialization.Codec;

import net.bristn.lectern.LecternEnchantedBooks;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class SimpleValueTransformer {
    private static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID,
            "simple_value_transformers");

    private static final ResourceKey<Registry<SimpleValueTransformer>> KEY = ResourceKey.createRegistryKey(IDENTIFIER);
    public static final Registry<SimpleValueTransformer> REGISTRY = FabricRegistryBuilder.create(KEY).buildAndRegister();
    public static final Codec<SimpleValueTransformer> CODEC = REGISTRY.byNameCodec();

    private final SimpleValueTransformerImpl transformer;

    public SimpleValueTransformer(SimpleValueTransformerImpl transformer) {
        this.transformer = transformer;
    }

    public float apply(float value) {
        return transformer.apply(value);
    }
}