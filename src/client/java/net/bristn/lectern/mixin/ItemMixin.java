package net.bristn.lectern.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.bristn.lectern.screen.EnchantedBookAccess;
import net.bristn.lectern.screen.EnchantedBookViewScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Client-only injection on the use method. Used if the mod is not installed on
 * the server
 */
@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void onUseItem(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> method) {
        if (player instanceof LocalPlayer == false) {
            return;
        }

        var stack = player.getItemInHand(hand);
        var item = stack.getItem();
        if (item != Items.ENCHANTED_BOOK) {
            return;
        }

        var screen = new EnchantedBookViewScreen(EnchantedBookAccess.fromItem(stack));
        var client = Minecraft.getInstance();
        client.setScreen(screen);
    }
}
