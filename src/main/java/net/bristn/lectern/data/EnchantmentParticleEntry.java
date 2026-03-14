package net.bristn.lectern.data;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class EnchantmentParticleEntry {
    public final TagKey<Item> enchantment;
    public final Identifier particle;

    // TODO: change data types to correct ones

    public EnchantmentParticleEntry(TagKey<Item> enchantment, Identifier particle) {
        this.enchantment = enchantment;
        this.particle = particle;
    }
}
