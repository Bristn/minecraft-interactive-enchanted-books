package net.bristn.interactive_enchanted_books.payloads;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.bristn.interactive_enchanted_books.gamerules.ModGameRules;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class ModPayloads {

    public static void registerModPayloads() {
        CommonModInitializer.LOGGER.info("Register ModPayloads for" + CommonModInitializer.MOD_ID);

        registerServerToClientPayloads();
    }

    private static void registerServerToClientPayloads() {
        PayloadTypeRegistry.clientboundPlay().register(SyncLecternItemPayload.ID, SyncLecternItemPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SyncLecternBookCountPayload.ID, SyncLecternBookCountPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ServerRunsModPayload.ID, ServerRunsModPayload.CODEC);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ServerPlayerEvents.JOIN.register(player -> {
                var echoCraftable = server.getGameRules().get(ModGameRules.CRAFTABLE_ENCHANTMENT_ECHO);
                ServerPlayNetworking.send(player, new ServerRunsModPayload(true, echoCraftable));
            });
        });
    }
}
