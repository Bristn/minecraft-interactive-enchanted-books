package net.bristn.interactive_enchanted_books;

import net.bristn.interactive_enchanted_books.items.ModItems;
import net.bristn.interactive_enchanted_books.particle.EnchantParticle;
import net.bristn.interactive_enchanted_books.particle.ModParticles;
import net.bristn.interactive_enchanted_books.payloads.ModPayloadListeners;
import net.bristn.interactive_enchanted_books.screen.LecternEnchantedBookViewScreen;
import net.bristn.interactive_enchanted_books.screen.ModScreens;
import net.bristn.interactive_enchanted_books.tag.ModItemTags;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

public class ClientOnlyModInitializer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ParticleProviderRegistry.getInstance().register(ModParticles.ENCHANT, EnchantParticle.ColorProvider::new);
        ParticleProviderRegistry.getInstance().register(ModParticles.CURSE, (sprites) -> {
            return new EnchantParticle.ColorProvider(sprites, 0.570f, 0.148f, 0.148f);
        });

        for (var particle : ModParticles.ENCHANTMENT_PARTICLES) {
            ParticleProviderRegistry.getInstance().register(particle, EnchantParticle.ColorProvider::new);
        }

        // Register the client mod screens
        MenuScreens.register(ModScreens.MENU, LecternEnchantedBookViewScreen::new);

        // Register the client payload listener
        ModPayloadListeners.registerModPayloadListeners();

        // Ensure fabric knows about the client side item tags used by the menu
        ClientPlayConnectionEvents.JOIN.register((listener, sender, client) -> {
            CommonModInitializer.LOGGER.info("Register client-side tags for" + CommonModInitializer.MOD_ID);

            for (var tagKey : ModItemTags.ALL_TAG_KEYS) {
                ClientTags.getOrCreateLocalTag(tagKey);
            }
        });

        // When disconnecting from a server, reset the static flags
        ClientPlayConnectionEvents.DISCONNECT.register((listener, client) -> {
            CommonModInitializer.areEchosCraftable = false;
            CommonModInitializer.isInstalledOnServer = false;
        });

        // Show a custom tooltip to indicate the enchanted books are openable
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            if (stack.is(Items.ENCHANTED_BOOK) || stack.is(ModItems.ENCHANTMENT_ECHO)) {
                var text = Component.translatable("gui.interactive_enchanted_books.openable_tooltip");
                lines.add(text.withColor(ChatFormatting.DARK_GREEN.getColor()));
            }
        });
    }
}
