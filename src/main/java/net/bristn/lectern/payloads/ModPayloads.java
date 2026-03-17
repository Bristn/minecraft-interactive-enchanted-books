package net.bristn.lectern.payloads;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ModPayloads {

    public static void registerModPayloads() {
        registerServerToClientPayloads();
    }

    private static void registerServerToClientPayloads() {
        PayloadTypeRegistry.clientboundPlay().register(SyncLecternItemPayload.ID, SyncLecternItemPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(OpenLecternPayload.ID, OpenLecternPayload.CODEC);
    }
}
