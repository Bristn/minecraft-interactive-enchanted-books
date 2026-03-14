package net.bristn.lectern.screen;

import java.util.List;

import org.slf4j.Logger;

import com.mojang.blaze3d.systems.RenderSystem;

import net.bristn.lectern.LecternEnchantedBooks;
import net.bristn.lectern.screen.handlers.LecternScreenHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class LecternEnchantedBookScreen extends Screen implements MenuAccess<LecternScreenHandler> {
    private static final Logger LOGGER = LecternEnchantedBooks.LOGGER;
    private static final Identifier BOOK_LOCATION = Identifier.withDefaultNamespace("textures/gui/book.png");

    private final LecternScreenHandler menu;

    // TODO: slot directory contains icons for the different tools & armor, but this
    // ResourceLocation is not correct
    // TODO: Check if supported group of enchantment has some data to tell which
    // slots are correct and get their icons
    public static final Identifier PICKAXE_LOCATION = Identifier.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID,
            "textures/gui/test.png");

    public LecternEnchantedBookScreen(LecternScreenHandler handler, Inventory inventory, Component title) {
        super(GameNarrator.NO_TITLE);

        this.menu = handler;
        // this.book = handler.getBook();

        // var test = handler.getBook();
        // LOGGER.info("----- Screen book " + this.book.toString());
        // LOGGER.info("----- Screen book " + test.toString());
    }

    public LecternScreenHandler getMenu() {
        return this.menu;
    }

    // ! -------------------------------------

    public void renderBackground(GuiGraphics graphics, int i, int j, float f) {
        this.renderTransparentBackground(graphics);

        int left = this.getBackgroundLeft();
        int top = this.getBackgroundTop();
        graphics.blit(RenderPipelines.GUI_TEXTURED, BOOK_LOCATION, left, top,
                0.0F,
                0.0F, 192, 192, 256, 256);

    }

    @Override
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
        super.render(graphics, mouseX, mouseY, a);

        int left = this.getBackgroundLeft();
        int top = this.getBackgroundTop();

        var collector = graphics.textRenderer(GuiGraphics.HoveredTextEffects.TOOLTIP_AND_CURSOR);
        collector.accept(TextAlignment.RIGHT, left + 148, top + 16, Component.literal("Test content"));

        // TODO: Check if there are data tags defining the different lists of supported
        // items
        // -> Use data to get the icons of every supported item

        var iconX = this.width / 2;
        var iconY = this.height / 2;

        // TODO: Draw box around the icons
        this.drawSupportedIcon(graphics, iconX, iconY);

    }

    private void drawSupportedIcon(final GuiGraphics graphics, int x, int y) {
        // var border = Identifier.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID,
        // "textures/gui/border.png");
        // graphics.blit(RenderPipelines.GUI_TEXTURED, border, x - 4, y - 4, 0.0F, 0.0F,
        // 24, 24, 24, 24);

        var icon = Identifier.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID, "textures/gui/sword.png");
        graphics.blit(RenderPipelines.GUI_TEXTURED, icon, x, y, 0.0F, 0.0F, 16, 16, 16, 16);

        // TODO: Draw the tooltip if the mouse is over the image (manually compare pos)
        if (x > 500) {
            List<Component> tooltip = List.of(
                    Component.literal("My Image").withStyle(ChatFormatting.GOLD),
                    Component.literal("This is a description!").withStyle(ChatFormatting.GRAY));
            graphics.setComponentTooltipForNextFrame(font, tooltip, x, y);
        }

    }

    // ----- ----- ----- Getters

    private int getBackgroundLeft() {
        return (this.width - 192) / 2;
    }

    private int getBackgroundTop() {
        return 2;
    }
}
