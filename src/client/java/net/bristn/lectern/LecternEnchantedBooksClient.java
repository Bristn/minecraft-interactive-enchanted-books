package net.bristn.lectern;

import net.bristn.lectern.payloads.ItemStackSyncS2CLoad;
import net.bristn.lectern.screen.LecternEnchantedBookScreen;
import net.bristn.lectern.screen.handlers.LecternScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

public class LecternEnchantedBooksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MenuScreens.register(LecternScreenHandler.SCREEN_HANDLER, LecternEnchantedBookScreen::new);

        PayloadTypeRegistry.playS2C().register(ItemStackSyncS2CLoad.PACKET_ID, ItemStackSyncS2CLoad.PACKET_CODEC);
        ClientPlayNetworking.registerGlobalReceiver(ItemStackSyncS2CLoad.PACKET_ID, ((payload, context) -> {
            if (context.player().clientLevel == null)
                return;
            if (context.player().clientLevel.getBlockEntity(payload.pos()) instanceof LecternBlockEntity lectern) {
                lectern.setBook(payload.stack());
                lectern.setChanged();
            }
        }));
    }

}
