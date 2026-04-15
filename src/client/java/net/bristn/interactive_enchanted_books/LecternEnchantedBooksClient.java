package net.bristn.interactive_enchanted_books;

import net.bristn.interactive_enchanted_books.particle.EnchantParticle;
import net.bristn.interactive_enchanted_books.particle.ModParticles;
import net.bristn.interactive_enchanted_books.payloads.ModPayloadListeners;
import net.bristn.interactive_enchanted_books.screen.LecternEnchantedBookViewScreen;
import net.bristn.interactive_enchanted_books.screen.ModScreens;
import net.bristn.interactive_enchanted_books.tag.ModItemTags;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.fabricmc.fabric.api.tag.client.v1.ClientTags;
import net.minecraft.client.gui.screens.MenuScreens;

public class LecternEnchantedBooksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ParticleProviderRegistry.getInstance().register(ModParticles.ENCHANT, EnchantParticle.ColorProvider::new);
        ParticleProviderRegistry.getInstance().register(ModParticles.CURSE, (sprites) -> {
            return new EnchantParticle.ColorProvider(sprites, 0.570f, 0.148f, 0.148f);
        });

        // Register the client mod screens
        MenuScreens.register(ModScreens.MENU, LecternEnchantedBookViewScreen::new);

        // REgister the client payload listener
        ModPayloadListeners.registerModPayloadListeners();

        // Ensure fabric knows about the client side item tags used by the menu
        ClientPlayConnectionEvents.JOIN.register((listener, sender, client) -> {
            LecternEnchantedBooks.LOGGER.info("Register client-side tags for" + LecternEnchantedBooks.MOD_ID);

            for (var tagKey : ModItemTags.ALL_TAG_KEYS) {
                ClientTags.getOrCreateLocalTag(tagKey);
            }
        });
    }
}
