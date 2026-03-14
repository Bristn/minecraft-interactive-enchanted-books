package net.bristn.lectern.mixin;

import net.bristn.lectern.LecternEnchantedBooks;
import net.bristn.lectern.data.TestDataEntry;
import net.bristn.lectern.screen.handlers.LecternScreenHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LecternBlockEntity.class)
public abstract class LecternBlockEntityMixin extends BlockEntity {
    private static final Logger LOGGER = LecternEnchantedBooks.LOGGER;

    public int ticks;
    public float nextPageAngle;
    public float pageAngle;
    public float flipRandom;
    public float flipTurn;
    public float bookRotation;
    public float lastBookRotation;
    public float targetBookRotation;
    public boolean isTomeReaderLectern;

    @Shadow
    Container bookAccess;

    @Shadow
    ContainerData dataAccess;

    @Shadow
    ItemStack book;

    public LecternBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * Injects a custom callback when opening the lectern GUI. If the lectern
     * contains an enchanted book, show a custom Screen which displays the book
     * information
     * 
     * @param id
     * @param playerInventory
     * @param player
     * @param originalMethod
     */
    @Inject(method = "createMenu", at = @At("HEAD"), cancellable = true)
    private void openLectern(int id, Inventory playerInventory, Player player,
            CallbackInfoReturnable<AbstractContainerMenu> originalMethod) {

        var stack = this.book;
        LOGGER.info(stack.toString());

        var enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
        var entrySet = enchantments.entrySet();
        for (var entry : entrySet) {
            var enchantmentLevel = entry.getIntValue();
            var enchantment = entry.getKey().value();
            var definition = enchantment.definition();

            var exclusive = enchantment.exclusiveSet();
            var supported = definition.supportedItems();

            TestDataEntry.getSupportedItemTextures(supported);

            // Get the translated description
            // var description = getEnchantmentDescription(enchantment, enchantmentLevel);

            // Determine the icons for the supported items

            for (var entry2 : exclusive) {
                // LecternEnchantedBooks.LOGGER.info(entry2.toString());
            }

            break;
        }

        Item bookItem = this.book.getItem();
        if (bookItem == Items.ENCHANTED_BOOK) {
            // container.setItem(0, stack);
            originalMethod
                    .setReturnValue(new LecternScreenHandler(id, playerInventory, this.bookAccess, this.worldPosition));
        }
    }

    /**
     * Uses the TagKey of the exclusive set to get a list of Enchantments contained
     * in this exclusive set
     * 
     * @param exclusiveSet
     * @return
     */
    private ArrayList<Enchantment> getEnchantmentsOfExclusiveSet(TagKey<Enchantment> exclusiveSet) {
        ArrayList<Enchantment> values = new ArrayList<>();
        var registryAccess = this.level.registryAccess();
        var registry = registryAccess.lookup(Registries.ENCHANTMENT).orElseThrow();
        var tags = registry.getTagOrEmpty(exclusiveSet);
        for (var tag : tags) {
            var value = tag.value();
            values.add(value);
        }

        return values;
    }

    /**
     * Uses the TagKey of the supported items set to get a list of supported items
     * 
     * @param supportedSet
     * @return
     */
    private ArrayList<Item> getEnchantableItemsOfSet(TagKey<Item> supportedSet) {
        ArrayList<Item> values = new ArrayList<>();
        var registryAccess = this.level.registryAccess();
        var registry = registryAccess.lookup(Registries.ITEM).orElseThrow();
        var tags = registry.getTagOrEmpty(supportedSet);
        for (var tag : tags) {
            var value = tag.value();
            values.add(value);
        }

        return values;
    }
}