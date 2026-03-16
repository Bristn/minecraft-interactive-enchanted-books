package net.bristn.lectern.data;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.Identifier;

public class EnchantmentParticleEntry {
    public final Identifier enchantment;
    public final ParticleOptions particle;

    public EnchantmentParticleEntry(Identifier enchantment, ParticleOptions particle) {
        this.enchantment = enchantment;
        this.particle = particle;
    }
}
