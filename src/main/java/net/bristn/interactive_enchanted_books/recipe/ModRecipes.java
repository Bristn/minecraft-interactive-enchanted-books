package net.bristn.interactive_enchanted_books.recipe;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipes {

    private static Identifier identifier = Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID, "cloning");

    public static final RecipeSerializer<EnchantmentCloningRecipe> UPGRADING_RECIPE_SERIALIZER = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER, //
            identifier, //
            new RecipeSerializer<EnchantmentCloningRecipe>(EnchantmentCloningRecipe.MAP_CODEC,
                    EnchantmentCloningRecipe.STREAM_CODEC) //
    );

    public static final RecipeType<EnchantmentCloningRecipe> UPGRADING_RECIPE_TYPE = Registry.register( //
            BuiltInRegistries.RECIPE_TYPE, //
            identifier, //
            new RecipeType<EnchantmentCloningRecipe>() {
            } //
    );

    public static void registerModRecipes() {
        CommonModInitializer.LOGGER.info("Register ModParticles for" + CommonModInitializer.MOD_ID);
    }
}
