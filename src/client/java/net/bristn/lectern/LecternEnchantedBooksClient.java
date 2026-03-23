package net.bristn.lectern;

import net.bristn.lectern.particle.ModParticles;
import net.bristn.lectern.particle.SparkleParticle;
import net.bristn.lectern.payloads.ModPayloadListeners;
import net.bristn.lectern.screen.LecternEnchantedBookScreen;
import net.bristn.lectern.screen.handlers.LecternScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.client.gui.screens.MenuScreens;

public class LecternEnchantedBooksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ParticleProviderRegistry.getInstance().register(ModParticles.SPARKLE_PARTICLE, SparkleParticle.Provider::new);

        MenuScreens.register(LecternScreenHandler.SCREEN_HANDLER, LecternEnchantedBookScreen::new);

        ModPayloadListeners.registerModPayloadListeners();

    }
}
