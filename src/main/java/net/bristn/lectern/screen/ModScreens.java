package net.bristn.lectern.screen;

import net.bristn.lectern.LecternEnchantedBooks;
import net.bristn.lectern.screen.handlers.LecternEnchantedBookMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class ModScreens {
    public static final MenuType<LecternEnchantedBookMenu> MENU = register("dirt_chest",
            (id, inv) -> new LecternEnchantedBookMenu(id));

    public static void registerModScreens() {
        LecternEnchantedBooks.LOGGER.info("Register ModScreens for" + LecternEnchantedBooks.MOD_ID);
    }

    private static <T extends AbstractContainerMenu> MenuType<T> register(String name, MenuType.MenuSupplier<T> constructor) {
        return Registry.register(BuiltInRegistries.MENU, name, new MenuType<>(constructor, FeatureFlagSet.of()));
    }
}
