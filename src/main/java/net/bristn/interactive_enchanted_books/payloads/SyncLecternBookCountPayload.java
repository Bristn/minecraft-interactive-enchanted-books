package net.bristn.interactive_enchanted_books.payloads;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SyncLecternBookCountPayload(BlockPos pos, Integer bookCount) implements CustomPacketPayload {

    public static final Type<SyncLecternBookCountPayload> ID = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID, "sync_lectern_book_count"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncLecternBookCountPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SyncLecternBookCountPayload::pos, ByteBufCodecs.INT, SyncLecternBookCountPayload::bookCount,
            SyncLecternBookCountPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}