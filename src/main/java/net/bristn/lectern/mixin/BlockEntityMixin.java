package net.bristn.lectern.mixin;

import com.llamalad7.mixinextras.sugar.Local;

import net.bristn.lectern.payloads.ItemStackSyncS2CLoad;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import java.util.Collection;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

    @Shadow
    @Nullable
    public abstract Level getLevel();

    @Shadow
    public abstract BlockPos getBlockPos();

    @Shadow
    public abstract CompoundTag saveWithoutMetadata(HolderLookup.Provider registryLookup);

    @Inject(method = "setChanged()V", at = @At("TAIL"))
    private void addPacketToMarkDirty(CallbackInfo ci) {
        BlockEntity blockEntity = (BlockEntity) (Object) this;
        boolean isLectern = blockEntity instanceof LecternBlockEntity;
        if (isLectern == false) {
            return;
        }

        // Only continue if this is the server world
        Level world = this.getLevel();
        boolean isClient = world.isClientSide();
        if (world == null || isClient == true) {
            return;
        }

        ServerLevel serverWorld = (ServerLevel) world;
        LecternBlockEntity lectern = (LecternBlockEntity) blockEntity;
        ItemStackSyncS2CLoad payload = new ItemStackSyncS2CLoad(getBlockPos(), lectern.getBook());

        // Send the packet to the players
        Collection<ServerPlayer> players = PlayerLookup.tracking(serverWorld, getBlockPos());
        for (ServerPlayer player : players) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    @Inject(method = "getUpdateTag", at = @At("HEAD"), cancellable = true)
    private void addInitialNbt(CallbackInfoReturnable<CompoundTag> cir, @Local(argsOnly = true) HolderLookup.Provider registryLookup) {
        BlockEntity blockEntity = (BlockEntity) (Object) this;
        boolean isLectern = blockEntity instanceof LecternBlockEntity;
        if (isLectern == false) {
            return;
        }

        cir.setReturnValue(this.saveWithoutMetadata(registryLookup));
    }

    @Inject(method = "getUpdatePacket", at = @At("HEAD"), cancellable = true)
    private void addLecternUpdatePacket(CallbackInfoReturnable<Packet<ClientGamePacketListener>> cir) {
        BlockEntity blockEntity = (BlockEntity) (Object) this;
        boolean isLectern = blockEntity instanceof LecternBlockEntity;
        if (isLectern == false) {
            return;
        }

        cir.setReturnValue(ClientboundBlockEntityDataPacket.create((BlockEntity) (Object) this));
    }
}