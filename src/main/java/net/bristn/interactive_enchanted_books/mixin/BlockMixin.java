package net.bristn.interactive_enchanted_books.mixin;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.bristn.interactive_enchanted_books.utility.interfaces.LecternAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects into the animateTick method to spawn particles based on the enchanted
 * book of the lectern
 */
@Mixin(Block.class)
public class BlockMixin {
    private static final float MAX_PARTICLE_CHANCE = 0.6f;
    private static final float MIN_PARTICLE_CHANCE = 0.2f;
    private static final float RANGE_PARTICLE_CHANCE = MAX_PARTICLE_CHANCE - MIN_PARTICLE_CHANCE;

    private static final float MAX_PARTICLE_RANDOM_SPEED = 0.25f;
    private static final float MAX_PARTICLE_RANDOM_ANGLE = 90f;

    @Inject(method = "animateTick", at = @At("HEAD"))
    private void renderParticles(BlockState state, Level world, BlockPos pos, RandomSource random, CallbackInfo originalMethod) {
        if (world.isClientSide() == false) {
            return;
        }

        var blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof LecternBlockEntity == false) {
            return;
        }

        var lectern = (LecternBlockEntity) blockEntity;
        var item = lectern.getBook().getItem();
        if (item != Items.ENCHANTED_BOOK) {
            return;
        }

        var access = ((LecternAccess) lectern);
        access.updateCacheUsingStack(lectern.getBook());

        // If the lectern is on a specific enchantment page (not the cover), show the
        // respective particle only. Otherwise loop the different enchantment particles
        var enchantments = access.getCachedEnchantments();
        var particleIndex = access.updateParticleIndex();
        if (particleIndex >= enchantments.size() || particleIndex < 0) {
            var message = "Particle index {} is not valid. Count of enchantments {}";
            CommonModInitializer.LOGGER.error(message, particleIndex, enchantments.size());
            return;
        }

        // Determine the chance for a particle to spawn. The chance depends on the
        // current and max enchantment level. The higher the relative level, the more
        // particles are spawned. Sharpness 5 spawns the same as mending, as both are on
        // the highest possible level
        var wrapper = enchantments.get(particleIndex);
        var enchantment = wrapper.enchantment();
        var enchantmentLevel = wrapper.enchantmentLevel();
        var maxEnchantmentLevel = enchantment.getMaxLevel();
        var stepPerLevel = RANGE_PARTICLE_CHANCE / maxEnchantmentLevel;
        var chance = MIN_PARTICLE_CHANCE + stepPerLevel * enchantmentLevel;
        if (random.nextFloat() > chance) {
            return;
        }

        var particle = access.getCachedParticles().get(particleIndex);
        var normalizedLevel = (float) enchantmentLevel / (float) maxEnchantmentLevel;
        renderEnchantmentParticle(particle, world, pos, random, lectern, normalizedLevel);
    }

    /**
     * Spawns a particle using the enchantment and the lectern position. The
     * particle origin is the center of the book, whilst all particles move away
     * from the book in a random direction
     */
    private void renderEnchantmentParticle(ParticleOptions particle, Level world, BlockPos pos, RandomSource random,
            LecternBlockEntity lectern, float normalizedLevel) {

        // USe the block direction as the base of the direction vector
        var blockState = lectern.getBlockState();
        var dir = blockState.getValue(LecternBlock.FACING).getUnitVec3();

        // Determines the base movement speed. Visually the particles emit from the book
        // and only move in the opened hemisphere
        var up = new Vector3f(0, 1, 0);
        var perpAxis = up.cross(new Vector3f((float) dir.x, (float) dir.y, (float) dir.z), new Vector3f()).normalize();
        var baseDir = up.rotateAxis((float) Math.toRadians(90 - 67.5), perpAxis.x, perpAxis.y, perpAxis.z);

        // Defines the relative center of the book on the lectern
        var basePos = new Vector3f(pos.getX() + 0.5f, pos.getY() + 1.25f, pos.getZ() + 0.5f);

        // For higher enchantment levels, spawn more than one particle at a time
        // At most spawns 3 particles at once if the enchantment is at the highest level
        var maxParticles = 1 + normalizedLevel * 2f;
        for (int i = 0; i < maxParticles; i++) {
            var x = basePos.x + (random.nextDouble() - 0.5) * 0.25;
            var y = basePos.y + (random.nextDouble() - 0.5) * 0.25;
            var z = basePos.z + (random.nextDouble() - 0.5) * 0.25;

            // Add a random offset to the movement direction
            var movementDir = new Vector3f(baseDir.x, baseDir.y, baseDir.z);
            var pitchOffset = (float) Math.toRadians(getRandomAngleOffset(random));
            var yawOffset = (float) Math.toRadians(getRandomAngleOffset(random));
            movementDir.rotateAxis(pitchOffset, perpAxis.x, perpAxis.y, perpAxis.z);
            movementDir.rotateAxis(yawOffset, 0, 1, 0);
            movementDir.normalize();

            // Get speed with randomness of +- 10%
            var randomSpeed = random.nextFloat() * MAX_PARTICLE_RANDOM_SPEED + 1 - MAX_PARTICLE_RANDOM_SPEED / 2;
            var speedFactor = 30 * randomSpeed;
            var xSpeed = movementDir.x / speedFactor;
            var ySpeed = movementDir.y / speedFactor;
            var zSpeed = movementDir.z / speedFactor;

            // Offset the particles to not all spawn in the same spot
            var offset = movementDir.mul(0.1f);
            world.addParticle((ParticleOptions) particle, x + offset.x, y + offset.y, z + offset.z, xSpeed, ySpeed, zSpeed);
        }
    }

    private static float getRandomAngleOffset(RandomSource random) {
        return (random.nextFloat() * MAX_PARTICLE_RANDOM_ANGLE * 2) - MAX_PARTICLE_RANDOM_ANGLE;
    }
}