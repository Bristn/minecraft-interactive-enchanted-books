package net.bristn.lectern.screen;

import java.util.List;

import net.bristn.lectern.LecternEnchantedBooks;
import net.bristn.lectern.screen.data.LecternScreenPageData;
import net.bristn.lectern.screen.data.LecternScreenSupportedIconData;
import net.bristn.lectern.screen.handlers.LecternScreenHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class LecternEnchantedBookScreen extends Screen implements MenuAccess<LecternScreenHandler> {
    private static final int BACKGROUND_WIDTH = 272;
    private static final int PADDING_FROM_CENTER = 6;

    private static final int TEXT_LINE_WIDTH = BACKGROUND_WIDTH / 2 - PADDING_FROM_CENTER * 2 - 13;
    private static final int TEXT_LINE_HEIGHT = 10;
    private static final int LEFT_TEXT_OFFSET = BACKGROUND_WIDTH / 2 - PADDING_FROM_CENTER * 2 - 6;
    private static final int RIGHT_TEXT_OFFSET = PADDING_FROM_CENTER;
    private static final int TOP_TEXT_OFFSET = PADDING_FROM_CENTER + 10;

    private static final Identifier BOOK_LOCATION = Identifier.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID,
            "textures/gui/book.png");

    private final LecternScreenHandler menu;
    private final int totalPageCount;
    private PageButton forwardButton;
    private PageButton backButton;
    private int currentPage;

    public LecternEnchantedBookScreen(LecternScreenHandler menu, Inventory inventory, Component title) {
        super(GameNarrator.NO_TITLE);

        this.menu = menu;
        this.totalPageCount = menu.getPages().size();
        this.currentPage = 0;
    }

    @Override
    protected void init() {
        super.init();

        var borderPadding = 25;
        var buttonWidth = 24;

        var y = 159;
        var left = width / 2 - BACKGROUND_WIDTH / 2 + borderPadding;
        var right = width / 2 + BACKGROUND_WIDTH / 2 - borderPadding - buttonWidth;
        this.forwardButton = this.addRenderableWidget(new PageButton(right, y, true, button -> this.pageForward(), true));
        this.backButton = this.addRenderableWidget(new PageButton(left, y, false, button -> this.pageBack(), true));
        this.updateButtonVisibility();
    }

    public LecternScreenHandler getMenu() {
        return this.menu;
    }

    public void renderBackground(GuiGraphics graphics, int i, int j, float f) {
        this.renderTransparentBackground(graphics);

        graphics.blit(RenderPipelines.GUI_TEXTURED, BOOK_LOCATION, width / 2 - 256, 2, 0.0F, 0.0F, 512, 192, 512, 256);
    }

    @Override
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
        super.render(graphics, mouseX, mouseY, a);

        // Determine the current page data
        var pages = this.menu.getPages();
        var page = pages.get(this.currentPage);

        // Render the different interface areas
        renderLeftPage(graphics, mouseX, mouseY, page);
        renderRightPage(graphics, mouseX, mouseY, page);
    }

    /**
     * Renders the title and the description of the current page Returns the y
     * position below all texts. Used to dynamically draw the exclusive set
     * afterwards
     */
    private int renderLeftPage(GuiGraphics graphics, int mouseX, int mouseY, LecternScreenPageData page) {
        var x = this.width / 2 - LEFT_TEXT_OFFSET;
        var y = TOP_TEXT_OFFSET;

        var centerX = x + TEXT_LINE_WIDTH / 2;

        // Draw the title
        var collector = graphics.textRenderer(GuiGraphics.HoveredTextEffects.TOOLTIP_AND_CURSOR);
        y = renderTextLines(collector, centerX, y, page.title(), ChatFormatting.AQUA, TextAlignment.CENTER);
        y += TEXT_LINE_HEIGHT;

        // Draw the headers and texts
        var headers = page.leftHeaders();
        var texts = page.leftTexts();
        for (var i = 0; i < headers.size(); i++) {
            var header = headers.get(i);
            if (header.getString().isEmpty() == false) {
                y = renderTextLines(collector, centerX, y, header, ChatFormatting.GRAY, TextAlignment.CENTER);
            }

            var text = texts.get(i);
            y = renderTextLines(collector, x, y, text, ChatFormatting.BLACK, TextAlignment.LEFT);
            y += TEXT_LINE_HEIGHT;
        }

        // TODO: Render anvil cost ?
        return y;
    }

    /**
     * renders the supported items section of the interface. Includes the text hint,
     * the icons and tooltips for each icon
     */
    private void renderRightPage(GuiGraphics graphics, int mouseX, int mouseY, LecternScreenPageData page) {
        var x = this.width / 2 + RIGHT_TEXT_OFFSET;
        var y = TOP_TEXT_OFFSET;

        // Render the supported items hint
        var collector = graphics.textRenderer(GuiGraphics.HoveredTextEffects.TOOLTIP_AND_CURSOR);
        var title = Component.translatable("gui.lectern-enchanted-books.applicable").getString();
        y = renderTextLines(collector, x + TEXT_LINE_WIDTH / 2, y, title, ChatFormatting.GRAY, TextAlignment.CENTER);
        y += TEXT_LINE_HEIGHT / 2;

        var iconSize = 16;
        var iconPadding = 8;
        var iconsPerRow = 5;

        // Render the supported item icons
        var supported = page.supported();
        var icons = supported.icons().size();
        for (var i = 0; i < icons; i++) {
            var col = i % iconsPerRow;
            var row = (int) (Math.floor(i / iconsPerRow));

            var iconData = supported.icons().get(i);
            var iconX = x + iconSize * col + iconPadding * (col - 1) + iconSize / 2;
            var iconY = y + iconSize * row + iconPadding * (row - 1) + iconSize / 2;
            this.drawSupportedIcon(graphics, iconX, iconY, mouseX, mouseY, iconData);
        }

        // Render the tooltips after the icons to prevent layering issues
        for (var i = 0; i < icons; i++) {
            var col = i % iconsPerRow;
            var row = (int) (Math.floor(i / iconsPerRow));

            var iconData = supported.icons().get(i);
            var iconX = x + iconSize * col + iconPadding * (col - 1) + iconSize / 2;
            var iconY = y + iconSize * row + iconPadding * (row - 1) + iconSize / 2;
            this.drawSupportedIconTooltip(graphics, iconX, iconY, mouseX, mouseY, iconData);
        }
    }

    /**
     * Draws the given supported icon at the given position. Additionally adds a
     * tooltip when hovering the icon
     */
    private void drawSupportedIcon(GuiGraphics graphics, int x, int y, int mouseX, int mouseY,
            LecternScreenSupportedIconData data) {

        // Draw the icon itself
        var size = 16;
        var icon = data.texture();
        graphics.blit(RenderPipelines.GUI_TEXTURED, icon, x, y, 0.0F, 0.0F, size, size, size, size);
    }

    /**
     * Draws the tooltip when hovering over a given supported item icon
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

        var tooltip = new LecternScreenTooltipComponent(data.tooltipTitle(), data.tooltipItems());
        graphics.renderTooltip(font, List.of(tooltip), x, y, DefaultTooltipPositioner.INSTANCE, null);
    }

    /**
     * Renders the given text in multiple lines if it is too long. Returns the new
     * vertical position below the text.
     */
    private int renderTextLines(ActiveTextCollector collector, int x, int y, String text, ChatFormatting format,
            TextAlignment align) {

        var component = Component.literal(text).withStyle(format).withoutShadow();
        var lines = this.font.split(component, TEXT_LINE_WIDTH);
        for (var line : lines) {
            collector.accept(align, x, y, line);
            y += TEXT_LINE_HEIGHT;
        }

        return y;
    }

    private int renderTextLines(ActiveTextCollector collector, int x, int y, MutableComponent text, ChatFormatting format,
            TextAlignment align) {

        var component = text.withStyle(format).withoutShadow();
        var lines = this.font.split(component, TEXT_LINE_WIDTH);
        for (var line : lines) {
            collector.accept(align, x, y, line);
            y += TEXT_LINE_HEIGHT;
        }

        return y;
    }

    public boolean setPage(final int page) {
        int clampedPage = Mth.clamp(page, 0, this.totalPageCount - 1);
        if (clampedPage != this.currentPage) {
            this.currentPage = clampedPage;
            this.updateButtonVisibility();
            return true;
        }

        return false;
    }

    private void pageBack() {
        if (this.currentPage > 0) {
            this.currentPage--;
        }

        this.updateButtonVisibility();
    }

    private void pageForward() {
        if (this.currentPage < this.totalPageCount - 1) {
            this.currentPage++;
        }

        this.updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        this.forwardButton.visible = this.currentPage < this.totalPageCount - 1;
        this.backButton.visible = this.currentPage > 0;
    }

    @Override
    public boolean keyPressed(final KeyEvent event) {
        if (super.keyPressed(event)) {
            return true;
        } else {
            return switch (event.key()) {
            case 266 -> {
                this.backButton.onPress(event);
                yield true;
            }
            case 267 -> {
                this.forwardButton.onPress(event);
                yield true;
            }
            default -> false;
            };
        }
    }
}
