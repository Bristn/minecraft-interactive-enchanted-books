package net.bristn.lectern.payloads;

import net.bristn.lectern.LecternEnchantedBooks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record SyncLecternItemPayload(BlockPos pos, ItemStack book) implements CustomPacketPayload {

    public static final Type<SyncLecternItemPayload> ID = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID, "sync_lectern_item"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncLecternItemPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SyncLecternItemPayload::pos, ItemStack.STREAM_CODEC, SyncLecternItemPayload::book,
            SyncLecternItemPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}