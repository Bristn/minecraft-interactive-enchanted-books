package net.bristn.lectern.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.LecternRenderer;
import net.minecraft.client.renderer.blockentity.state.LecternRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

@Debug(export = true)
@Mixin(LecternRenderer.class)
public abstract class LecternRendererMixin implements BlockEntityRenderer<LecternBlockEntity, LecternRenderState> {

    private static BookModel regularBook = new BookModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BOOK));

    // @Inject(method =
    // "render(Lnet/minecraft/world/level/block/entity/LecternBlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V",
    // at = @At("HEAD"), cancellable = true)
    // private void render(LecternBlockEntity lectern, float f, PoseStack poseStack,
    // MultiBufferSource vertexProvider, int i, int j, CallbackInfo originalMethod)
    // {

    // // Use the regular renderer if the lectern does not contain an enchanted book
    // ItemStack stack = lectern.getBook();
    // if (stack.getItem() != Items.ENCHANTED_BOOK) {
    // return;
    // }

    // renderBook(lectern, poseStack, vertexProvider, i, j);

    // // Cancel the original rendering method for the book
    // originalMethod.cancel();
    // }

    @Inject(method = "submit", at = @At("HEAD"), cancellable = true)
    private void submit(final LecternRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector,
            final CameraRenderState camera, CallbackInfo originalMethod) {

    }

    private void renderBook(LecternBlockEntity lectern, PoseStack poseStack, MultiBufferSource vertexProvider, int i, int j) {

        // Default rotation code for the lectern book
        BlockState blockState = lectern.getBlockState();
        float g = blockState.getValue(LecternBlock.FACING).getClockWise().toYRot();
        poseStack.pushPose();
        poseStack.translate(0.5F, 1.0625F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-g));
        poseStack.mulPose(Axis.ZP.rotationDegrees(67.5F));
        poseStack.translate(0.0F, -0.125F, 0.0F);

        // Set the page flip
        // regularBook.setupAnim(0.0F, 0.1F, 0.9F, 1.2F);

        // VertexConsumer vertexConsumer = BOOK_LOCATION.buffer(vertexProvider,
        // RenderType::entitySolid);
        // regularBook.render(poseStack, vertexConsumer, i, j, -1);
        // poseStack.popPose();
    }
}