package net.bristn.lectern.payloads;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

public class ModPayloadListeners {

    public static void registerModPayloadListeners() {
        ClientPlayNetworking.registerGlobalReceiver(SyncLecternItemPayload.ID, (payload, context) -> {
            handleSyncLecternItemPayload(payload, context);
        });

        ClientPlayNetworking.registerGlobalReceiver(OpenLecternPayload.ID, (payload, context) -> {
            OpenLecternPayloadListener.handleOpenLecternPayload(payload, context);
        });
    }

    /**
     * Uses a custom networking message to keep track of what book the lectern
     * contains. The regular networking from minecraft does not sync the complete
     * book, but a version with reduced information
     * 
     * @param payload
     * @param context
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
            lectern.setChanged();
        }

        // TODO: Determine the particles here (Move to separate class)
    }
}
