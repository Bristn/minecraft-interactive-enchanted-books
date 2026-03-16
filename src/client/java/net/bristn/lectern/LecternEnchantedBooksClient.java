package net.bristn.lectern;

import net.bristn.lectern.particle.ModParticles;
import net.bristn.lectern.particle.SparkleParticle;
import net.bristn.lectern.payloads.ItemStackSyncS2CLoad;
import net.bristn.lectern.screen.LecternEnchantedBookScreen;
import net.bristn.lectern.screen.handlers.LecternScreenHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleProviderRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

public class LecternEnchantedBooksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ParticleProviderRegistry.getInstance().register(ModParticles.SPARKLE_PARTICLE, SparkleParticle.Provider::new);

        MenuScreens.register(LecternScreenHandler.SCREEN_HANDLER, LecternEnchantedBookScreen::new);

        // Uses a custom networking message to keep track of what book the lectern
        // contains. The regular networking from minecraft does not sync the complete
        // book, but a version with reduced information
        ClientPlayNetworking.registerGlobalReceiver(ItemStackSyncS2CLoad.ID, (payload, context) -> {
            var level = context.client().level;
            if (level == null) {
                return;
            }

            var pos = payload.pos();
            var book = payload.stack();

            // Update the book of the lectern and mark it as dirty
            var blockEntity = level.getBlockEntity(pos);
            var lectern = (LecternBlockEntity) blockEntity;
            if (lectern != null) {
                lectern.setBook(book);
                lectern.setChanged();
            }
        });
    }

}
