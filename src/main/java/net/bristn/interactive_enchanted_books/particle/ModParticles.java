package net.bristn.interactive_enchanted_books.particle;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class ModParticles {

    public static final SimpleParticleType CURSE = register("curse_particle", FabricParticleTypes.simple());
    public static final SimpleParticleType ENCHANT = register("enchant_particle", FabricParticleTypes.simple());

    public static void registerModParticles() {
        CommonModInitializer.LOGGER.info("Register ModParticles for" + CommonModInitializer.MOD_ID);
    }

    private static SimpleParticleType register(String name, SimpleParticleType particle) {
        var identifier = Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID, name);
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, identifier, particle);
    }
}
