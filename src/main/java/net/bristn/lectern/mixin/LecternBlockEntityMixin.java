package net.bristn.lectern.mixin;

import net.bristn.lectern.LecternAccess;
import net.bristn.lectern.LecternEnchantedBooks;
import net.bristn.lectern.screen.handlers.LecternScreenHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LecternBlockEntity.class)
public abstract class LecternBlockEntityMixin extends BlockEntity implements LecternAccess {
    private final static String NBT_TAG = "TomeReader";

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
    ItemStack book;

    public LecternBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * Injects a custom callback when opening the lectern GUI. If the lectern contains an enchanted book, show a custom Screen which displays the book information
     * 
     * @param id
     * @param playerInventory
     * @param player
     * @param originalMethod
     */
    @Inject(method = "createMenu", at = @At("HEAD"), cancellable = true)
    private void openLectern(int id, Inventory playerInventory, Player player, CallbackInfoReturnable<AbstractContainerMenu> originalMethod) {
        LecternEnchantedBooks.LOGGER.info("A");

        var stack = this.book;
        var enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
        var entrySet = enchantments.entrySet();
        for (var entry : entrySet) {
            var enchantment = entry.getKey().value();
            var supported = enchantment.definition().supportedItems();
            var slots = enchantment.definition().slots();

            // TODO: Properly read the translation key (append .desc to use the translations of "enchantment descriptions")

            // Use a default key to provide text if there is no translation
            var translationKey = "enchantment.lectern-enchanted-books.no-description";

            // Try to read the translation key from the description contents
            var keyContent = enchantment.description().getContents();
            if (keyContent instanceof TranslatableContents translatable) {
                translationKey = translatable.getKey() + ".desc";

                // TODO: Check if level specific translations are present
            }

            //
            var translation = Component.translatable(translationKey).getString();
            LecternEnchantedBooks.LOGGER.info(translation);

        }

        Item bookItem = this.book.getItem();
        if (bookItem instanceof EnchantedBookItem) {
            // TODO: Open menu!
            originalMethod.setReturnValue(new LecternScreenHandler(id, playerInventory, this.bookAccess, this.worldPosition));
        }
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void writeLecternNbt(CompoundTag nbt, HolderLookup.Provider registryLookup, CallbackInfo ci) {
        nbt.putBoolean(NBT_TAG, isTomeReaderLectern);
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void readLecternNbt(CompoundTag nbt, HolderLookup.Provider registryLookup, CallbackInfo ci) {
        this.isTomeReaderLectern = nbt.getBoolean(NBT_TAG);
    }

    // --- Lectern Access interface

    @Override
    public int getTicks() {
        return ticks;
    }

    @Override
    public float getNextPageAngle() {
        return nextPageAngle;
    }

    @Override
    public float getPageAngle() {
        return pageAngle;
    }

    @Override
    public float getFlipRandom() {
        return flipRandom;
    }

    @Override
    public float getFlipTurn() {
        return flipTurn;
    }

    @Override
    public float getBookRotation() {
        return bookRotation;
    }

    @Override
    public float getTargetBookRotation() {
        return targetBookRotation;
    }

    @Override
    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    @Override
    public void setNextPageAngle(float nextPageAngle) {
        this.nextPageAngle = nextPageAngle;
    }

    @Override
    public void setPageAngle(float pageAngle) {
        this.pageAngle = pageAngle;
    }

    @Override
    public void setFlipRandom(float flipRandom) {
        this.flipRandom = flipRandom;
    }

    @Override
    public void setFlipTurn(float flipTurn) {
        this.flipTurn = flipTurn;
    }

    @Override
    public void setBookRotation(float bookRotation) {
        this.bookRotation = bookRotation;
    }

    @Override
    public void setLastBookRotation(float lastBookRotation) {
        this.lastBookRotation = lastBookRotation;
    }

    @Override
    public void setTargetBookRotation(float targetBookRotation) {
        this.targetBookRotation = targetBookRotation;
    }

    @Override
    public boolean isTomeReaderLectern() {
        return isTomeReaderLectern;
    }

    @Override
    public void setIsTomeReaderLectern(boolean val) {
        this.isTomeReaderLectern = val;
    }
}