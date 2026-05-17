package net.bristn.interactive_enchanted_books.mixin;

import net.bristn.interactive_enchanted_books.gamerules.ModGameRules;
import net.bristn.interactive_enchanted_books.particle.ModParticles;
import net.bristn.interactive_enchanted_books.screen.handlers.LecternEnchantedBookMenu;
import net.bristn.interactive_enchanted_books.tag.ModEnchantmentTags;
import net.bristn.interactive_enchanted_books.utility.EnchantmentUtility;
import net.bristn.interactive_enchanted_books.utility.interfaces.LecternAccess;
import net.bristn.interactive_enchanted_books.utility.wrappers.EnchantmentWrapper;
import net.bristn.interactive_enchanted_books.utility.wrappers.ParticleWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;

import java.util.ArrayList;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Injects into the LecternBlockEntity to allow.
 * <li>Opening the enchanted book screen when right clicking</li>
 * <li>Saving the enchanted book in the data to preserve during reload</li>
 * <li>Enable hopper functionality</li>
 */
@Mixin(LecternBlockEntity.class)
public abstract class LecternBlockEntityMixin extends BlockEntity implements WorldlyContainer, LecternAccess {

    private static final int SLOT_BOOK = LecternEnchantedBookMenu.SLOT_BOOK;
    private static final int[] SLOTS = new int[] { SLOT_BOOK };
    private static final int PAGE_SOUND_EVENT = 1043;

    private ItemStack cachedBook;
    private List<EnchantmentWrapper> cachedEnchantments;
    private List<ParticleWrapper> cachedParticles = new ArrayList<>();

    private int cachedPage;
    private int cachedSignal;
    private int particleIndex;
    private int chiseledBookshelfBookCount;

    private boolean wasPowered;

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

    @Shadow
    public abstract void setPage(int page);

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
            var hasBook = LecternBlockEntityMixin.this.hasBook();
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
    @Inject(method = "hasBook", at = @At("TAIL"), cancellable = true)
    public void hasEnchantedBook(CallbackInfoReturnable<Boolean> method) {
        var hasEnchanted = this.book.has(DataComponents.STORED_ENCHANTMENTS);
        if (hasEnchanted) {
            method.setReturnValue(true);
        }
    }

    private void setEnchantedBookPage(int page) {
        int newPage = Mth.clamp(page, 0, this.pageCount - 1);
        if (newPage != this.page) {
            this.page = newPage;
            this.setChanged();

            // ! Don't send an outgoing redstone signal if the game rule to switch pages using signals is on
            // By default the lectern outputs signal, but this game rule acts on input signals
            if (this.level instanceof ServerLevel serverLevel) {
                var signalChangesPage = serverLevel.getGameRules().get(ModGameRules.SIGNAL_CHANGES_LECTERN_PAGE);
                if (signalChangesPage == true) {
                    level.levelEvent(PAGE_SOUND_EVENT, this.getBlockPos(), 0);
                    return;
                }

                LecternBlock.signalPageChange(this.getLevel(), this.getBlockPos(), this.getBlockState());
            }
        }
    }

    @Inject(method = "setPage", at = @At("HEAD"), cancellable = true)
    public void setRegularBookPage(final int page, CallbackInfo method) {
        // Original code from LecternBlockEntity
        int newPage = Mth.clamp(page, 0, this.pageCount - 1);
        if (newPage != this.page) {
            this.page = newPage;
            this.setChanged();

            // ! Don't send an outgoing redstone signal if the game rule to switch pages using signals is on
            // By default the lectern outputs signal, but this game rule acts on input signals
            if (this.level instanceof ServerLevel serverLevel) {
                var signalChangesPage = serverLevel.getGameRules().get(ModGameRules.SIGNAL_CHANGES_LECTERN_PAGE);
                if (signalChangesPage == true) {
                    level.levelEvent(PAGE_SOUND_EVENT, this.getBlockPos(), 0);
                    method.cancel();
                    return;
                }

                LecternBlock.signalPageChange(this.getLevel(), this.getBlockPos(), this.getBlockState());
            }
        }
    }

    @Inject(method = "getRedstoneSignal", at = @At("HEAD"), cancellable = true)
    public void getEnchantedBookRedstoneSignal(CallbackInfoReturnable<Integer> method) {
        if (this.book.getItem() != Items.ENCHANTED_BOOK || this.page < 0) {
            return;
        }

        // Cache the enchantments to improve performance
        var changedBook = false;
        if (this.book != cachedBook) {
            updateCacheUsingStack(this.book);
            changedBook = true;
        }

        // Title page always has a redstone signal of 1
        var isOnePage = cachedEnchantments.size() == 1;
        if (this.page == 0 && isOnePage == false) {
            method.setReturnValue(1);
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

        method.setReturnValue(this.cachedSignal);
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"), cancellable = true)
    public void loadEnchantedBook(ValueInput input, CallbackInfo method) {
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
     * Injects a custom callback when opening the lectern GUI. If the lectern contains an enchanted
     * book, show a custom Screen which displays the book information
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
        if (this.level instanceof ServerLevel serverLevel) {
            var hopperInteractsWithLectern = serverLevel.getGameRules().get(ModGameRules.HOPPER_INTERACTS_WITH_LECTERN);
            if (hopperInteractsWithLectern == false) {
                return false;
            }
        }

        return slot == SLOT_BOOK && this.book.isEmpty() && stack.is(ItemTags.LECTERN_BOOKS);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        if (this.level instanceof ServerLevel serverLevel) {
            var hopperInteractsWithLectern = serverLevel.getGameRules().get(ModGameRules.HOPPER_INTERACTS_WITH_LECTERN);
            if (hopperInteractsWithLectern == false) {
                return false;
            }
        }

        return slot == SLOT_BOOK && this.book.isEmpty() == false;
    }

    // ! Methods of the LecternAccessor interface

    @Override
    public int getPageCount() {
        // ! For regular books, use vanilla code of determining the page count (see LecternBlockEntity)
        if (this.book.getItem() != Items.ENCHANTED_BOOK) {
            var writtenContent = (WrittenBookContent) book.get(DataComponents.WRITTEN_BOOK_CONTENT);
            if (writtenContent != null) {
                return writtenContent.pages().size();
            } else {
                var writableContent = (WritableBookContent) book.get(DataComponents.WRITABLE_BOOK_CONTENT);
                return writableContent != null ? writableContent.pages().size() : 0;
            }
        }

        var itemEnchants = EnchantmentHelper.getEnchantmentsForCrafting(this.book);
        if (itemEnchants.entrySet().size() == 1) {
            return 1;
        }

        return itemEnchants.entrySet().size() + 1;
    }

    @Override
    public int getCurrentPage() {
        return this.page;
    }

    @Override
    public void setCurrentPage(int page) {

        if (this.book.getItem() == Items.ENCHANTED_BOOK) {
            this.setEnchantedBookPage(page);
        } else {
            this.setPage(page);
        }
    }

    @Override
    public void updateCacheUsingStack(ItemStack book) {
        if (cachedBook != null && cachedBook == book) {
            return;
        }

        cachedBook = book;
        cachedParticles.clear();
        cachedEnchantments = EnchantmentUtility.getSortedEnchantments(book, this.level);

        var totalWeight = 0.0f;
        for (var wrapper : cachedEnchantments) {
            var entry = EnchantmentUtility.getParticleForEnchantment(wrapper.holder());

            var enchantment = wrapper.enchantment();
            var enchantmentLevel = wrapper.enchantmentLevel();
            var maxEnchantmentLevel = enchantment.getMaxLevel();
            var normalizedLevel = (float) enchantmentLevel / (float) maxEnchantmentLevel;

            // Store the particle with the normalized enchantment level
            var particle = entry != null ? entry.particle : ModParticles.ENCHANT;
            var particleWrapper = new ParticleWrapper(normalizedLevel, particle, entry.enchantment);
            cachedParticles.add(particleWrapper);

            totalWeight += particleWrapper.weight;
        }

        // Normalize all weights to have the sum of them be equal to 1
        for (var particleWrapper : cachedParticles) {
            particleWrapper.weight = particleWrapper.weight / totalWeight;
        }

        // Sort the particles by weight. Multiply the float weight to get a integer for comparison
        // Uses a large number to allow smaller weights to still result in different integers
        cachedParticles.sort((a, b) -> (int) (b.weight * 10000) - (int) (a.weight * 10000));
    }

    @Override
    public List<EnchantmentWrapper> getCachedEnchantments() {
        return cachedEnchantments;
    }

    @Override
    public List<ParticleWrapper> getCachedParticles() {
        return cachedParticles;
    }

    @Override
    public int updateParticleIndex() {
        // If the lectern is on a specific enchantment page (not the cover), show the
        // respective particle only. Otherwise loop the different enchantment particles
        var enchantments = getCachedEnchantments();
        if (enchantments.size() == 0 || getCurrentPage() != 0) {
            particleIndex = getCurrentPage() - 1; // Book has i + 1 pages as there is a title page
        } else {
            particleIndex = (particleIndex + 1) % enchantments.size();
        }

        return particleIndex;
    }

    @Override
    public void setWasPowered(boolean wasPowered) {
        this.wasPowered = wasPowered;
    }

    @Override
    public boolean getWasPowered() {
        return this.wasPowered;
    }

    @Override
    public void setChiseledBookshelfBookCount(int bookCount) {
        this.chiseledBookshelfBookCount = bookCount;
    }

    @Override
    public int getChiseledBookshelfBookCount() {
        return this.chiseledBookshelfBookCount;
    }
}