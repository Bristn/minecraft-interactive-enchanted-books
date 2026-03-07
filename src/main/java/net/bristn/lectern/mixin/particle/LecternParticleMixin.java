package net.bristn.lectern.mixin.particle;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class LecternParticleMixin {

    @Inject(method = "animateTick", at = @At("TAIL"))
    private void renderParticles(BlockState state, Level world, BlockPos pos, RandomSource random, CallbackInfo callback) {
        if (state.getBlock() != Blocks.LECTERN) {
            return;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof LecternBlockEntity == false) {
            return;
        }

        LecternBlockEntity lectern = (LecternBlockEntity) blockEntity;
        Item bookItem = lectern.getBook().getItem();
        if (bookItem != Items.ENCHANTED_BOOK) {
            return;
        }

        if (random.nextBoolean()) {
            return;
        }

        for (int i = 0; i < 3; i++) {
            double xChange = (random.nextBoolean() ? -0.5 : 0.5) * random.nextDouble();
            double yChange = (random.nextBoolean() ? -0.5 : 0.5) * random.nextDouble();
            double zChange = (random.nextBoolean() ? -0.5 : 0.5) * random.nextDouble();
            double d = (double) pos.getX() + xChange + 0.5;
            double e = (double) pos.getY() + yChange + 2.5;
            double f = (double) pos.getZ() + zChange + 0.5;
            double g = (xChange - 0.5);
            double h = (yChange - 0.6);
            double j = (zChange - 0.5);
            world.addParticle(ParticleTypes.ENCHANT, d, e, f, g, h, j);
        }
    }
}