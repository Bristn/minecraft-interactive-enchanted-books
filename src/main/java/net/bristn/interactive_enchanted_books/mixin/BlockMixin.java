package net.bristn.interactive_enchanted_books.mixin;

import net.bristn.interactive_enchanted_books.utility.EnchantmentUtility;
import net.bristn.interactive_enchanted_books.utility.interfaces.LecternAccess;
import net.bristn.interactive_enchanted_books.utility.wrappers.ParticleWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects into the animateTick method to spawn particles based on the enchanted book of the lectern
 */
@Mixin(Block.class)
public class BlockMixin {
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
        if (EnchantmentUtility.isEnchantedBookLike(item) == false) {
            return;
        }

        var access = ((LecternAccess) lectern);
        access.updateCacheUsingStack(lectern.getBook());

        // Only spawn particles if there is at least one chiseled book present
        var bookCount = Math.clamp(access.getChiseledBookshelfBookCount(), 0, ChiseledBookShelfBlockEntity.MAX_BOOKS_IN_STORAGE);
        if (bookCount == 0) {
            return;
        }

        // Only spawn a particle every other tick
        if (random.nextFloat() > 0.5) {
            return;
        }

        // Determine the random particle & spawn it
        var particles = access.getCachedParticles();
        var particleWrapper = ParticleWrapper.getRandomParticle(particles, random);

        var currentPage = access.getCurrentPage();
        var pageCount = access.getPageCount();
        if (pageCount > 1 && currentPage != 0) {
            particleWrapper = access.getParticleForPage(currentPage);
        }

        // Determine the normalized level (with 1 being the highest level the enchantment can reach)
        var enchantmentWrapper = particleWrapper.enchantment;
        var enchantment = enchantmentWrapper.enchantment();
        var enchantmentLevel = enchantmentWrapper.enchantmentLevel();
        var maxEnchantmentLevel = enchantment.getMaxLevel();
        var normalizedLevel = (float) enchantmentLevel / (float) maxEnchantmentLevel;

        var particle = particleWrapper.particle;
        renderEnchantmentParticle(particle, world, pos, random, lectern, normalizedLevel, bookCount);
    }

    /**
     * Spawns a particle using the enchantment and the lectern position. The particle origin is the
     * center of the book, whilst all particles move away from the book in a random direction
     */
    private void renderEnchantmentParticle(ParticleOptions particle, Level world, BlockPos pos, RandomSource random,
            LecternBlockEntity lectern, float normalizedLevel, int bookCount) {

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

        // At most spawns 2 particles at once if the enchantment is at the highest level
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

        // Encodes the book count in the y speed. The hundred digit being used as the book count
        // The resulting speed can still be negative
        var sign = Math.signum(ySpeed);
        var encodedSpeed = sign * 100.0 * bookCount + ySpeed;

        // Offset the particles to not all spawn in the same spot
        var offset = movementDir.mul(0.1f);
        world.addParticle(particle, x + offset.x, y + offset.y, z + offset.z, xSpeed, encodedSpeed, zSpeed);
    }

    private static float getRandomAngleOffset(RandomSource random) {
        return (random.nextFloat() * MAX_PARTICLE_RANDOM_ANGLE * 2) - MAX_PARTICLE_RANDOM_ANGLE;
    }
}