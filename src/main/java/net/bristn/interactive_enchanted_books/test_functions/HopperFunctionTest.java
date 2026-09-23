package net.bristn.interactive_enchanted_books.test_functions;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.bristn.interactive_enchanted_books.gamerules.ModGameRules;
import net.bristn.interactive_enchanted_books.items.ModItems;
import net.bristn.interactive_enchanted_books.test_utilities.TestFunctionHelper;
import net.bristn.interactive_enchanted_books.test_utilities.TestFunctionOrder;
import net.fabricmc.fabric.api.gametest.v1.GameTest;

public class HopperFunctionTest {
    public HopperFunctionTest() {
        TestFunctionOrder.resetIfFinished();
    }

    private static final BlockPos TOP_HOPPER = new BlockPos(0, 2, 0);
    private static final BlockPos LECTERN = new BlockPos(0, 1, 0);
    private static final BlockPos BOTTOM_HOPPER = new BlockPos(0, 0, 0);

    @GameTest(maxTicks = 1000)
    public void interactionDisabled(GameTestHelper context) {
        var rule = TestFunctionOrder.HOPPER_FALSE_ENCHANTED_BOOK;

        TestFunctionOrder.waitForTestToSucceed(context, rule, TestFunctionOrder.HOPPER_ORDER, () -> {
            var server = context.getLevel().getServer();
            context.getLevel().getGameRules().set(ModGameRules.HOPPER_INTERACTS_WITH_LECTERN, false, server);

            context.setBlock(TOP_HOPPER, Blocks.HOPPER);
            context.setBlock(LECTERN, Blocks.LECTERN);
            context.setBlock(BOTTOM_HOPPER, Blocks.HOPPER);

            var enchantedBook = TestFunctionHelper.getEnchantedBook(context);
            TestFunctionHelper.spawnItemStack(context, enchantedBook, TOP_HOPPER.above());

            context.runAfterDelay(20, () -> {
                context.assertContainerContains(TOP_HOPPER, Items.ENCHANTED_BOOK);
                context.assertContainerEmpty(BOTTOM_HOPPER);
                context.succeed();
                rule.succeed();
            });
        });
    }

    @GameTest(maxTicks = 1000)
    public void interactionEnabled(GameTestHelper context) {
        var rule = TestFunctionOrder.HOPPER_TRUE_ENCHANTED_BOOK;

        TestFunctionOrder.waitForTestToSucceed(context, rule, TestFunctionOrder.HOPPER_ORDER, () -> {
            var server = context.getLevel().getServer();
            context.getLevel().getGameRules().set(ModGameRules.HOPPER_INTERACTS_WITH_LECTERN, true, server);

            context.setBlock(TOP_HOPPER, Blocks.HOPPER);

            var enchantedBook = TestFunctionHelper.getEnchantedBook(context);
            TestFunctionHelper.spawnItemStack(context, enchantedBook, TOP_HOPPER.above());

            context.runAfterDelay(20, () -> {
                context.assertContainerContains(TOP_HOPPER, Items.ENCHANTED_BOOK);
                context.setBlock(LECTERN, Blocks.LECTERN);

                context.runAfterDelay(20, () -> {
                    context.assertContainerEmpty(TOP_HOPPER);
                    context.setBlock(BOTTOM_HOPPER, Blocks.HOPPER);

                    context.runAfterDelay(20, () -> {
                        context.assertContainerContains(BOTTOM_HOPPER, Items.ENCHANTED_BOOK);
                        context.assertContainerEmpty(TOP_HOPPER);
                        context.succeed();
                        rule.succeed();
                    });
                });
            });
        });
    }

    @GameTest(maxTicks = 1000)
    public void interactionEnabledForEcho(GameTestHelper context) {
        var rule = TestFunctionOrder.HOPPER_TRUE_ENCHANTMENT_ECHO;

        TestFunctionOrder.waitForTestToSucceed(context, rule, TestFunctionOrder.HOPPER_ORDER, () -> {
            var server = context.getLevel().getServer();
            context.getLevel().getGameRules().set(ModGameRules.HOPPER_INTERACTS_WITH_LECTERN, true, server);

            context.setBlock(TOP_HOPPER, Blocks.HOPPER);

            var enchantmentEcho = TestFunctionHelper.getEnchantmentEcho(context);
            TestFunctionHelper.spawnItemStack(context, enchantmentEcho, TOP_HOPPER.above());

            context.runAfterDelay(20, () -> {
                context.assertContainerContains(TOP_HOPPER, ModItems.ENCHANTMENT_ECHO);
                context.setBlock(LECTERN, Blocks.LECTERN);

                context.runAfterDelay(20, () -> {
                    context.assertContainerEmpty(TOP_HOPPER);
                    context.setBlock(BOTTOM_HOPPER, Blocks.HOPPER);

                    context.runAfterDelay(20, () -> {
                        context.assertContainerContains(BOTTOM_HOPPER, ModItems.ENCHANTMENT_ECHO);
                        context.assertContainerEmpty(TOP_HOPPER);
                        context.succeed();
                        rule.succeed();
                    });
                });
            });
        });
    }
}