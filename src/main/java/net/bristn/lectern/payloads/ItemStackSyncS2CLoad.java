package net.bristn.lectern.payloads;

import net.bristn.lectern.LecternEnchantedBooks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

public record ItemStackSyncS2CLoad(BlockPos pos, ItemStack stack) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ItemStackSyncS2CLoad> PACKET_ID = new CustomPacketPayload.Type<>(LecternEnchantedBooks.ITEM_SYNC);
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemStackSyncS2CLoad> PACKET_CODEC = StreamCodec.ofMember(ItemStackSyncS2CLoad::write,
            ItemStackSyncS2CLoad::new);

    public ItemStackSyncS2CLoad(RegistryFriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readBoolean() ? ItemStack.EMPTY : ItemStack.STREAM_CODEC.decode(buf));
    }

    public static void write(ItemStackSyncS2CLoad load, RegistryFriendlyByteBuf buf) {
        buf.writeBlockPos(load.pos);
        buf.writeBoolean(load.stack.isEmpty());
        if (!load.stack.isEmpty())
            ItemStack.STREAM_CODEC.encode(buf, load.stack);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }

}