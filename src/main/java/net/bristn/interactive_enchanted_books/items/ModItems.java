package net.bristn.interactive_enchanted_books.items;

import java.util.function.Function;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public class ModItems {
    public static final Item ENCHANTMENT_ECHO = register( //
            "enchantment_echo", Item::new, //
            new Item.Properties().rarity(Rarity.RARE) //
    );

    public static void registerModItems() {
        CommonModInitializer.LOGGER.info("Register ModGameItems for" + CommonModInitializer.MOD_ID);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register((tab) -> {
            tab.accept(ModItems.ENCHANTMENT_ECHO);
        });
    }

    private static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        var identifier = Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID, name);
        var key = ResourceKey.create(Registries.ITEM, identifier);
        T item = itemFactory.apply(settings.setId(key));
        Registry.register(BuiltInRegistries.ITEM, key, item);
        return item;
    }
}
