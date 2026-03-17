package net.bristn.lectern.mixin;

import com.llamalad7.mixinextras.sugar.Local;

import net.bristn.lectern.payloads.SyncLecternItemPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

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

    /**
     * The default networking does not send the full book item to the clients.
     * Update the method to send a custom packet, which internally sets the book on
     * the client side
     * 
     * @param ci
     */
    @Inject(method = "setChanged()V", at = @At("TAIL"))
    private void addPacketToMarkDirty(CallbackInfo ci) {
        var blockEntity = (BlockEntity) (Object) this;
        if (blockEntity instanceof LecternBlockEntity == false) {
            return;
        }

        // Only continue if this is the server world
        var level = this.getLevel();
        if (level == null || level.isClientSide() == true) {
            return;
        }

        // Send a custom network packet to properly save the book of the lectern
        var lectern = (LecternBlockEntity) blockEntity;
        var payload = new SyncLecternItemPayload(lectern.getBlockPos(), lectern.getBook());
        for (var player : PlayerLookup.level((ServerLevel) level)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    /**
     * Handles sending the book item of the lectern to the player. Otherwise the
     * player would not be able get the book of the lectern
     * 
     * @param cir
     * @param registryLookup
     */
    @Inject(method = "getUpdateTag", at = @At("HEAD"), cancellable = true)
    private void addInitialNbt(CallbackInfoReturnable<CompoundTag> cir,
            @Local(argsOnly = true) HolderLookup.Provider registryLookup) {
        var blockEntity = (BlockEntity) (Object) this;
        if (blockEntity instanceof LecternBlockEntity == false) {
            return;
        }

        cir.setReturnValue(this.saveWithoutMetadata(registryLookup));
    }

    @Inject(method = "getUpdatePacket", at = @At("HEAD"), cancellable = true)
    private void addLecternUpdatePacket(CallbackInfoReturnable<Packet<ClientGamePacketListener>> cir) {
        var blockEntity = (BlockEntity) (Object) this;
        if (blockEntity instanceof LecternBlockEntity == false) {
            return;
        }

        cir.setReturnValue(ClientboundBlockEntityDataPacket.create((BlockEntity) (Object) this));
    }
}