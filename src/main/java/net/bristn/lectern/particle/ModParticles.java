package net.bristn.lectern.particle;

import net.bristn.lectern.LecternEnchantedBooks;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class ModParticles {

    public static final SimpleParticleType SPARKLE_PARTICLE = register("sparkle_particle", FabricParticleTypes.simple());

    public static void registerModParticles() {
    }

    private static SimpleParticleType register(String name, SimpleParticleType particle) {
        var identifier = Identifier.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID, name);
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, identifier, particle);
    }
}
