package net.bristn.lectern.payloads;

import net.bristn.lectern.LecternEnchantedBooks;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record OpenEnchantedBookPayload(ItemStack book) implements CustomPacketPayload {

    public static final Type<OpenEnchantedBookPayload> ID = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID, "open_lectern"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenEnchantedBookPayload> CODEC = StreamCodec
            .composite(ItemStack.STREAM_CODEC, OpenEnchantedBookPayload::book, OpenEnchantedBookPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
