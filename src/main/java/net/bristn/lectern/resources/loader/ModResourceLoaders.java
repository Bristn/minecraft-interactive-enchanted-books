package net.bristn.lectern.resources.loader;

import net.bristn.lectern.LecternEnchantedBooks;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public class ModResourceLoaders {

    public static void registerModResourceLoaders() {
        register("item_tag_texture_loader", new ItemTagTextureLoader());
        register("enchantment_particle_loader", new EnchantmentDataLoader());
    }

    private static void register(String name, PreparableReloadListener listener) {
        var identifier = Identifier.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID, name);
        var loader = ResourceLoader.get(PackType.CLIENT_RESOURCES);
        loader.registerReloadListener(identifier, listener);
    }
}
