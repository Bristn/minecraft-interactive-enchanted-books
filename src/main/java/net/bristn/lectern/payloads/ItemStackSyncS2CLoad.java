package net.bristn.lectern.payloads;

import net.bristn.lectern.LecternEnchantedBooks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record ItemStackSyncS2CLoad(BlockPos pos, ItemStack stack) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ItemStackSyncS2CLoad> ID = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID, "item_stack_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackSyncS2CLoad> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ItemStackSyncS2CLoad::pos,
            ItemStack.OPTIONAL_STREAM_CODEC, ItemStackSyncS2CLoad::stack,
            ItemStackSyncS2CLoad::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}