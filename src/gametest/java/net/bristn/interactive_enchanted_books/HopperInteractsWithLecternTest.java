package net.bristn.interactive_enchanted_books;

import java.lang.reflect.Method;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.bristn.interactive_enchanted_books.gamerules.ModGameRules;
import net.fabricmc.fabric.api.gametest.v1.CustomTestMethodInvoker;
import net.fabricmc.fabric.api.gametest.v1.GameTest;

public class HopperInteractsWithLecternTest implements CustomTestMethodInvoker {
    private static final BlockPos TOP_HOPPER = new BlockPos(0, 5, 0);
    private static final BlockPos LECTERN = new BlockPos(0, 4, 0);
    private static final BlockPos BOTTOM_HOPPER = new BlockPos(0, 3, 0);

    @GameTest(maxTicks = 1000)
    public void enableEnchantedBook(GameTestHelper context) {
        var rule = GameRuleTestOrder.HOPPER_FALSE_ENCHANTED_BOOk;

        GameRuleTestOrder.waitForTestToSucceed(context, rule, GameRuleTestOrder.HOPPER_ORDER, () -> {
            var server = context.getLevel().getServer();
            context.getLevel().getGameRules().set(ModGameRules.HOPPER_INTERACTS_WITH_LECTERN, false, server);

            context.setBlock(TOP_HOPPER, Blocks.HOPPER);
            context.setBlock(LECTERN, Blocks.LECTERN);
            context.setBlock(BOTTOM_HOPPER, Blocks.HOPPER);
            context.spawnItem(Items.ENCHANTED_BOOK, new Vec3(0.5, 5.5, 0.5));

            context.runAfterDelay(20, () -> {
                context.assertContainerContains(TOP_HOPPER, Items.ENCHANTED_BOOK);
                context.assertContainerEmpty(BOTTOM_HOPPER);
                context.succeed();
                rule.succeed();
            });
        });

    }

    @GameTest(maxTicks = 1000)
    public void disableEnchantedBook(GameTestHelper context) {
        var rule = GameRuleTestOrder.HOPPER_TRUE_ENCHANTED_BOOk;

        GameRuleTestOrder.waitForTestToSucceed(context, rule, GameRuleTestOrder.HOPPER_ORDER, () -> {
            var server = context.getLevel().getServer();
            context.getLevel().getGameRules().set(ModGameRules.HOPPER_INTERACTS_WITH_LECTERN, true, server);

            context.setBlock(TOP_HOPPER, Blocks.HOPPER);
            context.spawnItem(Items.ENCHANTED_BOOK, new Vec3(0.5, 5.5, 0.5));

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

    @Override
    public void invokeTestMethod(GameTestHelper context, Method method) throws ReflectiveOperationException {
        method.invoke(this, context);
    }
}