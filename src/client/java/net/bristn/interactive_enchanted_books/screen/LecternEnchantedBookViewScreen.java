package net.bristn.interactive_enchanted_books.screen;

import java.util.Objects;

import net.bristn.interactive_enchanted_books.screen.handlers.LecternEnchantedBookMenu;
import net.bristn.interactive_enchanted_books.screen.LecternEnchantedBookViewScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;

public class LecternEnchantedBookViewScreen extends EnchantedBookViewScreen implements MenuAccess<LecternEnchantedBookMenu> {
    private static final Component TAKE_BOOK_LABEL = Component.translatable("lectern.take_book");
    private final LecternEnchantedBookMenu menu;

    /**
     * Uses a container listener to notify the screen of when the page has changed
     */
    private final ContainerListener listener = new ContainerListener() {
        public void slotChanged(AbstractContainerMenu container, int slotIndex, ItemStack itemStack) {
            LecternEnchantedBookViewScreen.this.bookChanged();
        }

        public void dataChanged(AbstractContainerMenu container, int id, int value) {
            if (id == LecternEnchantedBookMenu.DATA_ID_PAGE) {
                LecternEnchantedBookViewScreen.this.pageChanged();
            }
        }
    };

    public LecternEnchantedBookViewScreen(LecternEnchantedBookMenu menu, Inventory inventory, Component title) {
        this.menu = menu;
    }

    public LecternEnchantedBookMenu getMenu() {
        return this.menu;
    }

    protected void init() {
        super.init();
        this.menu.addSlotListener(this.listener);
    }

    public void onClose() {
        this.minecraft.player.closeContainer();
        super.onClose();
    }

    public void removed() {
        super.removed();
        this.menu.removeSlotListener(this.listener);
    }

    /**
     * Creates the "done" and "take book" buttons. Additionally sets up their
     * handlers
     */
    protected void createMenuControls() {
        // In adventure mode, prevent showing the take book button
        if (this.minecraft.player.mayBuild() == false) {
            super.createMenuControls();
            return;
        }

        int buttonY = 196;
        int middle = this.width / 2;

        // Render the done button
        var done = Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose());
        done.pos(middle - 98 - 2, buttonY);
        done.width(98);
        this.addRenderableWidget(done.build());

        // Render the take book button
        var take = Button.builder(TAKE_BOOK_LABEL, (button) -> this.sendButtonClick(3));
        take.pos(middle + 2, buttonY);
        take.width(98);
        this.addRenderableWidget(take.build());
    }

    protected void pageBack() {
        this.sendButtonClick(LecternEnchantedBookMenu.BUTTON_PREV_PAGE);
    }

    protected void pageForward() {
        this.sendButtonClick(LecternEnchantedBookMenu.BUTTON_NEXT_PAGE);
    }

    protected boolean forcePage(int page) {
        if (page != this.menu.getPage()) {
            this.sendButtonClick(LecternEnchantedBookMenu.BUTTON_PAGE_JUMP_RANGE_START + page);
            return true;
        }

        return false;
    }

    /**
     * Send the button click event using the server to ensure the page changes for
     * all players
     */
    private void sendButtonClick(int button) {
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, button);
    }

    /** Adapted method from LecternScreen */
    private void bookChanged() {
        var book = this.menu.getBook();
        var access = Objects.requireNonNullElse(EnchantedBookAccess.fromItem(book), EnchantedBookViewScreen.EMPTY_ACCESS);
        this.setBookAccess(access);
    }

    /** Method from LecternScreen */
    public boolean isPauseScreen() {
        return false;
    }

    /** Method from LecternScreen */
    private void pageChanged() {
        this.setPage(this.menu.getPage());
    }

    /** Method from LecternScreen */
    protected void closeContainerOnServer() {
        this.minecraft.player.closeContainer();
    }
}
