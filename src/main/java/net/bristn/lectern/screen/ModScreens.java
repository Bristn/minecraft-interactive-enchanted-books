package net.bristn.lectern.screen;

import net.bristn.lectern.LecternEnchantedBooks;
import net.bristn.lectern.screen.handlers.LecternScreenHandler;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public class ModScreens {

	/**
	 * Shorthand for registering a screen handler with the given name
	 * 
	 * @param name    - The name/identifier of the screen
	 * @param handler - The handler for the screen
	 */
	private static void registerScreen(String name, MenuType<LecternScreenHandler> handler) {
		ResourceLocation id = ResourceLocation.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID, name);
		Registry.register(BuiltInRegistries.MENU, id, handler);
	}

	public static void registerModScreens() {
		LecternEnchantedBooks.LOGGER.info("Register ModScreens for" + LecternEnchantedBooks.MOD_ID);

		// Lectern screen
		{
			String name = "lectern_enchanted_book";
			ModScreens.registerScreen(name, LecternScreenHandler.SCREEN_HANDLER);
		}
	}
}
