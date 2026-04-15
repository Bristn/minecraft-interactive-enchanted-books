package net.bristn.interactive_enchanted_books.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.bristn.interactive_enchanted_books.particle.EnchantParticle;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;

public class EnchantParticle extends SimpleAnimatedParticle {
    private double xSpeed;
    private double ySpeed;
    private double zSpeed;

    private final float randomAlpha;

    private static LifetimeAlpha startFade = new LifetimeAlpha(0.0f, 0.70f, 0f, 0.2f);
    private static LifetimeAlpha endFade = new LifetimeAlpha(0.70f, 0.0f, 0.8f, 1.0f);

    public EnchantParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed,
            SpriteSet sprites) {

        super(level, x, y, z, sprites, 0.0125f);

        // Set the lifetime in ticks (3 - 5 seconds)
        this.lifetime = 3 * 20 + this.random.nextInt(40);
        this.setSprite(sprites.get(random));

        // Randomly alternate the alpha by 0.8 to 1.0
        this.randomAlpha = 0.8f + random.nextFloat() * 0.2f;

        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.zSpeed = zSpeed;

        // Similar sizing to the default enchantment particles
        var size = 0.125f * (this.random.nextFloat() * 0.5f + 0.2f);
        this.quadSize = size;
        this.setSize(size, size);
        this.setAlpha(startFade.startAlpha());
    }

    protected EnchantParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed,
            SpriteSet sprites, float r, float g, float b) {

        this(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);

        this.setColor(r, g, b);
    }

    @Override
    public void extract(QuadParticleRenderState particleTypeRenderState, Camera camera, float partialTickTime) {
        var startAlpha = startFade.currentAlphaForAge(this.age, this.lifetime, partialTickTime);
        var endAlphaAlpha = endFade.currentAlphaForAge(this.age, this.lifetime, partialTickTime);
        var minAlpha = Math.min(startAlpha, endAlphaAlpha);
        this.setAlpha(minAlpha * randomAlpha);
        super.extract(particleTypeRenderState, camera, partialTickTime);
    }

    @Override
    public void setSpriteFromAge(SpriteSet sprites) {
        return;
    }

    @Override
    public void move(double xa, double ya, double za) {
        this.ySpeed -= 0.04 * (double) this.gravity;
        this.setBoundingBox(this.getBoundingBox().move(this.xSpeed, this.ySpeed, this.zSpeed));
        this.setLocationFromBoundingbox();
    }

    public static class ColorProvider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        private final float red;
        private final float green;
        private final float blue;

        public ColorProvider(SpriteSet sprites) {
            this.sprites = sprites;
            this.red = 1.0f;
            this.green = 1.0f;
            this.blue = 1.0f;
        }

        public ColorProvider(SpriteSet sprites, float red, float green, float blue) {
            this.sprites = sprites;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public Particle createParticle(SimpleParticleType options, ClientLevel level, double x, double y, double z, double xSpeed,
                double ySpeed, double zSpeed, RandomSource random) {

            return new EnchantParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites, red, green, blue);
        }
    }
}
