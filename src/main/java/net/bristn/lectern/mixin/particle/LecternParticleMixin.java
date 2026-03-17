package net.bristn.lectern.mixin.particle;

import net.bristn.lectern.LecternEnchantedBooks;
import net.bristn.lectern.resources.loader.EnchantmentParticleLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class LecternParticleMixin {
    private static final Logger LOGGER = LecternEnchantedBooks.LOGGER;

    @Inject(method = "animateTick", at = @At("HEAD"))
    private void renderParticles(BlockState state, Level world, BlockPos pos, RandomSource random,
            CallbackInfo originalMethod) {

        if (world.isClientSide() == false) {
            return;
        }

        var blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof LecternBlockEntity == false) {
            return;
        }

        var lectern = (LecternBlockEntity) blockEntity;
        Item item = lectern.getBook().getItem();
        if (item != Items.ENCHANTED_BOOK) {
            return;
        }

        if (random.nextBoolean()) {
            return;
        }

        // Using the custom network packet, the lectern contains the proper book
        var stack = lectern.getBook();
        var enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);

        for (var entry : enchantments.entrySet()) {
            var enchantment = entry.getKey().value();

            // TODO: Render multiple particles

            renderEnchantmentParticle(enchantment, world, pos, random);
        }
    }

    /**
     * 
     * @param enchantment
     */
    private void renderEnchantmentParticle(Enchantment enchantment, Level world, BlockPos pos, RandomSource random) {
        var particle = getParticleForEnchantment(enchantment);

        for (int i = 0; i < 3; i++) {
            var xChange = (random.nextBoolean() ? -0.5 : 0.5) * random.nextDouble();
            var yChange = (random.nextBoolean() ? -0.5 : 0.5) * random.nextDouble();
            var zChange = (random.nextBoolean() ? -0.5 : 0.5) * random.nextDouble();
            var x = (double) pos.getX() + xChange + 0.5;
            var y = (double) pos.getY() + yChange + 2.5;
            var z = (double) pos.getZ() + zChange + 0.5;
            var xSpeed = (xChange - 0.5);
            var ySpeed = (yChange - 0.6);
            var zSpeed = (zChange - 0.5);
            world.addParticle((ParticleOptions) particle, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }

    /**
     * Determines the particle from the enchantment. Tries to read the
     * enchantment_particle.json. If any error occurs, the default enchantment
     * particle is returned
     * 
     * @param enchantment
     * @return
     */
    private ParticleOptions getParticleForEnchantment(Enchantment enchantment) {
        try {
            var enchantmentKey = getEnchantmentIdentifier(enchantment);
            if (enchantmentKey == null) {
                LOGGER.info("LecternParticle: Unable to get the id key of enchantment {}", enchantment.toString());
                return ParticleTypes.ENCHANT;
            }

            var enchantmentId = Identifier.parse(enchantmentKey);
            var enchantmentParticles = EnchantmentParticleLoader.getMap();
            if (enchantmentParticles.containsKey(enchantmentId) == false) {
                LOGGER.info("LecternParticle: Enchantment {} is not registered in th json", enchantment.toString());
                return ParticleTypes.ENCHANT;
            }

            var enchantmentParticle = enchantmentParticles.get(enchantmentId);
            return enchantmentParticle.particle;
        } catch (Exception e) {
            LOGGER.info("LecternParticle: An error occurred getting the particle for {}", enchantment.toString());
            e.printStackTrace();
            return ParticleTypes.ENCHANT;
        }
    }

    private String getEnchantmentIdentifier(Enchantment enchantment) {
        var keyContent = enchantment.description().getContents();
        if (keyContent instanceof TranslatableContents translatable) {
            return translatable.getKey();
        }

        return null;
    }

}