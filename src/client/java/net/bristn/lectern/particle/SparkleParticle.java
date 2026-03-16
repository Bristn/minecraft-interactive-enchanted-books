package net.bristn.lectern.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.client.multiplayer.ClientLevel;

public class SparkleParticle extends SimpleAnimatedParticle {

    // TODO: Defines the movement of the particle

    protected SparkleParticle(
            final ClientLevel level, final double x, final double y, final double z,
            final double xSpeed,
            final double ySpeed,
            final double zSpeed, final SpriteSet sprites) {
        super(level, x, y, z, sprites, 0.0125F);

        this.age = 40;
        this.setSpriteFromAge(sprites);

        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;
    }

    @Override
    public void move(final double xa, final double ya, final double za) {
        this.setBoundingBox(this.getBoundingBox().move(xa, ya, za));
        this.setLocationFromBoundingbox();
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(final SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(
                final SimpleParticleType options,
                final ClientLevel level,
                final double x,
                final double y,
                final double z,
                final double xAux,
                final double yAux,
                final double zAux,
                final RandomSource random) {
            return new SparkleParticle(level, x, y, z, xAux, yAux, zAux, this.sprites);
        }
    }
}
