package net.bristn.lectern.mixin.particle;

import net.bristn.lectern.EnchantmentUtility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class LecternParticleMixin {

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
        var entry = EnchantmentUtility.getParticleForEnchantment(enchantment);
        if (entry == null) {
            return;
        }

        var particle = (ParticleOptions) entry.particle;

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
}