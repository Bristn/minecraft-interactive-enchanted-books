package net.bristn.lectern.transformers;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.bristn.lectern.LecternEnchantedBooks;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

public class ValueTransformer {
    private static final Identifier ID = Identifier.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID, "value_transformers");

    /**
     * Get or registers the registry for the value transformers. If another mod
     * implements a value transformer, the load order of the mods might differ. This
     * ensures that the first loaded mod creates the registry and the others can
     * access it without a redefinition error
     */
    @SuppressWarnings("unchecked")
    public static Registry<Function<Float, Float>> getOrCreateRegistry() {
        var ref = BuiltInRegistries.REGISTRY.get(ID);
        if (ref.isEmpty()) {
            var key = ResourceKey.createRegistryKey(ID);
            var registry = (Object) FabricRegistryBuilder.create(key).buildAndRegister();
            return (Registry<Function<Float, Float>>) registry;
        }

        return (Registry<Function<Float, Float>>) ref.get().value();
    }

    public static Codec<Function<Float, Float>> getCodec() {
        var registry = getOrCreateRegistry();
        return registry.byNameCodec();
    }
}