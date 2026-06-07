package net.bristn.interactive_enchanted_books.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.bristn.interactive_enchanted_books.screen.EnchantedBookAccess;
import net.bristn.interactive_enchanted_books.screen.EnchantedBookViewScreen;
import net.bristn.interactive_enchanted_books.utility.EnchantmentUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * Client-only injection on the use method. Used if the mod is not installed on the server
 */
@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void onUseItem(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> method) {
        if (player instanceof LocalPlayer == false) {
            return;
        }

        var stack = player.getItemInHand(hand);
        if (EnchantmentUtility.isEnchantedBookLike(stack) == false) {
            return;
        }

        var screen = new EnchantedBookViewScreen(EnchantedBookAccess.fromItem(stack));
        var client = Minecraft.getInstance();
        client.setScreen(screen);
    }
}
