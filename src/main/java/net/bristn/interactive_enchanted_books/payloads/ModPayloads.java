package net.bristn.interactive_enchanted_books.payloads;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ModPayloads {

    public static void registerModPayloads() {
        CommonModInitializer.LOGGER.info("Register ModPayloads for" + CommonModInitializer.MOD_ID);

        registerServerToClientPayloads();
    }

    private static void registerServerToClientPayloads() {
        PayloadTypeRegistry.clientboundPlay().register(SyncLecternItemPayload.ID, SyncLecternItemPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncLecternBookCountPayload.ID, SyncLecternBookCountPayload.CODEC);
    }
}
