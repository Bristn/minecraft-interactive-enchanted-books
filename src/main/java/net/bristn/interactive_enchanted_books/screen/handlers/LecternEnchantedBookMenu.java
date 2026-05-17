package net.bristn.interactive_enchanted_books.screen.handlers;

import net.bristn.interactive_enchanted_books.screen.ModScreens;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Handles menu interactions. Mainly identical to the vanilla LecternScreenMenu
 */
public class LecternEnchantedBookMenu extends AbstractContainerMenu {
    public static final int DATA_ID_PAGE = 0;
    public static final int SLOT_BOOK = 0;

    public static final int BUTTON_PREV_PAGE = 1;
    public static final int BUTTON_NEXT_PAGE = 2;
    public static final int BUTTON_TAKE_BOOK = 3;
    public static final int BUTTON_PAGE_JUMP_RANGE_START = 100;

    private final Container lectern;
    private final ContainerData lecternData;

    public LecternEnchantedBookMenu(int containerId) {
        this(containerId, new SimpleContainer(1), new SimpleContainerData(1));
    }

    public LecternEnchantedBookMenu(int containerId, Container lectern, ContainerData lecternData) {
        super(ModScreens.MENU, containerId);

        checkContainerSize(lectern, 1);
        checkContainerDataCount(lecternData, 1);
        this.lectern = lectern;
        this.lecternData = lecternData;

        this.addSlot(new Slot(lectern, 0, 0, 0) {
            @Override
            public void setChanged() {
                super.setChanged();
                LecternEnchantedBookMenu.this.slotsChanged(this.container);
            }
        });

        this.addDataSlots(lecternData);
    }

    public boolean clickMenuButton(Player player, int buttonId) {
        if (buttonId >= BUTTON_PAGE_JUMP_RANGE_START) {
            int pageToSet = buttonId - BUTTON_PAGE_JUMP_RANGE_START;
            this.setData(0, pageToSet);
            return true;
        }

        int currentPage = this.lecternData.get(0);
        switch (buttonId) {
        case BUTTON_PREV_PAGE:
            this.setData(0, currentPage - 1);
            return true;
        case BUTTON_NEXT_PAGE:
            this.setData(0, currentPage + 1);
            return true;
        case BUTTON_TAKE_BOOK:
            if (!player.mayBuild()) {
                return false;
            }

            var book = this.lectern.removeItemNoUpdate(0);
            this.lectern.setChanged();
            if (!player.getInventory().add(book)) {
                player.drop(book, false);
            }

            return true;
        }

        return false;
    }

    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return ItemStack.EMPTY;
    }

    public void setData(int id, int value) {
        super.setData(id, value);
        this.broadcastChanges();
    }

    public boolean stillValid(Player player) {
        return this.lectern.stillValid(player);
    }

    public ItemStack getBook() {
        return this.lectern.getItem(0);
    }

    public int getPage() {
        return this.lecternData.get(0);
    }
}
