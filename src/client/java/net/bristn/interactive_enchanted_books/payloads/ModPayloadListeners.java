package net.bristn.interactive_enchanted_books.payloads;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.bristn.interactive_enchanted_books.utility.interfaces.LecternAccess;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

public class ModPayloadListeners {

    public static void registerModPayloadListeners() {
        ClientPlayNetworking.registerGlobalReceiver(SyncLecternItemPayload.ID, (payload, context) -> {
            handleSyncLecternItemPayload(payload, context);
        });

        ClientPlayNetworking.registerGlobalReceiver(SyncLecternBookCountPayload.ID, (payload, context) -> {
            handleSyncLecternBookCountPayload(payload, context);
        });

        ClientPlayNetworking.registerGlobalReceiver(ServerRunsModPayload.ID, (payload, context) -> {
            handleServerRunsModPayload(payload, context);
        });
    }

    /**
     * Uses a custom networking message to keep track of what book the lectern contains. The regular
     * networking from minecraft does not sync the complete book, but a version with reduced information
     */
    private static void handleSyncLecternItemPayload(SyncLecternItemPayload payload, Context context) {
        var level = context.client().level;
        if (level == null) {
            return;
        }

        var pos = payload.pos();
        var book = payload.book();

        // Update the book of the lectern and mark it as dirty
        var blockEntity = level.getBlockEntity(pos);
        var lectern = (LecternBlockEntity) blockEntity;
        if (lectern != null) {
            lectern.setBook(book);

            var access = (LecternAccess) lectern;
            access.setCurrentPage(payload.page());

            lectern.setChanged();
        }
    }

    /**
     * Uses a custom networking message to keep track of how many books are in the nearest chiseled
     * bookshelf next to a lectern
     */
    private static void handleSyncLecternBookCountPayload(SyncLecternBookCountPayload payload, Context context) {
        var level = context.client().level;
        if (level == null) {
            return;
        }

        var pos = payload.pos();
        var bookCount = payload.bookCount();

        // Update the book of the lectern and mark it as dirty
        var blockEntity = level.getBlockEntity(pos);
        var lectern = (LecternBlockEntity) blockEntity;
        if (lectern != null) {
            var access = (LecternAccess) lectern;
            access.setChiseledBookshelfBookCount(bookCount);

            lectern.setChanged();
        }
    }

    /**
     * Uses a custom networking message sent by the server to allow clients to query if the mod is also
     * installed on the server. Can be used to disable ui functions if the server does not run the mod
     */
    private static void handleServerRunsModPayload(ServerRunsModPayload payload, Context context) {
        var level = context.client().level;
        if (level == null) {
            return;
        }

        CommonModInitializer.isInstalledOnServer = payload.installed();
        CommonModInitializer.areEchosCraftable = payload.echoCraftable();
    }
}
