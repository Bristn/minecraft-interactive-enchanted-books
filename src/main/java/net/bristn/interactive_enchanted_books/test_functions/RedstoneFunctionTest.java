package net.bristn.interactive_enchanted_books.test_functions;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.phys.Vec3;
import net.bristn.interactive_enchanted_books.gamerules.ModGameRules;
import net.bristn.interactive_enchanted_books.test_utilities.TestFunctionHelper;
import net.bristn.interactive_enchanted_books.test_utilities.TestFunctionOrder;
import net.bristn.interactive_enchanted_books.utility.interfaces.LecternAccess;
import net.fabricmc.fabric.api.gametest.v1.GameTest;

public class RedstoneFunctionTest {
    public RedstoneFunctionTest() {
        TestFunctionOrder.resetIfFinished();
    }

    private static final BlockPos TOP_HOPPER = new BlockPos(0, 3, 0);
    private static final BlockPos LECTERN = new BlockPos(0, 2, 0);
    private static final BlockPos REDSTONE_BLOCK = new BlockPos(0, 1, 0);
    private static final BlockPos COPPER_BULB = new BlockPos(1, 2, 0);

    @GameTest(maxTicks = 1000)
    public void signalChangeEnabled(GameTestHelper context) {
        var rule = TestFunctionOrder.SIGNAL_TRUE_ENCHANTED_BOOK;

        TestFunctionOrder.waitForTestToSucceed(context, rule, TestFunctionOrder.SIGNAL_ORDER, () -> {
            var server = context.getLevel().getServer();
            context.getLevel().getGameRules().set(ModGameRules.HOPPER_INTERACTS_WITH_LECTERN, true, server);
            context.getLevel().getGameRules().set(ModGameRules.SIGNAL_CHANGES_LECTERN_PAGE, true, server);

            context.setBlock(TOP_HOPPER, Blocks.HOPPER);
            context.setBlock(LECTERN, Blocks.LECTERN);

            var stack = TestFunctionHelper.getEnchantedBook(context);
            TestFunctionHelper.spawnItemStack(context, stack, new BlockPos(0, 3, 0));
            var lectern = (LecternAccess) (LecternBlockEntity) context.getLevel().getBlockEntity(context.absolutePos(LECTERN));

            context.runAfterDelay(20, () -> {
                context.assertContainerEmpty(TOP_HOPPER);
                context.assertValueEqual(lectern.getPageCount(), TestFunctionHelper.PAGE_COUNT, "Page count");
                context.assertValueEqual(lectern.getCurrentPage(), 0, "Initial page");
                context.setBlock(REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);

                context.runAfterDelay(20, () -> {
                    context.destroyBlock(REDSTONE_BLOCK);
                    context.assertValueEqual(lectern.getCurrentPage(), 1, "After 1 signal");
                    context.setBlock(REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);

                    context.runAfterDelay(20, () -> {
                        context.destroyBlock(REDSTONE_BLOCK);
                        context.assertValueEqual(lectern.getCurrentPage(), 2, "After 2 signals");
                        context.setBlock(REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);

                        context.runAfterDelay(20, () -> {
                            context.destroyBlock(REDSTONE_BLOCK);
                            context.assertValueEqual(lectern.getCurrentPage(), 0, "After 3 signals");
                            context.setBlock(REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);

                            context.runAfterDelay(20, () -> {
                                context.destroyBlock(REDSTONE_BLOCK);
                                context.assertValueEqual(lectern.getCurrentPage(), 1, "After 4 signals");
                                context.setBlock(REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);
                                context.succeed();
                                rule.succeed();
                            });
                        });
                    });
                });
            });
        });
    }

    @GameTest(maxTicks = 1000)
    public void signalChangeDisabled(GameTestHelper context) {
        var rule = TestFunctionOrder.SIGNAL_FALSE_ENCHANTED_BOOK;

        TestFunctionOrder.waitForTestToSucceed(context, rule, TestFunctionOrder.SIGNAL_ORDER, () -> {
            var server = context.getLevel().getServer();
            context.getLevel().getGameRules().set(ModGameRules.HOPPER_INTERACTS_WITH_LECTERN, true, server);
            context.getLevel().getGameRules().set(ModGameRules.SIGNAL_CHANGES_LECTERN_PAGE, false, server);

            context.setBlock(TOP_HOPPER, Blocks.HOPPER);
            context.setBlock(LECTERN, Blocks.LECTERN);
            context.setBlock(COPPER_BULB, Blocks.COPPER_BULB.asList().getFirst());

            var stack = TestFunctionHelper.getEnchantedBook(context);
            TestFunctionHelper.spawnItemStack(context, stack, new BlockPos(0, 3, 0));
            var lectern = (LecternAccess) (LecternBlockEntity) context.getLevel().getBlockEntity(context.absolutePos(LECTERN));

            context.runAfterDelay(20, () -> {
                context.assertContainerEmpty(TOP_HOPPER);
                context.assertValueEqual(lectern.getPageCount(), TestFunctionHelper.PAGE_COUNT, "Page count");
                context.assertValueEqual(lectern.getCurrentPage(), 0, "Initial page");
                context.setBlock(REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);

                context.runAfterDelay(20, () -> {
                    context.destroyBlock(REDSTONE_BLOCK);
                    context.assertValueEqual(lectern.getCurrentPage(), 0, "Page is the same");
                    context.setBlock(REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);

                    context.runAfterDelay(20, () -> {
                        context.destroyBlock(REDSTONE_BLOCK);
                        context.assertValueEqual(lectern.getCurrentPage(), 0, "Page is the same");
                        context.setBlock(REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);
                        TestFunctionHelper.advancePage(lectern);

                        context.runAfterDelay(20, () -> {
                            assertCopperBulbIsOn(context, COPPER_BULB);
                            TestFunctionHelper.advancePage(lectern);

                            context.runAfterDelay(20, () -> {
                                assertCopperBulbIsOff(context, COPPER_BULB);
                                context.succeed();
                                rule.succeed();
                            });
                        });
                    });
                });
            });
        });
    }

    @GameTest(maxTicks = 1000)
    public void signalChangeEnabledRegularBook(GameTestHelper context) {
        var rule = TestFunctionOrder.SIGNAL_TRUE_REGULAR_BOOK;

        TestFunctionOrder.waitForTestToSucceed(context, rule, TestFunctionOrder.SIGNAL_ORDER, () -> {
            var server = context.getLevel().getServer();
            context.getLevel().getGameRules().set(ModGameRules.HOPPER_INTERACTS_WITH_LECTERN, true, server);
            context.getLevel().getGameRules().set(ModGameRules.SIGNAL_CHANGES_LECTERN_PAGE, true, server);

            context.setBlock(TOP_HOPPER, Blocks.HOPPER);
            context.setBlock(LECTERN, Blocks.LECTERN);

            var stack = TestFunctionHelper.getWrittenBook();
            TestFunctionHelper.spawnItemStack(context, stack, new BlockPos(0, 3, 0));
            var lectern = (LecternAccess) (LecternBlockEntity) context.getLevel().getBlockEntity(context.absolutePos(LECTERN));

            context.runAfterDelay(20, () -> {
                context.assertContainerEmpty(TOP_HOPPER);
                context.assertValueEqual(lectern.getPageCount(), TestFunctionHelper.PAGE_COUNT, "Page count");
                context.assertValueEqual(lectern.getCurrentPage(), 0, "Initial page");
                context.setBlock(REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);

                context.runAfterDelay(20, () -> {
                    context.destroyBlock(REDSTONE_BLOCK);
                    context.assertValueEqual(lectern.getCurrentPage(), 1, "After 1 signal");
                    context.setBlock(REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);

                    context.runAfterDelay(20, () -> {
                        context.destroyBlock(REDSTONE_BLOCK);
                        context.assertValueEqual(lectern.getCurrentPage(), 2, "After 2 signals");
                        context.setBlock(REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);

                        context.runAfterDelay(20, () -> {
                            context.destroyBlock(REDSTONE_BLOCK);
                            context.assertValueEqual(lectern.getCurrentPage(), 0, "After 3 signals");
                            context.setBlock(REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);

                            context.runAfterDelay(20, () -> {
                                context.destroyBlock(REDSTONE_BLOCK);
                                context.assertValueEqual(lectern.getCurrentPage(), 1, "After 4 signals");
                                context.setBlock(REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);
                                context.succeed();
                                rule.succeed();
                            });
                        });
                    });
                });
            });
        });
    }

    @GameTest(maxTicks = 1000)
    public void signalChangeDisabledRegularBook(GameTestHelper context) {
        var rule = TestFunctionOrder.SIGNAL_FALSE_REGULAR_BOOK;

        TestFunctionOrder.waitForTestToSucceed(context, rule, TestFunctionOrder.SIGNAL_ORDER, () -> {
            var server = context.getLevel().getServer();
            context.getLevel().getGameRules().set(ModGameRules.HOPPER_INTERACTS_WITH_LECTERN, true, server);
            context.getLevel().getGameRules().set(ModGameRules.SIGNAL_CHANGES_LECTERN_PAGE, false, server);

            context.setBlock(TOP_HOPPER, Blocks.HOPPER);
            context.setBlock(LECTERN, Blocks.LECTERN);
            context.setBlock(COPPER_BULB, Blocks.COPPER_BULB.asList().getFirst());

            var stack = TestFunctionHelper.getWrittenBook();
            TestFunctionHelper.spawnItemStack(context, stack, new BlockPos(0, 3, 0));
            var lectern = (LecternAccess) (LecternBlockEntity) context.getLevel().getBlockEntity(context.absolutePos(LECTERN));

            context.runAfterDelay(20, () -> {
                context.assertContainerEmpty(TOP_HOPPER);
                context.assertValueEqual(lectern.getPageCount(), TestFunctionHelper.PAGE_COUNT, "Page count");
                context.assertValueEqual(lectern.getCurrentPage(), 0, "Initial page");
                context.setBlock(REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);

                context.runAfterDelay(20, () -> {
                    context.destroyBlock(REDSTONE_BLOCK);
                    context.assertValueEqual(lectern.getCurrentPage(), 0, "Page is the same");
                    context.setBlock(REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);

                    context.runAfterDelay(20, () -> {
                        context.destroyBlock(REDSTONE_BLOCK);
                        context.assertValueEqual(lectern.getCurrentPage(), 0, "Page is the same");
                        context.setBlock(REDSTONE_BLOCK, Blocks.REDSTONE_BLOCK);
                        TestFunctionHelper.advancePage(lectern);

                        context.runAfterDelay(20, () -> {
                            assertCopperBulbIsOn(context, COPPER_BULB);
                            TestFunctionHelper.advancePage(lectern);

                            context.runAfterDelay(20, () -> {
                                assertCopperBulbIsOff(context, COPPER_BULB);

                                context.succeed();
                                rule.succeed();
                            });
                        });
                    });
                });
            });
        });
    }

    private void assertCopperBulbIsOn(GameTestHelper context, BlockPos pos) {
        context.assertBlockState(pos, t -> t.getLightEmission() > 0, t -> Component.literal("Bulb is on"));
    }

    private void assertCopperBulbIsOff(GameTestHelper context, BlockPos pos) {
        context.assertBlockState(pos, t -> t.getLightEmission() == 0, t -> Component.literal("Bulb is off"));
    }
}