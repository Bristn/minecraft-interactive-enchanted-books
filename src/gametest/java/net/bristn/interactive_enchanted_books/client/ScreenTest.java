package net.bristn.interactive_enchanted_books.client;

import com.mojang.blaze3d.platform.NativeImage;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.bristn.interactive_enchanted_books.items.ModItems;
import net.bristn.interactive_enchanted_books.screen.EnchantedBookAccess;
import net.bristn.interactive_enchanted_books.screen.EnchantedBookViewScreen;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotComparisonOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;

@SuppressWarnings("UnstableApiUsage")
public class ScreenTest implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        var singlePlayer = context.worldBuilder().create();
        singlePlayer.getConnection().waitForChunksRender();

        testBothEnabled(context);
        testBothDisabled(context);
        testEchoDisabled(context);

        context.runOnClient(client -> {
            client.disconnectFromWorld(Component.literal(""));
        });
    }

    private static void testBothEnabled(ClientGameTestContext context) {
        var book = getEnchantedItem(Items.ENCHANTED_BOOK, context);
        context.runOnClient(client -> {
            var access = EnchantedBookAccess.fromItem(book);
            client.execute(() -> {
                client.setScreenAndShow(new EnchantedBookViewScreen(access));
            });
        });

        // Validate the particle info
        context.assertScreenshotContains(getImageTemplate("particle_info_button.png"));
        context.clickScreenButton(EnchantedBookViewScreen.BUTTON_PARTICLE_KEY);
        context.assertScreenshotContains(getImageTemplate("particle_info_panel.png"));

        // Validate the echo info
        context.assertScreenshotContains(getImageTemplate("echo_info_button.png"));
        context.clickScreenButton(EnchantedBookViewScreen.BUTTON_ECHO_KEY);
        context.assertScreenshotContains(getImageTemplate("echo_info_panel.png"));

        // Validate the right page layout
        context.assertScreenshotContains(getImageTemplate("book_overview.png"));
        context.clickScreenButton("book.page_button.next");
        context.assertScreenshotContains(getImageTemplate("particle_header.png"));

        context.runOnClient(client -> {
            client.execute(() -> {
                client.setScreenAndShow(null);
            });
        });
    }

    private static void testBothDisabled(ClientGameTestContext context) {
        CommonModInitializer.areEchosCraftable = false;
        CommonModInitializer.isInstalledOnServer = false;

        var book = getEnchantedItem(Items.ENCHANTED_BOOK, context);
        context.runOnClient(client -> {
            var access = EnchantedBookAccess.fromItem(book);
            client.execute(() -> {
                client.setScreenAndShow(new EnchantedBookViewScreen(access));
            });
        });

        context.assertScreenshotContains(getImageTemplate("not_on_server_overview.png"));
        context.clickScreenButton("book.page_button.next");
        context.assertScreenshotContains(getImageTemplate("not_on_server_sharpness.png"));

        context.runOnClient(client -> {
            client.execute(() -> {
                client.setScreenAndShow(null);
            });
        });
    }

    private static void testEchoDisabled(ClientGameTestContext context) {
        CommonModInitializer.areEchosCraftable = false;
        CommonModInitializer.isInstalledOnServer = false;

        var book = getEnchantedItem(ModItems.ENCHANTMENT_ECHO, context);
        context.runOnClient(client -> {
            var access = EnchantedBookAccess.fromItem(book);
            client.execute(() -> {
                client.setScreenAndShow(new EnchantedBookViewScreen(access));
            });
        });

        context.assertScreenshotContains(getImageTemplate("echo_book_texture.png"));

        context.runOnClient(client -> {
            client.execute(() -> {
                client.setScreenAndShow(null);
            });
        });
    }

    /**
     * Helper method to read an image used for assertion if a given ui element is present
     */
    private static TestScreenshotComparisonOptions getImageTemplate(String filename) {
        var basePath = "/assets/interactive_enchanted_books/";
        try {
            var stream = ScreenTest.class.getResourceAsStream(basePath + filename);
            if (stream == null) {
                throw new IllegalStateException("Resource not found: " + basePath + filename);
            }

            return TestScreenshotComparisonOptions.of(NativeImage.read(stream));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load comparison image", e);
        }
    }

    private static ItemStack getEnchantedItem(Item item, ClientGameTestContext context) {
        var stack = item.getDefaultInstance();

        context.runOnClient(client -> {
            var lookup = client.level.holderLookup(Registries.ENCHANTMENT);
            var aquaAffinity = lookup.get(Enchantments.AQUA_AFFINITY).get();
            var sharpness = lookup.get(Enchantments.SHARPNESS).get();
            stack.enchant(aquaAffinity, 1);
            stack.enchant(sharpness, 1);
        });

        return stack;
    }

}