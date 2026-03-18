package net.bristn.lectern.screen;

import java.util.List;

import org.slf4j.Logger;

import net.bristn.lectern.LecternEnchantedBooks;
import net.bristn.lectern.screen.data.LecternScreenSupportedData;
import net.bristn.lectern.screen.data.LecternScreenSupportedIconData;
import net.bristn.lectern.screen.handlers.LecternScreenHandler;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class LecternEnchantedBookScreen extends Screen implements MenuAccess<LecternScreenHandler> {
    private static final Logger LOGGER = LecternEnchantedBooks.LOGGER;
    private static final Identifier BOOK_LOCATION = Identifier.withDefaultNamespace("textures/gui/book.png");

    private final LecternScreenHandler menu;

    public LecternEnchantedBookScreen(LecternScreenHandler menu, Inventory inventory, Component title) {
        super(GameNarrator.NO_TITLE);

        this.menu = menu;
    }

    public LecternScreenHandler getMenu() {
        return this.menu;
    }

    // TODO: Render the page data

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

        var pages = this.menu.getPages();
        var pageIndex = this.menu.getPage();
        var page = pages.get(pageIndex);

        //
        var supported = page.supported();
        renderSupportedItems(graphics, mouseX, mouseY, supported);
    }

    /**
     * 
     * @param graphics
     * @param mouseX
     * @param mouseY
     * @param supported
     */
    private void renderSupportedItems(GuiGraphics graphics, int mouseX, int mouseY,
            LecternScreenSupportedData supported) {
        var iconX = this.width / 2;
        var iconY = this.height / 2;

        var iconSize = 16;
        var iconPadding = 8;

        var iconsPerRow = 5;

        var icons = supported.icons().size();
        for (var i = 0; i < icons; i++) {
            var col = i % iconsPerRow;
            var row = (int) (Math.floor(i / iconsPerRow));

            var iconData = supported.icons().get(i);
            var x = iconX + iconSize * col + iconPadding * (col - 1);
            var y = iconY + iconSize * row + iconPadding * (row - 1);
            this.drawSupportedIcon(graphics, x, y, mouseX, mouseY, iconData);
        }

        // Draw the tooltips after the icons to prevent layering issues
        for (var i = 0; i < icons; i++) {
            var col = i % iconsPerRow;
            var row = (int) (Math.floor(i / iconsPerRow));

            var iconData = supported.icons().get(i);
            var x = iconX + iconSize * col + iconPadding * (col - 1);
            var y = iconY + iconSize * row + iconPadding * (row - 1);
            this.drawSupportedIconTooltip(graphics, x, y, mouseX, mouseY, iconData);
        }
    }

    /**
     * Draws the given supported icon at the given position. Additionally adds a
     * tooltip when hovering the icon
     * 
     * @param graphics
     * @param x
     * @param y
     * @param mouseX
     * @param mouseY
     * @param data
     */
    private void drawSupportedIcon(GuiGraphics graphics, int x, int y, int mouseX, int mouseY,
            LecternScreenSupportedIconData data) {

        // Draw the icon itself
        var size = 16;
        var icon = data.texture();
        graphics.blit(RenderPipelines.GUI_TEXTURED, icon, x, y, 0.0F, 0.0F, size, size, size, size);
    }

    /**
     * 
     * 
     * @param graphics
     * @param x
     * @param y
     * @param mouseX
     * @param mouseY
     * @param data
     */
    private void drawSupportedIconTooltip(GuiGraphics graphics, int x, int y, int mouseX, int mouseY,
            LecternScreenSupportedIconData data) {

        // Draw the icon itself
        var size = 16;

        // Draw the tooltip if the mouse is hovering above
        var mouseInX = mouseX > x && mouseX < (x + size);
        var mouseInY = mouseY > y && mouseY < (y + size);
        if (mouseInX == false || mouseInY == false) {
            return;
        }

        var tooltip = new LecternScreenTooltipComponent(data.tooltipTitle(), data.tooltipIcons());
        graphics.renderTooltip(font, List.of(tooltip), x, y, DefaultTooltipPositioner.INSTANCE, null);
    }

    private int getBackgroundLeft() {
        return (this.width - 192) / 2;
    }

    private int getBackgroundTop() {
        return 2;
    }
}
