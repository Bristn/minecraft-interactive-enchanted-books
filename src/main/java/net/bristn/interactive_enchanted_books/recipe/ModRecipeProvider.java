package net.bristn.interactive_enchanted_books.recipe;

import java.util.concurrent.CompletableFuture;

import net.bristn.interactive_enchanted_books.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(Provider registries, BootstrapContext<Recipe<?>> recipes,
            BootstrapContext<Advancement> advancements) {
        return new RecipeProvider(recipes, advancements) {
            @Override
            public void buildRecipes() {
                SpecialRecipeBuilder.special(() -> {
                    var material = Ingredient.of(Items.BOOK);
                    var result = new ItemStackTemplate(ModItems.ENCHANTMENT_ECHO);
                    return new EnchantmentCloningRecipe(material, result);
                }).save(this.output, "enchantment_echo");
            }
        };
    }

    @Override
    public String getName() {
        return "InteractiveEnchantedBooksRecipeProvider";
    }

}
