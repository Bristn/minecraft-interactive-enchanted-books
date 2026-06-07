package net.bristn.interactive_enchanted_books;

import java.util.List;

import net.minecraft.gametest.framework.GameTestHelper;

public class GameRuleTestOrder {

    public static GameRuleTestOrder HOPPER_TRUE_ENCHANTED_BOOK = new GameRuleTestOrder(false);
    public static GameRuleTestOrder HOPPER_FALSE_ENCHANTED_BOOK = new GameRuleTestOrder(false);
    public static GameRuleTestOrder HOPPER_TRUE_ENCHANTMENT_ECHO = new GameRuleTestOrder(false);
    public static List<GameRuleTestOrder> HOPPER_ORDER = List.of(HOPPER_TRUE_ENCHANTED_BOOK, HOPPER_FALSE_ENCHANTED_BOOK,
            HOPPER_TRUE_ENCHANTMENT_ECHO);

    public static GameRuleTestOrder SIGNAL_TRUE_ENCHANTED_BOOK = new GameRuleTestOrder(false);
    public static GameRuleTestOrder SIGNAL_TRUE_REGULAR_BOOK = new GameRuleTestOrder(false);
    public static GameRuleTestOrder SIGNAL_FALSE_ENCHANTED_BOOK = new GameRuleTestOrder(false);
    public static GameRuleTestOrder SIGNAL_FALSE_REGULAR_BOOK = new GameRuleTestOrder(false);
    public static List<GameRuleTestOrder> SIGNAL_ORDER = List.of(SIGNAL_TRUE_ENCHANTED_BOOK, SIGNAL_TRUE_REGULAR_BOOK,
            SIGNAL_FALSE_ENCHANTED_BOOK, SIGNAL_FALSE_REGULAR_BOOK);

    public static GameRuleTestOrder CRAFTABLE_ECHO_TRUE = new GameRuleTestOrder(false);
    public static GameRuleTestOrder CRAFTABLE_ECHO_FALSE = new GameRuleTestOrder(false);
    public static GameRuleTestOrder CHISELED_BOOKSHELF = new GameRuleTestOrder(false);
    public static List<GameRuleTestOrder> CRAFTABLE_ECHO_ORDER = List.of(CRAFTABLE_ECHO_TRUE, CRAFTABLE_ECHO_FALSE,
            CHISELED_BOOKSHELF);

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
