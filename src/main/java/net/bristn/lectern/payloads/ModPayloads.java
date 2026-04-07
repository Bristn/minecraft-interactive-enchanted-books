package net.bristn.lectern.payloads;

import net.bristn.lectern.LecternEnchantedBooks;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ModPayloads {

    public static void registerModPayloads() {
        LecternEnchantedBooks.LOGGER.info("Register ModPayloads for" + LecternEnchantedBooks.MOD_ID);

        registerServerToClientPayloads();
    }

    private static void registerServerToClientPayloads() {
        PayloadTypeRegistry.clientboundPlay().register(SyncLecternItemPayload.ID, SyncLecternItemPayload.CODEC);
    }
}
