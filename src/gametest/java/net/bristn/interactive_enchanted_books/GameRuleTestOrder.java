package net.bristn.interactive_enchanted_books;

import java.util.List;

import net.minecraft.gametest.framework.GameTestHelper;

public class GameRuleTestOrder {

    public static GameRuleTestOrder HOPPER_TRUE_ENCHANTED_BOOk = new GameRuleTestOrder(false);
    public static GameRuleTestOrder HOPPER_FALSE_ENCHANTED_BOOk = new GameRuleTestOrder(false);
    public static List<GameRuleTestOrder> HOPPER_ORDER = List.of(HOPPER_TRUE_ENCHANTED_BOOk, HOPPER_FALSE_ENCHANTED_BOOk);

    public static GameRuleTestOrder SIGNAL_TRUE_ENCHANTED_BOOK = new GameRuleTestOrder(false);
    public static GameRuleTestOrder SIGNAL_TRUE_REGULAR_BOOK = new GameRuleTestOrder(false);
    public static GameRuleTestOrder SIGNAL_FALSE_ENCHANTED_BOOK = new GameRuleTestOrder(false);
    public static GameRuleTestOrder SIGNAL_FALSE_REGULAR_BOOK = new GameRuleTestOrder(false);
    public static List<GameRuleTestOrder> SIGNAL_ORDER = List.of(SIGNAL_TRUE_ENCHANTED_BOOK, SIGNAL_TRUE_REGULAR_BOOK,
            SIGNAL_FALSE_ENCHANTED_BOOK, SIGNAL_FALSE_REGULAR_BOOK);

    public boolean hasSucceeded;

    public GameRuleTestOrder(boolean hasSucceeded) {
        this.hasSucceeded = hasSucceeded;
    }

    public void succeed() {
        this.hasSucceeded = true;
    }

    public static void waitForTestToSucceed(GameTestHelper context, GameRuleTestOrder order, List<GameRuleTestOrder> list,
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
