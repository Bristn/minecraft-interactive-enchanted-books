package net.bristn.lectern.mixin;

import net.bristn.lectern.LecternEnchantedBooks;
import net.bristn.lectern.screen.handlers.LecternScreenHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LecternBlockEntity.class)
public abstract class LecternBlockEntityMixin extends BlockEntity {
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

        var stack = this.book;
        var enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
        var entrySet = enchantments.entrySet();
        for (var entry : entrySet) {
            var enchantmentLevel = entry.getIntValue();
            var enchantment = entry.getKey().value();
            var definition = enchantment.definition();
            var supported = definition.supportedItems();
            var slots = definition.slots();

            // TODO: Properly read the translation key (append .desc to use the translations of "enchantment descriptions")

            var description = getEnchantmentDescription(enchantment, enchantmentLevel);
            LecternEnchantedBooks.LOGGER.info(description);
        }

        Item bookItem = this.book.getItem();
        // TODO: Check if the item is an enchanted book
        // if (bookItem instanceof EnchantedBookItem) {
        // TODO: Open menu!
        originalMethod.setReturnValue(new LecternScreenHandler(id, playerInventory, this.bookAccess, this.worldPosition));
        // }
    }

    /**
     * Determines the localized enchantment description by using the key of the title translation and appending a prefix. <br>
     * The method uses 3 steps to determine the translation. The first successful step is returned <br>
     * 1. "title_key.desc.level-enchantmentLevel": The translation for the exact enchantment and level (Allows listing details for each level) <br>
     * <b> Example: enchantment.minecraft.blast_protection.desc.level-4</b> <br>
     * 2. "title_key.desc"; The general translation for this enchantment. Has the same format as <b>Enchantment Descriptions</b> <br>
     * <b>Example: enchantment.minecraft.blast_protection.desc</b> <br>
     * 3. A fallback translation that shows the user which translation keys need to be implemented
     * 
     * @param enchantment
     * @param enchantmentLevel
     * @return
     */
    String getEnchantmentDescription(Enchantment enchantment, int enchantmentLevel) {
        var descriptionKey = "n/a";
        var descriptionLevelKey = "n/a";

        // Try to read the translation key from the description contents
        var keyContent = enchantment.description().getContents();
        if (keyContent instanceof TranslatableContents translatable) {
            descriptionLevelKey = translatable.getKey() + ".desc.level-" + enchantmentLevel;
            var levelTranslation = Component.translatable(descriptionLevelKey).getString();

            // If there is a translation for the specific level, use it immediately
            if (descriptionLevelKey.equals(levelTranslation) == false) {
                return levelTranslation;
            }

            // Otherwise check if there is a general enchantment description
            descriptionKey = translatable.getKey() + ".desc";
            var translation = Component.translatable(descriptionKey).getString();
            if (descriptionKey.equals(translation) == false) {
                return translation;
            }
        }

        // If no translation has been found, use a fallback translation that is shipped with the mod
        var fallbackKey = "enchantment.lectern-enchanted-books.no-description";
        return Component.translatable(fallbackKey, descriptionKey, descriptionLevelKey).getString();
    }

}