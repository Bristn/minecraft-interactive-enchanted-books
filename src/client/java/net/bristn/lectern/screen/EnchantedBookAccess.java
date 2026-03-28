package net.bristn.lectern.screen;

import java.util.ArrayList;
import java.util.List;

import net.bristn.lectern.screen.data.LecternScreenPageData;
import net.bristn.lectern.util.UtilLecternScreenPage;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record EnchantedBookAccess(List<LecternScreenPageData> pages) {
    public int getPageCount() {
        return this.pages.size();
    }

    /**
     * Gets the page contents from the page number
     */
    public LecternScreenPageData getPage(final int page) {
        if (page >= 0 && page < this.getPageCount()) {
            return this.pages.get(page);
        }

        return LecternScreenPageData.EMPTY;
    }

    /**
     * Utility to get the BookAccess from a given item stack
     */
    public static EnchantedBookAccess fromItem(final ItemStack stack) {
        var item = stack.getItem();
        if (item != Items.ENCHANTED_BOOK) {
            return new EnchantedBookAccess(new ArrayList<>());
        }

        var mc = Minecraft.getInstance();

        // TODO: Respect this boolean
        var filterEnabled = mc.isTextFilteringEnabled();
        var pages = UtilLecternScreenPage.getScreenPages(stack, mc.level);
        return new EnchantedBookAccess(pages);
    }
}
