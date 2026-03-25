package net.bristn.lectern.screen.handlers;

import java.util.ArrayList;
import java.util.List;

import net.bristn.lectern.screen.data.LecternScreenPageData;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class LecternScreenHandler extends AbstractContainerMenu {

    public static final MenuType<LecternScreenHandler> SCREEN_HANDLER = new MenuType<>(
            ((id, inv) -> new LecternScreenHandler(id, inv, ItemStack.EMPTY, new ArrayList<LecternScreenPageData>())),
            FeatureFlags.VANILLA_SET);

    private final SimpleContainer container;
    private final List<LecternScreenPageData> pages;

    /**
     * Single constructor for both client and server. Using code like in LecternView
     * did not sync the ItemSTack to the client. Therefore manually open this menu
     * on the client using a custom payload that contains the enchanted book item
     * 
     * @param containerId
     * @param playerInv
     * @param book
     */
    public LecternScreenHandler(int containerId, Inventory playerInv, ItemStack book, List<LecternScreenPageData> pages) {
        super(SCREEN_HANDLER, containerId);
        this.container = new SimpleContainer(1);
        this.container.setItem(0, book);
        this.pages = pages;
        this.addSlot(new Slot(container, 0, 80, 40));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    /**
     * Getter to determine the enchanted book
     * 
     * @return
     */
    public ItemStack getBook() {
        return container.getItem(0);
    }

    /**
     * 
     * @return
     */
    public List<LecternScreenPageData> getPages() {
        return pages;
    }
}