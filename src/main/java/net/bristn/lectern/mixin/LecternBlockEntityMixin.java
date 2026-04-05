package net.bristn.lectern.mixin;

import net.bristn.lectern.EnchantmentUtility;
import net.bristn.lectern.EnchantmentWrapper;
import net.bristn.lectern.LecternAccess;
import net.bristn.lectern.screen.handlers.LecternEnchantedBookMenu;
import net.bristn.lectern.tag.ModEnchantmentTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LecternBlockEntity.class)
public abstract class LecternBlockEntityMixin extends BlockEntity implements WorldlyContainer, LecternAccess {

    private static final int SLOT_BOOK = LecternEnchantedBookMenu.SLOT_BOOK;
    private static final int[] SLOTS = new int[] { SLOT_BOOK };

    private ItemStack cachedBook;
    private List<EnchantmentWrapper> cachedEnchantments;
    private int cachedPage;
    private int cachedSignal;

    @Shadow
    protected ItemStack book;

    @Shadow
    protected int page;

    @Shadow
    protected int pageCount;

    @Shadow
    public abstract void onBookItemRemove();

    @Shadow
    public abstract boolean hasBook();

    @Shadow
    public abstract ItemStack getBook();

    @Shadow
    public abstract void setBook(ItemStack book);

    private final Container enchantedBookAccess = new Container() {
        @Override
        public int getContainerSize() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return LecternBlockEntityMixin.this.book.isEmpty();
        }

        @Override
        public ItemStack getItem(int slot) {
            if (slot == LecternEnchantedBookMenu.SLOT_BOOK) {
                return LecternBlockEntityMixin.this.book;
            }

            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int slot, int count) {
            if (slot == LecternEnchantedBookMenu.SLOT_BOOK) {
                var stack = LecternBlockEntityMixin.this.book.split(count);
                if (LecternBlockEntityMixin.this.book.isEmpty()) {
                    LecternBlockEntityMixin.this.onBookItemRemove();
                }

                return stack;
            }

            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            if (slot == LecternEnchantedBookMenu.SLOT_BOOK) {
                var prev = LecternBlockEntityMixin.this.book;
                LecternBlockEntityMixin.this.book = ItemStack.EMPTY;
                LecternBlockEntityMixin.this.onBookItemRemove();
                return prev;
            }

            return ItemStack.EMPTY;
        }

        @Override
        public void setItem(int slot, ItemStack itemStack) {
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public void setChanged() {
            LecternBlockEntityMixin.this.setChanged();
        }

        @Override
        public boolean stillValid(Player player) {
            var validEntity = Container.stillValidBlockEntity(LecternBlockEntityMixin.this, player);
            var hasBook = LecternBlockEntityMixin.this.hasEnchantedBook();
            return validEntity && hasBook;
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack itemStack) {
            return false;
        }

        @Override
        public void clearContent() {
        }
    };

    private final ContainerData enchantedDataAccess = new ContainerData() {
        @Override
        public int get(int dataId) {
            if (dataId == LecternEnchantedBookMenu.DATA_ID_PAGE) {
                return LecternBlockEntityMixin.this.page;
            }

            return 0;
        }

        @Override
        public void set(int dataId, int value) {
            if (dataId == LecternEnchantedBookMenu.DATA_ID_PAGE) {
                LecternBlockEntityMixin.this.setEnchantedBookPage(value);
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    public LecternBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /**
     * Replaces the hasBook() method to properly check for enchanted books
     */
    private boolean hasEnchantedBook() {
        return this.book.has(DataComponents.STORED_ENCHANTMENTS);
    }

    private void setEnchantedBookPage(int page) {
        int newPage = Mth.clamp(page, 0, this.pageCount - 1);
        if (newPage != this.page) {
            this.page = newPage;
            this.setChanged();
            LecternBlock.signalPageChange(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    @Inject(method = "getRedstoneSignal", at = @At("HEAD"), cancellable = true)
    public void getEnchantedBookRedstoneSignal(final CallbackInfoReturnable<Integer> originalMethod) {
        if (this.book.getItem() != Items.ENCHANTED_BOOK || this.page < 0) {
            return;
        }

        // Cache the enchantments to improve performance
        var changedBook = false;
        if (this.book != cachedBook) {
            cachedBook = this.book;
            cachedEnchantments = EnchantmentUtility.getSortedEnchantments(this.book, this.level);
            changedBook = true;
        }

        // Title page always has a redstone signal of 1
        var isOnePage = cachedEnchantments.size() == 1;
        if (this.page == 0 && isOnePage == false) {
            originalMethod.setReturnValue(1);
            return;
        }

        // Only get the signal every time the page has been flipped
        if (this.page != cachedPage || changedBook == true) {
            var wrapper = cachedEnchantments.get(this.page - (isOnePage ? 0 : 1));
            var enchantment = wrapper.enchantment();

            var registry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            var holder = registry.wrapAsHolder(enchantment);

            this.cachedSignal = ModEnchantmentTags.getRedstoneSignal(holder);
            this.cachedPage = this.page;
        }

        originalMethod.setReturnValue(this.cachedSignal);
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"), cancellable = true)
    public void loadEnchantedBook(final ValueInput input, final CallbackInfo originalMethod) {
        // ! Original setBook uses resolveBook method
        this.book = (ItemStack) input.read("Book", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        var item = book.getItem();
        if (item != Items.ENCHANTED_BOOK) {
            return;
        }

        this.pageCount = getPageCount();
        this.page = Mth.clamp(input.getIntOr("Page", 0), 0, this.pageCount - 1);
    }

    @Inject(method = "setBook", at = @At("TAIL"), cancellable = true)
    public void setEnchantedBook(final ItemStack book, final CallbackInfo method) {
        var item = book.getItem();
        if (item != Items.ENCHANTED_BOOK) {
            return;
        }

        this.book = book; // ! Original setBook uses resolveBook method
        this.page = 0;
        this.pageCount = getPageCount();
        this.setChanged();
    }

    /**
     * Injects a custom callback when opening the lectern GUI. If the lectern
     * contains an enchanted book, show a custom Screen which displays the book
     * information
     */
    @Inject(method = "createMenu", at = @At("HEAD"), cancellable = true)
    private void openLectern(int id, Inventory inventory, Player player, CallbackInfoReturnable<AbstractContainerMenu> method) {
        var lectern = (LecternBlockEntity) (Object) this;
        if (lectern.getBook().getItem() != Items.ENCHANTED_BOOK) {
            return;
        }

        var menu = new LecternEnchantedBookMenu(id, this.enchantedBookAccess, this.enchantedDataAccess);
        method.setReturnValue(menu);
    }

    @Inject(method = "getPage", at = @At("HEAD"), cancellable = true)
    private void getEnchantedPage(CallbackInfoReturnable<Integer> method) {
        var item = this.book.getItem();
        if (item != Items.ENCHANTED_BOOK) {
            return;
        }

        var itemEnchants = EnchantmentHelper.getEnchantmentsForCrafting(this.book);
        var enchantmentCount = itemEnchants.entrySet().size();
        if (enchantmentCount == 1) {
            method.setReturnValue(enchantmentCount);
            return;
        }

        method.setReturnValue(enchantmentCount + 1);
    }

    // ! Methods for hopper functionality

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot != SLOT_BOOK || this.level == null) {
            return;
        }

        var lecternBlock = this.getBlockState().getBlock();
        var hasBookState = this.getBlockState().setValue(LecternBlock.HAS_BOOK, stack.isEmpty() == false);

        // Depending on the stack, either remove the book, or add a new one
        setBook(stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1));

        // Update the block state & mark the lectern as dirty
        this.page = 0;
        this.level.setBlock(this.worldPosition, hasBookState, Block.UPDATE_ALL);
        this.level.updateNeighbourForOutputSignal(this.worldPosition, lecternBlock);
        this.setChanged();
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot != SLOT_BOOK || this.book.isEmpty()) {
            return ItemStack.EMPTY;
        }

        // Remove the item by setting an empty stack. This also updates the state
        var removed = this.book.copy();
        setItem(SLOT_BOOK, ItemStack.EMPTY);
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return removeItem(slot, 1);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return this.book.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return slot == SLOT_BOOK ? this.book : ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot == SLOT_BOOK && this.book.isEmpty() && stack.is(ItemTags.LECTERN_BOOKS);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == SLOT_BOOK && this.book.isEmpty() == false;
    }

    // ! Methods of the LecternAccessor interface

    @Override
    public int getCurrentPage() {
        return this.page;
    }

    @Override
    public int getPageCount() {
        var enchantments = EnchantmentUtility.getSortedEnchantments(this.book, this.level);
        if (enchantments.size() == 1) {
            return 1;
        }

        return enchantments.size() + 1;
    }

    @Override
    public void setCurrentPage(int page) {
        this.setEnchantedBookPage(page);
    }
}