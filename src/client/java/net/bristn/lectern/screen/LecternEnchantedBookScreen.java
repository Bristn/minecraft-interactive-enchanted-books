package net.bristn.lectern.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.bristn.lectern.LecternEnchantedBooks;
import net.bristn.lectern.screen.handlers.LecternScreenHandler;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class LecternEnchantedBookScreen extends Screen implements MenuAccess<LecternScreenHandler> {
    private final LecternScreenHandler menu;
    // private final ContainerListener listener = new 1(this);

    public static final Identifier BOOK_LOCATION = Identifier.withDefaultNamespace("textures/gui/book.png");

    // TODO: slot directory contains icons for the different tools & armor, but this
    // ResourceLocation is not correct
    // TODO: Check if supported group of enchantment has some data to tell which
    // slots are correct and get their icons
    public static final Identifier PICKAXE_LOCATION = Identifier.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID, "textures/gui/test.png");
    public static final Identifier test = Identifier.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID, "textures/gui/sword.png");

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
    // this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button)
    // -> this.onClose()).bounds(this.width / 2 - 100, 196, 98, 20).build());
    // this.addRenderableWidget(
    // Button.builder(Component.translatable("lectern.take_book"), (button) ->
    // this.sendButtonClick(3)).bounds(this.width / 2 + 2, 196, 98, 20).build());
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
    // // this.setBookAccess((BookViewScreen.BookAccess)
    // Objects.requireNonNullElse(BookAccess.fromItem(itemStack),
    // BookViewScreen.EMPTY_ACCESS));
    // }

    // void pageChanged() {
    // // this.setPage(this.menu.getPage());
    // }

    // protected void closeScreen() {
    // this.minecraft.player.closeContainer();
    // }

    // ! -------------------------------------

    public void renderBackground(GuiGraphics graphics, int i, int j, float f) {
        this.renderTransparentBackground(graphics);

        // TODO: Check if one page is enough, or two pages
        // No matter, a custom renderer is needed for icons

        var AXE_SLOT = Identifier.withDefaultNamespace("container/slot/axe");

        // var sprite = Minecraft.getInstance().getGuiSprites().getSprite(AXE_SLOT);

        // var imageWidth = 192;
        // var imageHeight = 192;
        // guiGraphics.blit(BOOK_LOCATION, (this.width - imageWidth) / 2, 2, 0, 0,
        // imageWidth, imageHeight);

        // guiGraphics.blit(PICKAXE_LOCATION, (this.width - 8) / 2 + 8 / 2, 2, 0, 0, 8,
        // 8);
        // guiGraphics.blit(BOOK_LOCATION, this.width / 2, this.height / 2, 0, 0, 192,
        // 192);

        // TODO: Icon does not render due traansparency
        graphics.blit(RenderPipelines.GUI_TEXTURED, BOOK_LOCATION, this.width / 2, this.height / 2, 0.0F, 0.0F, 32, 32, 32, 32);

        // guiGraphics.blit(test, this.width / 2, this.height / 2, 0, 0, 32, 32);

        // graphics.blit(RenderPipelines.GUI_TEXTURED, texture2, 90, 190, u, v, 14, 14,
        // regionWidth, regionHeight, 256, 256);
    }
}
