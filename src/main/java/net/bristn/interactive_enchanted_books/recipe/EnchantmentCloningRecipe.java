package net.bristn.interactive_enchanted_books.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.bristn.interactive_enchanted_books.gamerules.ModGameRules;
import net.bristn.interactive_enchanted_books.utility.EnchantmentUtility;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.TransmuteRecipe;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public class EnchantmentCloningRecipe extends CustomRecipe {

    public static final MapCodec<EnchantmentCloningRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(//
            Ingredient.CODEC.fieldOf("material").forGetter(o -> o.material), //
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(o -> o.result) //
    ).apply(i, EnchantmentCloningRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentCloningRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, o -> o.material, //
            ItemStackTemplate.STREAM_CODEC, o -> o.result, //
            EnchantmentCloningRecipe::new //
    );

    private final Ingredient material;
    private final ItemStackTemplate result;

    public EnchantmentCloningRecipe(Ingredient material, ItemStackTemplate result) {
        this.material = material;
        this.result = result;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.ingredientCount() < 2) {
            return false;
        }

        // Check the game rule if crafting is allowed
        if (level instanceof ServerLevel serverLevel) {
            var craftable = serverLevel.getGameRules().get(ModGameRules.CRAFTABLE_ENCHANTMENT_ECHO);
            if (craftable == false) {
                return false;
            }
        }

        var hasMaterial = false;
        var hasSource = false;
        for (var slot = 0; slot < input.size(); ++slot) {
            var itemStack = input.getItem(slot);
            if (itemStack.isEmpty()) {
                continue;
            }

            if (EnchantmentUtility.hasEnchantments(itemStack)) {
                if (hasSource) {
                    return false;
                }

                hasSource = true;
                continue;
            }

            if (this.material.test(itemStack)) {
                hasMaterial = true;
                continue;
            }

            return false;
        }

        return hasSource && hasMaterial;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        var count = 0;
        var source = ItemStack.EMPTY;

        for (var slot = 0; slot < input.size(); ++slot) {
            var itemStack = input.getItem(slot);
            if (itemStack.isEmpty()) {
                continue;
            }

            if (EnchantmentUtility.hasEnchantments(itemStack)) {
                if (!source.isEmpty()) {
                    return ItemStack.EMPTY;
                }

                source = itemStack;
                continue;
            }

            if (this.material.test(itemStack)) {
                ++count;
                continue;
            }

            return ItemStack.EMPTY;
        }

        var enchantments = EnchantmentUtility.getEnchantments(source);
        if (enchantments == null) {
            return ItemStack.EMPTY;
        }

        var copy = new ItemEnchantments.Mutable(enchantments).toImmutable();
        var result = TransmuteRecipe.createWithOriginalComponents(this.result, source, count - 1);
        result.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        result.set(DataComponents.STORED_ENCHANTMENTS, copy);
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        var result = NonNullList.withSize(input.size(), ItemStack.EMPTY);

        for (var slot = 0; slot < result.size(); ++slot) {
            var itemStack = input.getItem(slot);
            var remainder = itemStack.getItem().getCraftingRemainder();
            if (remainder != null) {
                result.set(slot, remainder.create());
                continue;
            }

            if (EnchantmentUtility.hasEnchantments(itemStack)) {
                result.set(slot, itemStack.copyWithCount(1));
                break;
            }
        }

        return result;
    }

    @Override
    public RecipeSerializer<EnchantmentCloningRecipe> getSerializer() {
        return ModRecipes.UPGRADING_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public boolean isSpecial() {
        return false;
    }

    @Override
    public boolean showNotification() {
        return true;
    }

    @Override
    public String group() {
        return "cloning";
    }
}
