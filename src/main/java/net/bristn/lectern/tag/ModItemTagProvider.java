package net.bristn.lectern.tag;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.Items;

public class ModItemTagProvider extends FabricTagsProvider.ItemTagsProvider {

    public ModItemTagProvider(FabricPackOutput output, CompletableFuture<Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(Provider registries) {
        valueLookupBuilder(ModItemTags.BOWS).add(Items.BOW).setReplace(false);

        valueLookupBuilder(ModItemTags.CROSSBOWS).add(Items.CROSSBOW).setReplace(false);

        valueLookupBuilder(ModItemTags.ELYTRAS).add(Items.ELYTRA).setReplace(false);

        valueLookupBuilder(ModItemTags.SHIELDS).add(Items.SHIELD).setReplace(false);

        valueLookupBuilder(ModItemTags.TRIDENTS).add(Items.TRIDENT).setReplace(false);

        valueLookupBuilder(ModItemTags.MACES).add(Items.MACE).setReplace(false);

        valueLookupBuilder(ModItemTags.MISC).add(Items.FLINT_AND_STEEL).setReplace(false);

        valueLookupBuilder(ModItemTags.MISC).add(Items.BRUSH).add(Items.SHEARS).add(Items.FISHING_ROD)
                .add(Items.WARPED_FUNGUS_ON_A_STICK).add(Items.CARROT_ON_A_STICK).add(Items.COMPASS).setReplace(false);

        valueLookupBuilder(ModItemTags.SKULLS).add(Items.CARVED_PUMPKIN).add(Items.PLAYER_HEAD).add(Items.SKELETON_SKULL)
                .add(Items.CREEPER_HEAD).add(Items.ZOMBIE_HEAD).add(Items.DRAGON_HEAD).add(Items.PIGLIN_HEAD)
                .add(Items.WITHER_SKELETON_SKULL).setReplace(false);
    }
}
