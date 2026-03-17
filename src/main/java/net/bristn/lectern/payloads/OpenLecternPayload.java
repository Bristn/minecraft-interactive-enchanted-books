package net.bristn.lectern.payloads;

import net.bristn.lectern.LecternEnchantedBooks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record OpenLecternPayload(BlockPos pos, ItemStack book) implements CustomPacketPayload {

    public static final Type<OpenLecternPayload> ID = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID, "open_lectern"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenLecternPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, OpenLecternPayload::pos,
            ItemStack.STREAM_CODEC, OpenLecternPayload::book,
            OpenLecternPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
