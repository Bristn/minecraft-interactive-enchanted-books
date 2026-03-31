package net.bristn.lectern;

import net.bristn.lectern.particle.ModParticles;
import net.bristn.lectern.particle.EnchantParticle;
import net.bristn.lectern.payloads.ModPayloadListeners;
import net.bristn.lectern.screen.LecternEnchantedBookViewScreen;
import net.bristn.lectern.screen.ModScreens;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.client.gui.screens.MenuScreens;

public class LecternEnchantedBooksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ParticleProviderRegistry.getInstance().register(ModParticles.CURSE, (sprites) -> {
            return new EnchantParticle.ColorProvider(sprites, 0.570f, 0.148f, 0.148f);
        });

        ParticleProviderRegistry.getInstance().register(ModParticles.ENCHANT, EnchantParticle.ColorProvider::new);

        // Register the client mod screens
        MenuScreens.register(ModScreens.MENU, LecternEnchantedBookViewScreen::new);

        // REgister the client payload listener
        ModPayloadListeners.registerModPayloadListeners();
    }
}
