package net.bristn.interactive_enchanted_books.test_utilities;

import java.util.List;

import net.minecraft.gametest.framework.GameTestHelper;

public class TestFunctionOrder {

    public static TestFunctionOrder HOPPER_TRUE_ENCHANTED_BOOK = new TestFunctionOrder(false);
    public static TestFunctionOrder HOPPER_FALSE_ENCHANTED_BOOK = new TestFunctionOrder(false);
    public static TestFunctionOrder HOPPER_TRUE_ENCHANTMENT_ECHO = new TestFunctionOrder(false);
    public static List<TestFunctionOrder> HOPPER_ORDER = List.of(HOPPER_TRUE_ENCHANTED_BOOK, HOPPER_FALSE_ENCHANTED_BOOK,
            HOPPER_TRUE_ENCHANTMENT_ECHO);

    public static TestFunctionOrder SIGNAL_TRUE_ENCHANTED_BOOK = new TestFunctionOrder(false);
    public static TestFunctionOrder SIGNAL_TRUE_REGULAR_BOOK = new TestFunctionOrder(false);
    public static TestFunctionOrder SIGNAL_FALSE_ENCHANTED_BOOK = new TestFunctionOrder(false);
    public static TestFunctionOrder SIGNAL_FALSE_REGULAR_BOOK = new TestFunctionOrder(false);
    public static List<TestFunctionOrder> SIGNAL_ORDER = List.of(SIGNAL_TRUE_ENCHANTED_BOOK, SIGNAL_TRUE_REGULAR_BOOK,
            SIGNAL_FALSE_ENCHANTED_BOOK, SIGNAL_FALSE_REGULAR_BOOK);

    public static TestFunctionOrder CRAFTABLE_ECHO_TRUE = new TestFunctionOrder(false);
    public static TestFunctionOrder CRAFTABLE_ECHO_FALSE = new TestFunctionOrder(false);
    public static TestFunctionOrder CHISELED_BOOKSHELF = new TestFunctionOrder(false);
    public static List<TestFunctionOrder> ITEM_ORDER = List.of(CRAFTABLE_ECHO_TRUE, CRAFTABLE_ECHO_FALSE, CHISELED_BOOKSHELF);

    public boolean hasSucceeded;

    public TestFunctionOrder(boolean hasSucceeded) {
        this.hasSucceeded = hasSucceeded;
    }

    public void succeed() {
        this.hasSucceeded = true;
    }

    public static void resetIfFinished() {
        var allTests = List.of(HOPPER_ORDER, SIGNAL_ORDER, ITEM_ORDER);
        for (var group : allTests) {
            for (var test : group) {
                if (test.hasSucceeded == false) {
                    return;
                }
            }
        }

        for (var group : allTests) {
            for (var test : group) {
                test.hasSucceeded = false;
            }
        }
    }

    public static void waitForTestToSucceed(GameTestHelper context, TestFunctionOrder order, List<TestFunctionOrder> list,
            Runnable callback) {
        context.runAfterDelay(5, () -> {
            var index = list.indexOf(order);
            if (index == 0) {
                callback.run();
                return;
            }

            if (list.get(index - 1).hasSucceeded == true) {
                callback.run();
                return;
            }

            waitForTestToSucceed(context, order, list, callback);
        });
    }
}
