package net.bristn.interactive_enchanted_books.resources;

import java.util.ArrayList;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.Identifier;

public class EnchantmentData {
    public final Identifier enchantment;
    public final ParticleOptions particle;
    public final ArrayList<ArrayList<EnchantmentNamedParameter>> parameters;

    public EnchantmentData(Identifier enchantment, ParticleOptions particle,
            ArrayList<ArrayList<EnchantmentNamedParameter>> parameters) {

        this.enchantment = enchantment;
        this.particle = particle;
        this.parameters = parameters;
    }
}
