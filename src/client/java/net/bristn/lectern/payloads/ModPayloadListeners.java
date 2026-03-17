package net.bristn.lectern.payloads;

import net.bristn.lectern.screen.LecternEnchantedBookScreen;
import net.bristn.lectern.screen.handlers.LecternScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

public class ModPayloadListeners {

    public static void registerModPayloadListeners() {
        ClientPlayNetworking.registerGlobalReceiver(SyncLecternItemPayload.ID, (payload, context) -> {
            handleSyncLecternItemPayload(payload, context);
        });

        ClientPlayNetworking.registerGlobalReceiver(OpenLecternPayload.ID, (payload, context) -> {
            handleOpenLecternPayload(payload, context);
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
    }

    /**
     * Use a custom payload to open the lectern screen with the correct item.
     * The tested code using mojangs default "createMenu" method did not sync the
     * book. Therefore use custom payload and open the menu on the client
     * 
     * @param payload
     * @param context
     */
    private static void handleOpenLecternPayload(OpenLecternPayload payload, Context context) {
        context.client().execute(() -> {
            var inventory = context.client().player.getInventory();
            var menu = new LecternScreenHandler(0, inventory, payload.book());

            context.client().player.containerMenu = menu;
            var screen = new LecternEnchantedBookScreen(menu, inventory, Component.empty());
            context.client().setScreen(screen);
        });
    }

}
