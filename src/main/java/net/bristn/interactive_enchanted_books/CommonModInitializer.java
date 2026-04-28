package net.bristn.interactive_enchanted_books;

import net.bristn.interactive_enchanted_books.gamerules.ModGameRules;
import net.bristn.interactive_enchanted_books.particle.ModParticles;
import net.bristn.interactive_enchanted_books.payloads.ModPayloads;
import net.bristn.interactive_enchanted_books.resources.loader.ModResourceLoaders;
import net.bristn.interactive_enchanted_books.screen.ModScreens;
import net.bristn.interactive_enchanted_books.tag.ModEnchantmentTags;
import net.bristn.interactive_enchanted_books.tag.ModItemTags;
import net.bristn.interactive_enchanted_books.transformers.ModTransformers;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonModInitializer implements ModInitializer {
    public static final String MOD_ID = "interactive_enchanted_books";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModScreens.registerModScreens();
        ModParticles.registerModParticles();
        ModPayloads.registerModPayloads();
        ModGameRules.registerModGameRules();

        ModItemTags.registerModItemTags();
        ModEnchantmentTags.registerModEnchantmentTags();

        ModTransformers.registerModTransformers();
        ModResourceLoaders.registerModResourceLoaders();
    }
}
