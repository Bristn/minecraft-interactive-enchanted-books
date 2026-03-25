package net.bristn.lectern.mixin;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.bristn.lectern.LecternEnchantedBooks;
import net.bristn.lectern.payloads.OpenLecternPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

@Mixin(Item.class)
public abstract class EnchantedBookItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void onUseItem(Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> originalMethod) {

        var stack = player.getItemInHand(hand);
        var item = stack.getItem();
        if (item != Items.ENCHANTED_BOOK) {
            return;
        }

        // TODO: Pass additional data to show different ui buttons

        if (player instanceof ServerPlayer serverPlayer) {
            var payload = new OpenLecternPayload(stack);
            ServerPlayNetworking.send(serverPlayer, payload);
            originalMethod.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
