package net.bristn.interactive_enchanted_books.tag.generator;

import java.util.concurrent.CompletableFuture;

import net.bristn.interactive_enchanted_books.tag.ModItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.references.BlockItemIds;
import net.minecraft.references.ItemIds;

/**
 * Adds custom item tags to group certain items into one icon in the supported item section. Vanilla
 * has some tags like "SWORDS", "AXES", etc, but some item groups don't have these tags. Therefore
 * these are created here
 */
public class ModItemTagProvider extends FabricTagsProvider.ItemTagsProvider {

    public ModItemTagProvider(FabricPackOutput output, CompletableFuture<Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(Provider registries) {
        builder(ModItemTags.BOWS).add(ItemIds.BOW).setReplace(false);

        builder(ModItemTags.CROSSBOWS).add(ItemIds.CROSSBOW).setReplace(false);

        builder(ModItemTags.ELYTRAS).add(ItemIds.ELYTRA).setReplace(false);

        builder(ModItemTags.SHIELDS).add(ItemIds.SHIELD).setReplace(false);

        builder(ModItemTags.TRIDENTS).add(ItemIds.TRIDENT).setReplace(false);

        builder(ModItemTags.MACES).add(ItemIds.MACE).setReplace(false);

        builder(ModItemTags.MISC).add(ItemIds.FLINT_AND_STEEL).setReplace(false);

        builder(ModItemTags.MISC).add(ItemIds.BRUSH).add(ItemIds.SHEARS).add(ItemIds.FISHING_ROD)
                .add(ItemIds.WARPED_FUNGUS_ON_A_STICK).add(ItemIds.CARROT_ON_A_STICK).add(ItemIds.COMPASS).setReplace(false);

        builder(ModItemTags.SKULLS).add(BlockItemIds.CARVED_PUMPKIN).add(BlockItemIds.PLAYER_HEAD)
                .add(BlockItemIds.SKELETON_SKULL).add(BlockItemIds.CREEPER_HEAD).add(BlockItemIds.ZOMBIE_HEAD)
                .add(BlockItemIds.DRAGON_HEAD).add(BlockItemIds.PIGLIN_HEAD).add(BlockItemIds.WITHER_SKELETON_SKULL)
                .setReplace(false);
    }
}
