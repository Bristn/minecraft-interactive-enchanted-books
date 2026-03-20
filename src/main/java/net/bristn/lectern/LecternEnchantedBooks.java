package net.bristn.lectern;

import net.bristn.lectern.item.ModItems;
import net.bristn.lectern.particle.ModParticles;
import net.bristn.lectern.payloads.ModPayloads;
import net.bristn.lectern.resources.loader.ModResourceLoaders;
import net.bristn.lectern.screen.ModScreens;
import net.bristn.lectern.tag.ModItemTags;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;

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
		ModParticles.registerModParticles();
		ModPayloads.registerModPayloads();
		ModResourceLoaders.registerModResourceLoaders();
		ModItemTags.registerModItemTags();
	}
}
