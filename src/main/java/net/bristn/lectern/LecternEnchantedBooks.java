package net.bristn.lectern;

import net.bristn.lectern.data.EnchantmentParticleLoader;
import net.bristn.lectern.data.ItemTagTextureLoader;
import net.bristn.lectern.item.ModItems;
import net.bristn.lectern.screen.ModScreens;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LecternEnchantedBooks implements ModInitializer {
	public static final String MOD_ID = "lectern-enchanted-books";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Identifier ITEM_SYNC = Identifier.fromNamespaceAndPath(MOD_ID, "item_sync");

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModScreens.registerModScreens();

		var textureLoader = Identifier.fromNamespaceAndPath(MOD_ID, "item_tag_texture_loader");
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(
				textureLoader,
				new ItemTagTextureLoader());

		var particleLoader = Identifier.fromNamespaceAndPath(MOD_ID, "enchantment_particle_loader");
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(
				particleLoader,
				new EnchantmentParticleLoader());
	}
}
