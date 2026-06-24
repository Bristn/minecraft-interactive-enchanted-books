package net.bristn.interactive_enchanted_books.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleResources;

@Mixin(ParticleEngine.class)
public interface ParticleEngineAccessor {
    @Accessor("resourceManager")
    public ParticleResources getParticleResources();
}
