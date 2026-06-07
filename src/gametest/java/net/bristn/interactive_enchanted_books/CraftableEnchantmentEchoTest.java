package net.bristn.interactive_enchanted_books;

import java.lang.reflect.Method;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.bristn.interactive_enchanted_books.gamerules.ModGameRules;
import net.bristn.interactive_enchanted_books.items.ModItems;
import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;

public class CraftableEnchantmentEchoTest implements CustomTestMethodInvoker {
    private static final BlockPos REDSTONE_BLOCK = new BlockPos(1, 1, 0);
    private static final BlockPos INPUT_HOPPER = new BlockPos(0, 2, 0);
    private static final BlockPos CRAFTER = new BlockPos(0, 1, 0);
    private static final BlockPos CHISELED_BOOKSHELF = new BlockPos(0, 1, 0);
    private static final BlockPos OUTPUT_HOPPER = new BlockPos(0, 0, 0);

    @GameTest(maxTicks = 1000)
    public void enableCrafting(GameTestHelper context) {
        var rule = GameRuleTestOrder.CRAFTABLE_ECHO_TRUE;

        GameRuleTestOrder.waitForTestToSucceed(context, rule, GameRuleTestOrder.CRAFTABLE_ECHO_ORDER, () -> {
            var server = context.getLevel().getServer();
            context.getLevel().getGameRules().set(ModGameRules.CRAFTABLE_ENCHANTMENT_ECHO, true, server);

            context.setBlock(INPUT_HOPPER, Blocks.HOPPER);
            context.setBlock(CRAFTER, Blocks.CRAFTER, Direction.SOUTH);

            var enchantedBook = BookTestHelper.getEnchantedBook(context);
            BookTestHelper.spawnItemStack(context, enchantedBook, INPUT_HOPPER.above().getCenter());

            context.runAfterDelay(20, () -> {
                context.assertContainerEmpty(INPUT_HOPPER);
                context.assertContainerContains(CRAFTER, enchantedBook.getItem());

                var book = new ItemStack(Items.BOOK, 1);
                BookTestHelper.spawnItemStack(context, book, INPUT_HOPPER.above().getCenter());

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
    public void disableCrafting(GameTestHelper context) {
        var rule = GameRuleTestOrder.CRAFTABLE_ECHO_FALSE;

        GameRuleTestOrder.waitForTestToSucceed(context, rule, GameRuleTestOrder.CRAFTABLE_ECHO_ORDER, () -> {
            var server = context.getLevel().getServer();
            context.getLevel().getGameRules().set(ModGameRules.CRAFTABLE_ENCHANTMENT_ECHO, false, server);

            context.setBlock(INPUT_HOPPER, Blocks.HOPPER);
            context.setBlock(CRAFTER, Blocks.CRAFTER, Direction.SOUTH);

            // ! Swapped spawn order, as crafter caches recipes (Original order will still work until restart)
            var book = new ItemStack(Items.BOOK, 1);
            BookTestHelper.spawnItemStack(context, book, INPUT_HOPPER.above().getCenter());

            context.runAfterDelay(20, () -> {
                context.assertContainerEmpty(INPUT_HOPPER);
                context.assertContainerContains(CRAFTER, book.getItem());

                var enchantedBook = BookTestHelper.getEnchantedBook(context);
                BookTestHelper.spawnItemStack(context, enchantedBook, INPUT_HOPPER.above().getCenter());

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
    public void chiseledBookshelf(GameTestHelper context) {
        var rule = GameRuleTestOrder.CHISELED_BOOKSHELF;

        GameRuleTestOrder.waitForTestToSucceed(context, rule, GameRuleTestOrder.CRAFTABLE_ECHO_ORDER, () -> {
            context.setBlock(INPUT_HOPPER, Blocks.HOPPER);
            context.setBlock(OUTPUT_HOPPER, Blocks.HOPPER);
            context.setBlock(CHISELED_BOOKSHELF, Blocks.CHISELED_BOOKSHELF);

            var echo = new ItemStack(ModItems.ENCHANTMENT_ECHO, 1);
            BookTestHelper.spawnItemStack(context, echo, INPUT_HOPPER.above().getCenter());

            context.runAfterDelay(40, () -> {
                context.assertContainerEmpty(INPUT_HOPPER);
                context.assertContainerContains(OUTPUT_HOPPER, echo.getItem());
                context.succeed();
                rule.succeed();
            });
        });
    }

    @Override
    public void invokeTestMethod(GameTestHelper context, Method method) throws ReflectiveOperationException {
        method.invoke(this, context);
    }
}