package net.bristn.lectern.mixin;

import net.bristn.lectern.LecternEnchantedBooks;
import net.minecraft.world.level.block.LecternBlock;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LecternBlock.class)
public class LecternBlockMixin {
    private static final Logger LOGGER = LecternEnchantedBooks.LOGGER;

    // @Inject(method = "useItemOn", at = @At(value = "INVOKE", target =
    // "Lnet/minecraft/world/level/block/LecternBlock;tryPlaceBook(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/item/ItemStack;)Z",
    // shift = At.Shift.BEFORE))
    // private void applyTagChange(ItemStack stack, BlockState state, Level world,
    // BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit,
    // CallbackInfoReturnable<InteractionResult> cir) {

    // if (stack.is(Items.ENCHANTED_BOOK) == false) {
    // return;
    // }

    // var blockEntity = world.getBlockEntity(pos);
    // if (blockEntity == null) {
    // return;
    // }

    // if (blockEntity instanceof LecternBlockEntity == false) {
    // return;
    // }

    // var lectern = (LecternBlockEntity) blockEntity;
    // lectern.setBook(stack);
    // }

    // @Inject(method = "isBook", at = @At("HEAD"), cancellable = true)
    // private static void allowEnchantedBook(ItemStack stack,
    // CallbackInfoReturnable<Boolean> cir) {
    // if (stack.is(Items.ENCHANTED_BOOK)) {
    // LOGGER.info("Allow placing the enchanted book");
    // cir.setReturnValue(true);
    // }
    // }
}