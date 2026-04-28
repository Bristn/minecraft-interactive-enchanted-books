package net.bristn.interactive_enchanted_books.mixin;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.bristn.interactive_enchanted_books.gamerules.ModGameRules;
import net.bristn.interactive_enchanted_books.utility.interfaces.LecternAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;

@Mixin(BlockBehaviour.class)
public abstract class LecternBlockMixin {

    @Inject(method = "neighborChanged", at = @At("HEAD"))
    private void onNeighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation,
            boolean movedByPiston, CallbackInfo method) {

        if (state.getBlock() instanceof LecternBlock == false || level.isClientSide()) {
            return;
        }

        if (state.getValue(LecternBlock.HAS_BOOK) == false) {
            return;
        }

        if (level instanceof ServerLevel == false) {
            return;
        }

        var serverLevel = (ServerLevel) level;
        var signalChangesPage = serverLevel.getGameRules().get(ModGameRules.SIGNAL_CHANGES_LECTERN_PAGE);
        if (signalChangesPage == false) {
            return;
        }

        var isPowered = level.hasNeighborSignal(pos);
        var blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof LecternBlockEntity lectern) {
            var access = (LecternAccess) lectern;
            var wasPowered = access.getWasPowered();
            access.setWasPowered(isPowered);

            // Only keep the rising edge signals
            if (isPowered == false || wasPowered == true) {
                return;
            }

            advancePage(access);
        }
    }

    private void advancePage(LecternAccess accessor) {
        int current = accessor.getCurrentPage();
        int total = accessor.getPageCount();

        // Advance — wrap around to beginning if at the last page
        int page = (current + 1) % total;
        accessor.setCurrentPage(page);
    }
}
