package net.bristn.lectern.mixin;

import net.bristn.lectern.EnchantmentUtility;
import net.bristn.lectern.EnchantmentWrapper;
import net.bristn.lectern.screen.handlers.LecternEnchantedBookMenu;
import net.bristn.lectern.tag.ModEnchantmentTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LecternBlockEntity.class)
public abstract class LecternBlockEntityMixin extends BlockEntity {
    private static final int DATA_ID_PAGE = 0;

    private ItemStack cachedBook;
    private List<EnchantmentWrapper> cachedEnchantments;
    private int cachedPage;
    private int cachedSignal;

    @Shadow
    ItemStack book;

    @Shadow
    int page;

    @Shadow
    int pageCount;

    @Shadow
    void onBookItemRemove() {
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
            if (slot == 0) {
                return LecternBlockEntityMixin.this.book;
            }

            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int slot, int count) {
            if (slot == 0) {
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
            if (slot == 0) {
                ItemStack prev = LecternBlockEntityMixin.this.book;
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
            if (dataId == DATA_ID_PAGE) {
                return LecternBlockEntityMixin.this.page;
            }

            return 0;
        }

        @Override
        public void set(int dataId, int value) {
            if (dataId == DATA_ID_PAGE) {
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

    @Inject(method = "getRedstoneSignal", at = @At("HEAD"), cancellable = true)
    public void getEnchantedBookRedstoneSignal(final CallbackInfoReturnable<Integer> originalMethod) {
        if (this.book.getItem() != Items.ENCHANTED_BOOK) {
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
        if (cachedEnchantments.size() == 1 || this.page == 0) {
            originalMethod.setReturnValue(1);
            return;
        }

        // Only get the signal every time the page has been flipped
        if (this.page != cachedPage || changedBook == true) {
            var wrapper = cachedEnchantments.get(this.page - 1);
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

        this.pageCount = getEnchantedPageCount(this.book);
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
        this.pageCount = getEnchantedPageCount(this.book);
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

    /**
     * Helper method to get the total page count
     */
    private static int getEnchantedPageCount(ItemStack book) {
        var item = book.getItem();
        if (item != Items.ENCHANTED_BOOK) {
            return 0;
        }

        var itemEnchants = EnchantmentHelper.getEnchantmentsForCrafting(book);
        var enchantmentCount = itemEnchants.entrySet().size();
        if (enchantmentCount == 1) {
            return enchantmentCount;
        }

        return enchantmentCount + 1;
    }
}