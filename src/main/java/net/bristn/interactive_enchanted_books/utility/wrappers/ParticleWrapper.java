package net.bristn.interactive_enchanted_books.utility.wrappers;

import java.util.List;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;

public class ParticleWrapper {

    public float weight;
    public ParticleOptions particle;
    public EnchantmentWrapper enchantment;

    public ParticleWrapper(float weight, ParticleOptions particle, EnchantmentWrapper enchantment) {
        this.weight = weight;
        this.particle = particle;
        this.enchantment = enchantment;
    }

    /**
     * Uses the weight of the wrappers to ensure the enchantment particles with a relative higher level
     * are used more often. This assumes that the total weight is always equal to 1
     */
    public static ParticleWrapper getRandomParticle(List<ParticleWrapper> particles, RandomSource random) {
        var currentWeight = 0.0f;
        var randomValue = random.nextFloat();
        for (var particleWrapper : particles) {
            currentWeight += particleWrapper.weight;

            if (currentWeight > randomValue) {
                return particleWrapper;
            }
        }

        return particles.get(0);
    }
}
