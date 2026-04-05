package net.bristn.lectern;

import net.bristn.lectern.particle.ModParticles;
import net.bristn.lectern.payloads.ModPayloads;
import net.bristn.lectern.resources.loader.ModResourceLoaders;
import net.bristn.lectern.screen.ModScreens;
import net.bristn.lectern.tag.ModEnchantmentTags;
import net.bristn.lectern.tag.ModItemTags;
import net.bristn.lectern.transformers.ModTransformers;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LecternEnchantedBooks implements ModInitializer {
    public static final String MOD_ID = "lectern-enchanted-books";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModScreens.registerModScreens();
        ModParticles.registerModParticles();
        ModPayloads.registerModPayloads();
        ModResourceLoaders.registerModResourceLoaders();

        ModItemTags.registerModItemTags();
        ModEnchantmentTags.registerModEnchantmentTags();

        ModTransformers.registerModTransformers();
    }
}
