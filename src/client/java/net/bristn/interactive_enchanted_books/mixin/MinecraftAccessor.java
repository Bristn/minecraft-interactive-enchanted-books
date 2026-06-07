package net.bristn.interactive_enchanted_books.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleResources;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Accessor("particleResources")
    public ParticleResources getParticleResources();
}
