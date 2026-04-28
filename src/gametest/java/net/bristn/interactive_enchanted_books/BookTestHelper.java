package net.bristn.interactive_enchanted_books;

import java.util.List;
import java.util.Optional;

import net.bristn.interactive_enchanted_books.utility.interfaces.LecternAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FilterMask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.Filterable;
import net.minecraft.server.network.FilteredText;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;

public class BookTestHelper {

    public static int PAGE_COUNT = 3;

    public static ItemStack getWrittenBook() {
        var stack = Items.WRITABLE_BOOK.getDefaultInstance();
        var title = Filterable.from(new FilteredText("text", FilterMask.PASS_THROUGH));
        var content = new Filterable<Component>(Component.nullToEmpty("content"), Optional.empty());
        var pages = List.of(content, content, content);
        stack.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(title, "test", 1, pages, false));
        return stack;
    }

    public static ItemStack getEnchantedBook(GameTestHelper context) {
        var stack = Items.ENCHANTED_BOOK.getDefaultInstance();
        var lookup = context.getLevel().holderLookup(Registries.ENCHANTMENT);
        var aquaAffinity = lookup.get(Enchantments.AQUA_AFFINITY).get();
        var sharpness = lookup.get(Enchantments.SHARPNESS).get();
        stack.enchant(aquaAffinity, 1);
        stack.enchant(sharpness, 1);
        return stack;
    }

    /**
     * Custom version of the spawnItem method that ensure the enchantments are still present on the
     * item. Using the regular spawnItem removes the enchantment information
     */
    public static ItemEntity spawnItemStack(GameTestHelper context, ItemStack stack, Vec3 pos) {
        ServerLevel level = context.getLevel();
        Vec3 absoluteVec = context.absoluteVec(pos);
        ItemEntity itemEntity = new ItemEntity(level, absoluteVec.x, absoluteVec.y, absoluteVec.z, stack);
        itemEntity.setDeltaMovement((double) 0.0F, (double) 0.0F, (double) 0.0F);
        level.addFreshEntity(itemEntity);
        return itemEntity;
    }

    public static void advancePage(LecternAccess lectern) {
        lectern.setCurrentPage((lectern.getCurrentPage() + 1) % lectern.getPageCount());
    }
}
