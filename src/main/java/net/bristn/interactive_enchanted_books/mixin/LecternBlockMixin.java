package net.bristn.interactive_enchanted_books.mixin;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.bristn.interactive_enchanted_books.gamerules.ModGameRules;
import net.bristn.interactive_enchanted_books.payloads.SyncLecternBookCountPayload;
import net.bristn.interactive_enchanted_books.utility.interfaces.LecternAccess;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;

@Mixin(BlockBehaviour.class)
public abstract class LecternBlockMixin {

    @Inject(method = "neighborChanged", at = @At("HEAD"))
    private void onNeighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation,
            boolean movedByPiston, CallbackInfo method) {

        if (state.getBlock() instanceof LecternBlock == false) {
            return;
        }

        if (state.getValue(LecternBlock.HAS_BOOK) == false) {
            return;
        }

        if (level instanceof ServerLevel == false) {
            return;
        }

        // Keep track of the chiseled bookshelf book count (Used for particle lifetime)
        updateChiseledBookshelfBookCount(level, pos);

        var serverLevel = (ServerLevel) level;
        var signalChangesPage = serverLevel.getGameRules().get(ModGameRules.SIGNAL_CHANGES_LECTERN_PAGE);
        if (signalChangesPage == false) {
            return;
        }

        var isPowered = level.hasNeighborSignal(pos);
        var blockEntity = level.getBlockEntity(pos);
        var lectern = (LecternBlockEntity) blockEntity;
        var access = (LecternAccess) lectern;
        var wasPowered = access.getWasPowered();
        access.setWasPowered(isPowered);

        // Only keep the rising edge signals
        if (isPowered == false || wasPowered == true) {
            return;
        }

        advancePage(access);
    }

    private void updateChiseledBookshelfBookCount(Level level, BlockPos pos) {
        var maxBookCount = 0;
        var neighbors = List.of(pos.north(), pos.east(), pos.south(), pos.west(), pos.above(), pos.below());
        for (var neighborPos : neighbors) {
            var neighbor = level.getBlockEntity(neighborPos);
            if (neighbor instanceof ChiseledBookShelfBlockEntity == false) {
                continue;
            }

            var bookshelf = (ChiseledBookShelfBlockEntity) neighbor;
            var bookCount = 0;
            for (var stack : bookshelf.getItems()) {
                bookCount += stack == ItemStack.EMPTY ? 0 : 1;
            }

            maxBookCount = Math.max(maxBookCount, bookCount);
            if (maxBookCount == ChiseledBookShelfBlockEntity.MAX_BOOKS_IN_STORAGE) {
                break;
            }
        }

        // Update the book count on the server
        var blockEntity = level.getBlockEntity(pos);
        var lectern = (LecternBlockEntity) blockEntity;
        var access = (LecternAccess) lectern;
        access.setChiseledBookshelfBookCount(maxBookCount);

        // Send the networking message to all players
        var payload = new SyncLecternBookCountPayload(pos, maxBookCount);
        for (var player : PlayerLookup.level((ServerLevel) level)) {
            ServerPlayNetworking.send(player, payload);
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
