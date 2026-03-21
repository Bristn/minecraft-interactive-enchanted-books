package net.bristn.lectern.resources;

import java.util.List;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.Identifier;

public class EnchantmentParticleEntry {
    public final Identifier enchantment;
    public final ParticleOptions particle;
    public final List<EnchantmentTranslationEntry> parameters;

    public EnchantmentParticleEntry(Identifier enchantment, ParticleOptions particle,
            List<EnchantmentTranslationEntry> parameters) {
        this.enchantment = enchantment;
        this.particle = particle;
        this.parameters = parameters;
    }

}
