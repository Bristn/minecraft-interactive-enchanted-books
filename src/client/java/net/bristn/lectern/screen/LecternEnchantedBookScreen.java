package net.bristn.lectern.screen;

import net.bristn.lectern.BookModelHelper;
import net.bristn.lectern.screen.handlers.LecternScreenHandler;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.component.WrittenBookContent;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Shadow;

public class LecternEnchantedBookScreen extends Screen implements MenuAccess<LecternScreenHandler> {
    private final LecternScreenHandler menu;
    // private final ContainerListener listener = new 1(this);

    public static final ResourceLocation BOOK_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/book.png");

    public LecternEnchantedBookScreen(LecternScreenHandler handler, Inventory inventory, Component title) {
        super(GameNarrator.NO_TITLE);
        this.menu = handler;
    }

    public LecternScreenHandler getMenu() {
        return this.menu;
    }

    // protected void init() {
    // super.init();
    // // this.menu.addSlotListener(this.listener);

    // ! See BookViewScreen for implementation of menu
    // TODO: Use BookViewScreen to draw two pages for each enchantment?
    // - Name, description, supported items, exclude set

    // ! Adds pages to the lectern menu (Changing page does not seem to work)
    // List<Component> pages = new ArrayList<>();
    // pages.add(Component.literal("Test String"));
    // pages.add(Component.literal("Test String"));
    // pages.add(Component.literal("Test String"));
    // setBookAccess(new BookAccess(pages));
    // }

    // public void onClose() {
    // this.minecraft.player.closeContainer();
    // super.onClose();
    // }

    // public void removed() {
    // super.removed();
    // // this.menu.removeSlotListener(this.listener);
    // }

    // protected void createMenuControls() {
    // if (this.minecraft.player.mayBuild()) {
    // this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose()).bounds(this.width / 2 - 100, 196, 98, 20).build());
    // this.addRenderableWidget(
    // Button.builder(Component.translatable("lectern.take_book"), (button) -> this.sendButtonClick(3)).bounds(this.width / 2 + 2, 196, 98, 20).build());
    // } else {
    // super.createMenuControls();
    // }

    // }

    // protected void pageBack() {
    // this.sendButtonClick(1);
    // }

    // protected void pageForward() {
    // this.sendButtonClick(2);
    // }

    // protected boolean forcePage(int i) {
    // // if (i != this.menu.getPage()) {
    // // this.sendButtonClick(100 + i);
    // // return true;
    // // } else {
    // // return false;
    // // }
    // return true;
    // }

    // private void sendButtonClick(int i) {
    // this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, i);
    // }

    // public boolean isPauseScreen() {
    // return false;
    // }

    // void bookChanged() {
    // // ItemStack itemStack = this.menu.getBook();
    // // this.setBookAccess((BookViewScreen.BookAccess) Objects.requireNonNullElse(BookAccess.fromItem(itemStack), BookViewScreen.EMPTY_ACCESS));
    // }

    // void pageChanged() {
    // // this.setPage(this.menu.getPage());
    // }

    // protected void closeScreen() {
    // this.minecraft.player.closeContainer();
    // }

    // ! -------------------------------------

    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        this.renderTransparentBackground(guiGraphics);

        // TODO: Check if one page is enough, or two pages
        // No matter, a custom renderer is needed for icons

        var imageWidth = 192;
        var imageHeight = 192;
        guiGraphics.blit(BOOK_LOCATION, (this.width - imageWidth) / 2 - imageWidth / 2, 2, 0, 0, imageWidth, imageHeight);

        guiGraphics.blit(BOOK_LOCATION, (this.width - imageWidth) / 2 + imageWidth / 2, 2, 0, 0, imageWidth, imageHeight);
    }
}
