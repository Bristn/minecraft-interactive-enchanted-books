package net.bristn.interactive_enchanted_books.payloads;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ServerRunsModPayload(boolean installed, boolean echoCraftable) implements CustomPacketPayload {

    public static final Type<ServerRunsModPayload> ID = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID, "server_runs_mod"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerRunsModPayload> CODEC = StreamCodec.composite( //
            ByteBufCodecs.BOOL, ServerRunsModPayload::installed, //
            ByteBufCodecs.BOOL, ServerRunsModPayload::echoCraftable, //
            ServerRunsModPayload::new //
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
