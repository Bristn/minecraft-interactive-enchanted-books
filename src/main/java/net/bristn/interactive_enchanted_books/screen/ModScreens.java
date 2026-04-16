package net.bristn.interactive_enchanted_books.screen;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.bristn.interactive_enchanted_books.screen.handlers.LecternEnchantedBookMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ModScreens {
    public static final MenuType<LecternEnchantedBookMenu> MENU = register("lectern_enchanted_book",
            (id, inv) -> new LecternEnchantedBookMenu(id));

    public static void registerModScreens() {
        CommonModInitializer.LOGGER.info("Register ModScreens for" + CommonModInitializer.MOD_ID);
    }

    private static <T extends AbstractContainerMenu> MenuType<T> register(String name, MenuType.MenuSupplier<T> constructor) {
        return Registry.register(BuiltInRegistries.MENU, name, new MenuType<>(constructor, FeatureFlagSet.of()));
    }
}
