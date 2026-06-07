package net.bristn.interactive_enchanted_books.screen;

import java.util.ArrayList;
import java.util.List;

import net.bristn.interactive_enchanted_books.screen.data.LecternScreenPageData;
import net.bristn.interactive_enchanted_books.utility.EnchantmentUtility;
import net.bristn.interactive_enchanted_books.utility.LecternScreenPageUtility;
import net.bristn.interactive_enchanted_books.screen.EnchantedBookAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

/**
 * Custom version of the BookAccess used by vanilla. Returns a custom record per page instead of a
 * single renderable component
 */
public record EnchantedBookAccess(List<LecternScreenPageData> pages) {
    public int getPageCount() {
        return this.pages.size();
    }

    /**
     * Gets the page contents from the page number
     */
    public LecternScreenPageData getPage(int page) {
        if (page >= 0 && page < this.getPageCount()) {
            return this.pages.get(page);
        }

        return LecternScreenPageData.EMPTY;
    }

    /**
     * Utility to get the BookAccess from a given item stack
     */
    public static EnchantedBookAccess fromItem(ItemStack stack) {
        if (EnchantmentUtility.isEnchantedBookLike(stack) == false) {
            return new EnchantedBookAccess(new ArrayList<>());
        }

        var mc = Minecraft.getInstance();
        var pages = LecternScreenPageUtility.getScreenPages(stack, mc.level);
        return new EnchantedBookAccess(pages);
    }
}
