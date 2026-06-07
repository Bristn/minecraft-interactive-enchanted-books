package net.bristn.interactive_enchanted_books.particle;

import java.util.ArrayList;
import java.util.List;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class ModParticles {

    public static final SimpleParticleType CURSE = register("curse_particle");
    public static final SimpleParticleType ENCHANT = register("enchant_particle");

    public static final List<SimpleParticleType> ENCHANTMENT_PARTICLES = new ArrayList<>();

    public static final List<String> ENCHANTMENT_PARTICLE_NAMES = List.of(//
            "aqua_affinity", //
            "bane_of_arthropods", //
            "binding_curse", //
            "blast_protection", //
            "breach", //
            "channeling", //
            "density", //
            "depth_strider", //
            "efficiency", //
            "feather_falling", //
            "fire_aspect", //
            "fire_protection", //
            "flame", //
            "fortune", //
            "frost_walker", //
            "impaling", //
            "infinity", //
            "knockback", //
            "looting", //
            "loyalty", //
            "luck_of_the_sea", //
            "lunge", //
            "lure", //
            "mending", //
            "multishot", //
            "piercing", //
            "power", //
            "projectile_protection", //
            "protection", //
            "punch", //
            "quick_charge", //
            "respiration", //
            "riptide", //
            "sharpness", //
            "silk_touch", //
            "smite", //
            "soul_speed", //
            "sweeping_edge", //
            "swift_sneak", //
            "thorns", //
            "unbreaking", //
            "vanishing_curse", //
            "wind_burst");

    public static void registerModParticles() {
        CommonModInitializer.LOGGER.info("Register ModParticles for" + CommonModInitializer.MOD_ID);

        for (var name : ENCHANTMENT_PARTICLE_NAMES) {
            ENCHANTMENT_PARTICLES.add(register(name));
        }
    }

    private static SimpleParticleType register(String name) {
        var identifier = Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID, name);
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, identifier, FabricParticleTypes.simple());
    }
}
