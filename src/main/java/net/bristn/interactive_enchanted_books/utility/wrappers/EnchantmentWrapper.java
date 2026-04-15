package net.bristn.interactive_enchanted_books.utility.wrappers;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;

public record EnchantmentWrapper(int enchantmentLevel, Holder<Enchantment> holder) {

    public Enchantment enchantment() {
        return holder.value();
    }
}
