package net.bristn.interactive_enchanted_books.payloads;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record SyncLecternItemPayload(BlockPos pos, ItemStack book, Integer page) implements CustomPacketPayload {

    public static final Type<SyncLecternItemPayload> ID = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID, "sync_lectern_item"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncLecternItemPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SyncLecternItemPayload::pos, ItemStack.STREAM_CODEC, SyncLecternItemPayload::book,
            ByteBufCodecs.INT, SyncLecternItemPayload::page, SyncLecternItemPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}