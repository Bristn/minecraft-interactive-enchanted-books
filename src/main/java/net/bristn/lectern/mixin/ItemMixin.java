package net.bristn.lectern.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.bristn.lectern.payloads.OpenEnchantedBookPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Injects the use method of the item to open the enchanted book ui when
 * right-clicking with an enchanted book item
 */
@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void onUseItem(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> method) {
        var stack = player.getItemInHand(hand);
        var item = stack.getItem();
        if (item != Items.ENCHANTED_BOOK) {
            return;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            var payload = new OpenEnchantedBookPayload(stack);
            ServerPlayNetworking.send(serverPlayer, payload);
            method.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
