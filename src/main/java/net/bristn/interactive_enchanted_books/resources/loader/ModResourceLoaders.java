package net.bristn.interactive_enchanted_books.resources.loader;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public class ModResourceLoaders {

    public static void registerModResourceLoaders() {
        CommonModInitializer.LOGGER.info("Register ModResourceLoader for" + CommonModInitializer.MOD_ID);

        register("item_tag_texture_loader", new ItemTagTextureLoader());
        register("enchantment_particle_loader", new EnchantmentDataLoader());
    }

    private static void register(String name, PreparableReloadListener listener) {
        var identifier = Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID, name);
        var loader = ResourceLoader.get(PackType.CLIENT_RESOURCES);
        loader.registerReloadListener(identifier, listener);
    }
}
