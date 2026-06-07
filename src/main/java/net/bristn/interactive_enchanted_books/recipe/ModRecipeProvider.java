package net.bristn.interactive_enchanted_books.recipe;

import java.util.concurrent.CompletableFuture;

import net.bristn.interactive_enchanted_books.items.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
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
