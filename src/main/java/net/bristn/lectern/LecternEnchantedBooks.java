package net.bristn.lectern;

import net.bristn.lectern.item.ModItems;
import net.bristn.lectern.screen.ModScreens;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
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
	}

	public static LecternAccess getAccess(LecternBlockEntity blockEntity) {
		return (LecternAccess) blockEntity;
	}
}