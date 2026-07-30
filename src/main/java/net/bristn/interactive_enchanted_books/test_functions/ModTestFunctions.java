package net.bristn.interactive_enchanted_books.test_functions;

import java.util.function.Consumer;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;

/**
 * Having the tests in the main package allows adding a json in the "test_instance" so the test may
 * be run visually in game which allows easier debugging
 */
public class ModTestFunctions {

    public static void registerModTestFunctions() {
        CommonModInitializer.LOGGER.info("Register ModTestFunctions " + CommonModInitializer.MOD_ID);

        registerCraftingTestFunctions();
        registerHopperTestFunctions();
        registerRedstoneTestFunctions();
    }

    private static void registerCraftingTestFunctions() {
        var CRAFTING_ALLOWED = getId("item_crafting_allowed");
        var CRAFTING_PREVENTED = getId("item_crafting_prevented");
        var PLACEABLE_IN_BOOKSHELF = getId("item_placeable_in_bookshelf");

        Registry.register(BuiltInRegistries.TEST_FUNCTION, CRAFTING_ALLOWED, (Consumer<GameTestHelper>) (helper) -> {
            var instance = new ItemFunctionTest();
            instance.craftingIsAllowed(helper);
        });

        Registry.register(BuiltInRegistries.TEST_FUNCTION, CRAFTING_PREVENTED, (Consumer<GameTestHelper>) (helper) -> {
            var instance = new ItemFunctionTest();
            instance.craftingIsPrevented(helper);
        });

        Registry.register(BuiltInRegistries.TEST_FUNCTION, PLACEABLE_IN_BOOKSHELF, (Consumer<GameTestHelper>) (helper) -> {
            var instance = new ItemFunctionTest();
            instance.placeableInBookshelf(helper);
        });
    }

    private static void registerHopperTestFunctions() {
        var INTERACTION_DISABLED = getId("hopper_interaction_disabled");
        var INTERACTION_ENABLED = getId("hopper_interaction_enabled");
        var INTERACTION_ENABLED_FOR_ECHO = getId("hopper_interaction_enabled_for_echo");

        Registry.register(BuiltInRegistries.TEST_FUNCTION, INTERACTION_DISABLED, (Consumer<GameTestHelper>) (helper) -> {
            var instance = new HopperFunctionTest();
            instance.interactionDisabled(helper);
        });

        Registry.register(BuiltInRegistries.TEST_FUNCTION, INTERACTION_ENABLED, (Consumer<GameTestHelper>) (helper) -> {
            var instance = new HopperFunctionTest();
            instance.interactionEnabled(helper);
        });

        Registry.register(BuiltInRegistries.TEST_FUNCTION, INTERACTION_ENABLED_FOR_ECHO, (Consumer<GameTestHelper>) (helper) -> {
            var instance = new HopperFunctionTest();
            instance.interactionEnabledForEcho(helper);
        });
    }

    private static void registerRedstoneTestFunctions() {
        var SIGNAL_CHANGE_ENABLED = getId("redstone_signal_change_enabled");
        var SIGNAL_CHANGE_DISABLED = getId("redstone_signal_change_disabled");
        var SIGNAL_CHANGE_ENABLED_REGULAR_BOOK = getId("redstone_signal_change_enabled_regular_book");
        var SIGNAL_cHANGE_DISABLED_REGULAR_BOOK = getId("redstone_signal_change_disabled_regular_book");

        Registry.register(BuiltInRegistries.TEST_FUNCTION, SIGNAL_CHANGE_ENABLED, (Consumer<GameTestHelper>) (helper) -> {
            var instance = new RedstoneFunctionTest();
            instance.signalChangeEnabled(helper);
        });

        Registry.register(BuiltInRegistries.TEST_FUNCTION, SIGNAL_CHANGE_DISABLED, (Consumer<GameTestHelper>) (helper) -> {
            var instance = new RedstoneFunctionTest();
            instance.signalChangeDisabled(helper);
        });

        Registry.register(BuiltInRegistries.TEST_FUNCTION, SIGNAL_CHANGE_ENABLED_REGULAR_BOOK,
                (Consumer<GameTestHelper>) (helper) -> {
                    var instance = new RedstoneFunctionTest();
                    instance.signalChangeEnabledRegularBook(helper);
                });

        Registry.register(BuiltInRegistries.TEST_FUNCTION, SIGNAL_cHANGE_DISABLED_REGULAR_BOOK,
                (Consumer<GameTestHelper>) (helper) -> {
                    var instance = new RedstoneFunctionTest();
                    instance.signalChangeDisabledRegularBook(helper);
                });
    }

    private static Identifier getId(String name) {
        return Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID, name);
    }
}
