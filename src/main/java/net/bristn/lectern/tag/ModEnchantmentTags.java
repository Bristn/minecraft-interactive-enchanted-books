package net.bristn.lectern.tag;

import java.util.ArrayList;
import java.util.List;

import net.bristn.lectern.LecternEnchantedBooks;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantmentTags {
    public static final List<TagKey<Enchantment>> LECTERN_SIGNAL = new ArrayList<>();

    public static void registerModEnchantmentTags() {
        LecternEnchantedBooks.LOGGER.info("Register ModEnchantmentTags for" + LecternEnchantedBooks.MOD_ID);

        for (var i = 1; i < 16; i++) {
            LECTERN_SIGNAL.add(registerTag("lectern_signal_" + i));
        }
    }

    private static TagKey<Enchantment> registerTag(String name) {
        var identifier = Identifier.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID, name);
        return TagKey.create(Registries.ENCHANTMENT, identifier);
    }

    /**
     * Utility function to get the signal from the enchantment. See the provider for
     * the different levels
     */
    public static int getRedstoneSignal(Holder<Enchantment> enchantment) {
        for (var i = 0; i < LECTERN_SIGNAL.size(); i++) {
            var key = LECTERN_SIGNAL.get(i);

            var hasTag = enchantment.is(key);
            if (hasTag == true) {
                return i + 1;
            }
        }

        return 0;
    }
}
