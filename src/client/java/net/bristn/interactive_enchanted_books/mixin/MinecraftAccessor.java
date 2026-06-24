package net.bristn.interactive_enchanted_books.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Accessor("particleEngine")
    public ParticleEngine getParticleEngine();
}
