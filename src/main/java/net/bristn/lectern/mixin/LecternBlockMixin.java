package net.bristn.lectern.mixin;

import net.bristn.lectern.LecternAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LecternBlock.class)
public class LecternBlockMixin {

    // TODO: Enchanted books not placeable even though the data should be correctly implemented
    // ! Data is not properly applied
    // TODO: Might be because of installation errors (also missing all sounds & menu background)

    // @Inject(method = "useItemOn", at = @At(value = "INVOKE", target =
    // "Lnet/minecraft/world/level/block/LecternBlock;tryPlaceBook(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/item/ItemStack;)Z",
    // shift = At.Shift.BEFORE))
    // private void applyTagChange(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit,
    // CallbackInfoReturnable<InteractionResult> cir) {

    // boolean isEnchantedBook = stack.is(Items.ENCHANTED_BOOK);
    // boolean isBlockEntity = world.getBlockEntity(pos) != null;
    // if (isEnchantedBook && isBlockEntity) {
    // LecternAccess lecternEntity = (LecternAccess) world.getBlockEntity(pos);
    // lecternEntity.setIsTomeReaderLectern(true);
    // }
    // }
}