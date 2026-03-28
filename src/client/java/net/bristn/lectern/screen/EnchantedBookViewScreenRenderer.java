package net.bristn.lectern.screen;

import java.util.List;

import net.bristn.lectern.LecternEnchantedBooks;
import net.bristn.lectern.screen.data.LecternScreenPageData;
import net.bristn.lectern.screen.data.LecternScreenSupportedIconData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

public class EnchantedBookViewScreenRenderer {
    public static final int BACKGROUND_WIDTH = 272;
    private static final int PADDING_FROM_CENTER = 6;
    private static final int ICON_SIZE = 16;
    private static final int ICONS_PER_ROW = 5;

    private static final int TEXT_LINE_WIDTH = BACKGROUND_WIDTH / 2 - PADDING_FROM_CENTER * 2 - 13;
    private static final int TEXT_LINE_HEIGHT = 10;
    private static final int LEFT_TEXT_OFFSET = BACKGROUND_WIDTH / 2 - PADDING_FROM_CENTER * 2 - 6;
    private static final int RIGHT_TEXT_OFFSET = PADDING_FROM_CENTER;
    private static final int TOP_TEXT_OFFSET = PADDING_FROM_CENTER + 10;

    private static final Identifier BOOK_LOCATION = Identifier.fromNamespaceAndPath(LecternEnchantedBooks.MOD_ID,
            "textures/gui/book.png");

    private final Screen screen;

    public EnchantedBookViewScreenRenderer(Screen screen) {
        this.screen = screen;
    }

    /**
     * Public interface to this renderer. Handles rendering the static background of
     * the menu
     */
    public void renderBackground(final GuiGraphicsExtractor graphics) {
        if (this.screen == null) {
            return;
        }

        graphics.blit(RenderPipelines.GUI_TEXTURED, BOOK_LOCATION, screen.width / 2 - 256, 2, 0.0F, 0.0F, 512, 192, 512, 256);
    }

    /**
     * Public interface to this renderer. Handles rendering all foreground elements,
     * including the displayed texts, icons and tooltips
     */
    public void renderForeground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY,
            final LecternScreenPageData page, final int currentPage, final int totalPages) {

        if (this.screen == null) {
            return;
        }

        renderLeftPage(graphics, mouseX, mouseY, page);
        renderRightPage(graphics, mouseX, mouseY, page, currentPage, totalPages);
    }

    /**
     * Renders the title and the description of the current page Returns the y
     * position below all texts. Used to dynamically draw the exclusive set
     * afterwards
     */
    private int renderLeftPage(GuiGraphicsExtractor graphics, int mouseX, int mouseY, LecternScreenPageData page) {
        var x = screen.width / 2 - LEFT_TEXT_OFFSET;
        var y = TOP_TEXT_OFFSET;

        var centerX = x + TEXT_LINE_WIDTH / 2;

        // Draw the title
        var collector = graphics.textRenderer(GuiGraphicsExtractor.HoveredTextEffects.TOOLTIP_AND_CURSOR);
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
    private void renderRightPage(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY,
            final LecternScreenPageData page, final int currentPage, final int totalPages) {

        var x = screen.width / 2 + RIGHT_TEXT_OFFSET;
        var y = TOP_TEXT_OFFSET;

        // Render the page number in the top right
        var collector = graphics.textRenderer(GuiGraphicsExtractor.HoveredTextEffects.TOOLTIP_AND_CURSOR);
        var pageMessage = getPageNumberMessage(currentPage, totalPages);
        var pageMessageWidth = screen.width / 2 + BACKGROUND_WIDTH / 2 - 18;
        var pageMessageComp = Component.literal(pageMessage);
        y = renderTextLines(collector, pageMessageWidth, y, pageMessageComp, ChatFormatting.BLACK, TextAlignment.RIGHT);
        y += TEXT_LINE_HEIGHT / 2;

        // Render the supported items hint
        var title = Component.translatable("gui.lectern-enchanted-books.applicable").getString();
        var titleCom = Component.literal(title);
        y = renderTextLines(collector, x + TEXT_LINE_WIDTH / 2, y, titleCom, ChatFormatting.GRAY, TextAlignment.CENTER);

        var iconPadding = 8;

        // Render the supported item icons
        var supported = page.supported();
        var icons = supported.icons().size();
        for (var i = 0; i < icons; i++) {
            var col = i % ICONS_PER_ROW;
            var row = (int) (Math.floor(i / ICONS_PER_ROW));

            var iconData = supported.icons().get(i);
            var iconX = x + ICON_SIZE * col + iconPadding * (col - 1) + ICON_SIZE / 2;
            var iconY = y + ICON_SIZE * row + iconPadding * (row - 1) + ICON_SIZE / 2;
            this.renderSupportedIcon(graphics, iconX, iconY, mouseX, mouseY, iconData);
        }

        // Render the tooltips after the icons to prevent layering issues
        for (var i = 0; i < icons; i++) {
            var col = i % ICONS_PER_ROW;
            var row = (int) (Math.floor(i / ICONS_PER_ROW));

            var iconData = supported.icons().get(i);
            var iconX = x + ICON_SIZE * col + iconPadding * (col - 1) + ICON_SIZE / 2;
            var iconY = y + ICON_SIZE * row + iconPadding * (row - 1) + ICON_SIZE / 2;
            this.renderSupportedIconTooltip(graphics, iconX, iconY, mouseX, mouseY, iconData);
        }
    }

    /**
     * Draws the given supported icon at the given position. Additionally adds a
     * tooltip when hovering the icon
     */
    private void renderSupportedIcon(GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY,
            LecternScreenSupportedIconData data) {

        // Draw the icon itself
        var icon = data.texture();
        graphics.blit(RenderPipelines.GUI_TEXTURED, icon, x, y, 0.0F, 0.0F, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
    }

    /**
     * Draws the tooltip when hovering over a given supported item icon
     */
    private void renderSupportedIconTooltip(GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY,
            LecternScreenSupportedIconData data) {

        // Draw the tooltip if the mouse is hovering above
        var mouseInX = mouseX > x && mouseX < (x + ICON_SIZE);
        var mouseInY = mouseY > y && mouseY < (y + ICON_SIZE);
        if (mouseInX == false || mouseInY == false) {
            return;
        }

        var tooltip = new LecternScreenTooltipComponent(data.tooltipTitle(), data.tooltipItems());
        graphics.tooltip(screen.getFont(), List.of(tooltip), x, y, DefaultTooltipPositioner.INSTANCE, null);
    }

    /**
     * Renders the given text in multiple lines if it is too long. Returns the new
     * vertical position below the text.
     */
    private int renderTextLines(ActiveTextCollector collector, int x, int y, MutableComponent text, ChatFormatting format,
            TextAlignment align) {

        var component = text.withStyle(format).withoutShadow();
        var lines = screen.getFont().split(component, TEXT_LINE_WIDTH);
        for (var line : lines) {
            collector.accept(align, x, y, line);
            y += TEXT_LINE_HEIGHT;
        }

        return y;
    }

    /**
     * Determines the page number indicator of the book
     */
    private String getPageNumberMessage(int currentPage, int totalPages) {
        var parameters = new Object[] { currentPage + 1, Math.max(totalPages, 1) };
        return Component.translatable("book.pageIndicator", parameters).getString();
    }
}
