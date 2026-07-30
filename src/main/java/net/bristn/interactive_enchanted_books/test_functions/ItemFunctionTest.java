package net.bristn.interactive_enchanted_books.test_functions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.bristn.interactive_enchanted_books.gamerules.ModGameRules;
import net.bristn.interactive_enchanted_books.items.ModItems;
import net.bristn.interactive_enchanted_books.test_utilities.TestFunctionHelper;
import net.bristn.interactive_enchanted_books.test_utilities.TestFunctionOrder;
import net.fabricmc.fabric.api.gametest.v1.GameTest;

public class ItemFunctionTest {
    public ItemFunctionTest() {
        TestFunctionOrder.resetIfFinished();
    }

    private static final BlockPos REDSTONE_BLOCK = new BlockPos(1, 1, 0);
    private static final BlockPos INPUT_HOPPER = new BlockPos(0, 2, 0);
    private static final BlockPos CRAFTER = new BlockPos(0, 1, 0);
    private static final BlockPos CHISELED_BOOKSHELF = new BlockPos(0, 1, 0);
    private static final BlockPos OUTPUT_HOPPER = new BlockPos(0, 0, 0);

    @GameTest(maxTicks = 1000)
    public void craftingIsAllowed(GameTestHelper context) {
        var rule = TestFunctionOrder.CRAFTABLE_ECHO_TRUE;

        TestFunctionOrder.waitForTestToSucceed(context, rule, TestFunctionOrder.ITEM_ORDER, () -> {
            var server = context.getLevel().getServer();
            context.getLevel().getGameRules().set(ModGameRules.CRAFTABLE_ENCHANTMENT_ECHO, true, server);

            context.setBlock(INPUT_HOPPER, Blocks.HOPPER);
            context.setBlock(CRAFTER, Blocks.CRAFTER, Direction.SOUTH);

            var enchantedBook = TestFunctionHelper.getEnchantedBook(context);
            TestFunctionHelper.spawnItemStack(context, enchantedBook, INPUT_HOPPER.above().getCenter());

            context.runAfterDelay(20, () -> {
                context.assertContainerEmpty(INPUT_HOPPER);
                context.assertContainerContains(CRAFTER, enchantedBook.getItem());

                var book = new ItemStack(Items.BOOK, 1);
                TestFunctionHelper.spawnItemStack(context, book, INPUT_HOPPER.above().getCenter());

                context.runAfterDelay(20, () -> {
                    context.assertContainerEmpty(INPUT_HOPPER);
                    context.assertContainerContains(CRAFTER, book.getItem());
                    context.setBlock(REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);

                    context.runAfterDelay(60, () -> {
                        context.assertContainerEmpty(INPUT_HOPPER);
                        context.assertContainerEmpty(CRAFTER);
                        context.succeed();
                        rule.succeed();
                    });
                });
            });
        });
    }

    @GameTest(maxTicks = 1000)
    public void craftingIsPrevented(GameTestHelper context) {
        var rule = TestFunctionOrder.CRAFTABLE_ECHO_FALSE;

        TestFunctionOrder.waitForTestToSucceed(context, rule, TestFunctionOrder.ITEM_ORDER, () -> {
            var server = context.getLevel().getServer();
            context.getLevel().getGameRules().set(ModGameRules.CRAFTABLE_ENCHANTMENT_ECHO, false, server);

            context.setBlock(INPUT_HOPPER, Blocks.HOPPER);
            context.setBlock(CRAFTER, Blocks.CRAFTER, Direction.SOUTH);

            // ! Swapped spawn order, as crafter caches recipes (Original order will still work until restart)
            var book = new ItemStack(Items.BOOK, 1);
            TestFunctionHelper.spawnItemStack(context, book, INPUT_HOPPER.above().getCenter());

            context.runAfterDelay(20, () -> {
                context.assertContainerEmpty(INPUT_HOPPER);
                context.assertContainerContains(CRAFTER, book.getItem());

                var enchantedBook = TestFunctionHelper.getEnchantedBook(context);
                TestFunctionHelper.spawnItemStack(context, enchantedBook, INPUT_HOPPER.above().getCenter());

                context.runAfterDelay(20, () -> {
                    context.assertContainerEmpty(INPUT_HOPPER);
                    context.assertContainerContains(CRAFTER, enchantedBook.getItem());
                    context.setBlock(REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);

                    context.runAfterDelay(60, () -> {
                        context.assertContainerContains(CRAFTER, enchantedBook.getItem());
                        context.assertContainerContains(CRAFTER, book.getItem());
                        context.succeed();
                        rule.succeed();
                    });
                });
            });
        });
    }

    @GameTest(maxTicks = 1000)
    public void placeableInBookshelf(GameTestHelper context) {
        var rule = TestFunctionOrder.CHISELED_BOOKSHELF;

        TestFunctionOrder.waitForTestToSucceed(context, rule, TestFunctionOrder.ITEM_ORDER, () -> {
            context.setBlock(INPUT_HOPPER, Blocks.HOPPER);
            context.setBlock(OUTPUT_HOPPER, Blocks.HOPPER);
            context.setBlock(CHISELED_BOOKSHELF, Blocks.CHISELED_BOOKSHELF);

            var echo = new ItemStack(ModItems.ENCHANTMENT_ECHO, 1);
            TestFunctionHelper.spawnItemStack(context, echo, INPUT_HOPPER.above().getCenter());

            context.runAfterDelay(40, () -> {
                context.assertContainerEmpty(INPUT_HOPPER);
                context.assertContainerContains(OUTPUT_HOPPER, echo.getItem());
                context.succeed();
                rule.succeed();
            });
        });
    }
}