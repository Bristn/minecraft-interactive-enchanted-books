package net.bristn.interactive_enchanted_books.gamerules;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

public class ModGameRules {
    public static final GameRule<Boolean> HOPPER_INTERACTS_WITH_LECTERN = registerBoolean(false, "hopper_interacts_with_lectern");
    public static final GameRule<Boolean> SIGNAL_CHANGES_LECTERN_PAGE = registerBoolean(false, "signal_changes_lectern_page");

    public static void registerModGameRules() {
        CommonModInitializer.LOGGER.info("Register ModGameRules for" + CommonModInitializer.MOD_ID);
    }

    private static GameRule<Boolean> registerBoolean(boolean defaultValue, String name) {
        var identifier = Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID, name);
        return GameRuleBuilder.forBoolean(defaultValue).category(GameRuleCategory.MISC).buildAndRegister(identifier);
    }
}
