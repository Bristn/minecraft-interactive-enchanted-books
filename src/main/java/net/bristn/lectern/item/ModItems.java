package net.bristn.lectern.item;

import net.bristn.lectern.LecternEnchantedBooks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

public class ModItems {

    // public static final Item testItem = registerItem("test_item", new Item(new
    // Item.Properties()));

    // private static Item registerItem(String name, Item item) {
    // return Registry.register(BuiltInRegistries.ITEM,
    // Identifier.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID, name), item);
    // }

    public static void registerModItems() {
        LecternEnchantedBooks.LOGGER.info("Register ModItems for" + LecternEnchantedBooks.MOD_ID);

        // ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries
        // -> {
        // entries.accept(testItem);
        // });
    }
}
