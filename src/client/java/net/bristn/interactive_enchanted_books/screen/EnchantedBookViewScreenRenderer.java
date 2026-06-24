package net.bristn.interactive_enchanted_books.screen;

import java.util.List;
import java.util.Map;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.bristn.interactive_enchanted_books.mixin.MinecraftAccessor;
import net.bristn.interactive_enchanted_books.mixin.ParticleEngineAccessor;
import net.bristn.interactive_enchanted_books.mixin.ParticleResourcesAccessor;
import net.bristn.interactive_enchanted_books.screen.data.LecternScreenPageData;
import net.bristn.interactive_enchanted_books.screen.data.LecternScreenSupportedIconData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

/**
 * A separate class for rendering the menu page. This reduces the complexity of the screen class
 * itself why keeping all rendering logic in one place
 */
public class EnchantedBookViewScreenRenderer {
    public static final int BACKGROUND_WIDTH = 272;
    private static final int PADDING_FROM_CENTER = 6;
    private static final int ICON_SIZE = 16;
    private static final int ICONS_PER_ROW = 5;
    private static final int ICON_PADDING = 8;
    private static final int ICON_PADDING_HALF = ICON_PADDING / 2;

    private static final int TEXT_LINE_WIDTH = BACKGROUND_WIDTH / 2 - PADDING_FROM_CENTER * 2 - 13;
    private static final int TEXT_LINE_HEIGHT = 10;
    private static final int LEFT_TEXT_OFFSET = BACKGROUND_WIDTH / 2 - PADDING_FROM_CENTER * 2 - 6;
    private static final int RIGHT_TEXT_OFFSET = PADDING_FROM_CENTER;
    private static final int TOP_TEXT_OFFSET = PADDING_FROM_CENTER + 10;

    public static final int INFO_BUTTON_X_FROM_MIDDLE = BACKGROUND_WIDTH / 2 + Button.DEFAULT_SPACING;
    public static final int INFO_BUTTON_Y = 8;

    private static final Identifier BOOK_LOCATION = Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID,
            "textures/gui/book.png");

    private static final Identifier ECHO_BOOK_LOCATION = Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID,
            "textures/gui/echo_book.png");

    private final Screen screen;
    private final Map<Identifier, SpriteSet> particleSpriteSets;

    public static final int INFO_PANEL_WIDTH = 48;

    public EnchantedBookViewScreenRenderer(Screen screen) {
        this.screen = screen;

        var minecraft = (MinecraftAccessor) Minecraft.getInstance();
        var engine = (ParticleEngineAccessor) minecraft.getParticleEngine();
        var resources = (ParticleResourcesAccessor) engine.getParticleResources();
        this.particleSpriteSets = resources.getSpriteSets();
    }

    /**
     * Public interface to this renderer. Handles rendering the static background of the menu
     */
    public void renderBackground(GuiGraphicsExtractor graphics, LecternScreenPageData page) {
        if (this.screen == null) {
            return;
        }

        var texture = page.isEcho() ? ECHO_BOOK_LOCATION : BOOK_LOCATION;
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, screen.width / 2 - 256, 2, 0.0F, 0.0F, 512, 192, 512, 256);
    }

    /**
     * Public interface to this renderer. Handles rendering all foreground elements, including the
     * displayed texts, icons and tooltips
     */
    public void renderForeground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, LecternScreenPageData page,
            int currentPage, int totalPages) {

        if (this.screen == null) {
            return;
        }

        renderLeftPage(graphics, mouseX, mouseY, page);
        renderRightPage(graphics, mouseX, mouseY, page, currentPage, totalPages);
    }

    /**
     * Renders the title and the description of the current page Returns the y position below all texts.
     * Used to dynamically draw the exclusive set afterwards
     */
    private int renderLeftPage(GuiGraphicsExtractor graphics, int mouseX, int mouseY, LecternScreenPageData page) {
        var x = screen.width / 2 - LEFT_TEXT_OFFSET;
        var y = TOP_TEXT_OFFSET;

        var centerX = x + TEXT_LINE_WIDTH / 2;
        var iconSize = 16;

        var particleId = page.particleId();
        if (CommonModInitializer.isInstalledOnServer == false) {
            particleId = null;
        }

        // Draw the title
        var collector = graphics.textRenderer(GuiGraphicsExtractor.HoveredTextEffects.TOOLTIP_AND_CURSOR);
        var titleWidth = TEXT_LINE_WIDTH - (iconSize + 4);
        var titlePos = particleId == null ? centerX : centerX - (iconSize - 4) / 2;
        y = renderTextLines(collector, titlePos, y, page.title(), ChatFormatting.AQUA, TextAlignment.CENTER, titleWidth);
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

        if (particleId == null) {
            return y;
        }

        // Draw the particle texture
        var particleX = screen.width / 2 - 5 - iconSize;
        var particleY = TOP_TEXT_OFFSET - 4;
        var tooltip = Component.translatable("gui.interactive_enchanted_books.particle");
        drawParticleTexture(graphics, particleX, particleY, particleId, tooltip, mouseX, mouseY);
        return y;
    }

    /**
     * Helper method to draw the particle texture at any point in the gut. Allows showing a tooltip when
     * hovering
     */
    private void drawParticleTexture(GuiGraphicsExtractor graphics, int x, int y, Identifier particleId, Component tooltip,
            int mouseX, int mouseY) {
        var iconSize = 16;

        // Draw the particle texture
        var spriteSet = particleSpriteSets.get(particleId);
        var sprite = spriteSet.first();
        graphics.blit(sprite.atlasLocation(), //
                x, y, // Top left position
                x + iconSize, y + iconSize, // Bottom right position
                sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());

        if (tooltip == null) {
            return;
        }

        if (mouseX > x && mouseX < x + iconSize) {
            if (mouseY > y && mouseY < y + iconSize) {
                graphics.setTooltipForNextFrame(tooltip, mouseX, mouseY);
            }
        }
    }

    /**
     * renders the supported items section of the interface. Includes the text hint, the icons and
     * tooltips for each icon
     */
    private void renderRightPage(GuiGraphicsExtractor graphics, int mouseX, int mouseY, LecternScreenPageData page,
            int currentPage, int totalPages) {

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
        var title = Component.translatable("gui.interactive_enchanted_books.applicable").getString();
        var titleCom = Component.literal(title);
        y = renderTextLines(collector, x + TEXT_LINE_WIDTH / 2, y, titleCom, ChatFormatting.GRAY, TextAlignment.CENTER);

        // Render the supported item icons
        var supported = page.supported();
        var icons = supported.icons().size();
        for (var i = 0; i < icons; i++) {
            var col = i % ICONS_PER_ROW;
            var row = (int) (Math.floor(i / ICONS_PER_ROW));

            var iconData = supported.icons().get(i);
            var iconX = x + ICON_SIZE * col + ICON_PADDING * (col - 1) + ICON_SIZE / 2;
            var iconY = y + ICON_SIZE * row + ICON_PADDING * (row - 1) + ICON_SIZE / 2;
            this.renderSupportedIcon(graphics, iconX, iconY, mouseX, mouseY, iconData);
        }

        // Render the tooltips after the icons to prevent layering issues
        for (var i = 0; i < icons; i++) {
            var col = i % ICONS_PER_ROW;
            var row = (int) (Math.floor(i / ICONS_PER_ROW));

            var iconData = supported.icons().get(i);
            var iconX = x + ICON_SIZE * col + ICON_PADDING * (col - 1) + ICON_SIZE / 2;
            var iconY = y + ICON_SIZE * row + ICON_PADDING * (row - 1) + ICON_SIZE / 2;
            var hasRendered = this.renderSupportedIconTooltip(graphics, iconX, iconY, mouseX, mouseY, iconData);

            // Only render one tooltip at a time. Additionally improves performance by not
            // checking the other icons for hits
            if (hasRendered == true) {
                break;
            }
        }

        // Hide the comparator element if the signal is 0 (Happens if the mod is not
        // installed on the server)
        if (page.redstoneSignal().getString().isEmpty() || CommonModInitializer.isInstalledOnServer == false) {
            return;
        }

        // Show the redstone comparator signal of the page
        var iconSize = 16;
        var redstoneX = screen.width / 2 + 2;
        var redstoneY = TOP_TEXT_OFFSET - 4;
        graphics.item(Items.COMPARATOR.getDefaultInstance(), redstoneX, redstoneY);
        renderTextLines(collector, redstoneX + iconSize + 2, redstoneY + 4, page.redstoneSignal(), ChatFormatting.BLACK,
                TextAlignment.LEFT);

        // Show the comparator tooltip
        if (mouseX > redstoneX && mouseX < redstoneX + iconSize) {
            if (mouseY > redstoneY && mouseY < redstoneY + iconSize) {
                var tooltip = Component.translatable("gui.interactive_enchanted_books.signal");
                graphics.setTooltipForNextFrame(tooltip, mouseX, mouseY);
            }
        }
    }

    /**
     * Draws the given supported icon at the given position. Additionally adds a tooltip when hovering
     * the icon
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
    private boolean renderSupportedIconTooltip(GuiGraphicsExtractor graphics, int x, int y, int mouseX, int mouseY,
            LecternScreenSupportedIconData data) {

        // Draw the tooltip if the mouse is hovering above
        var mouseInX = mouseX >= (x - ICON_PADDING_HALF) && mouseX <= (x + ICON_SIZE + ICON_PADDING_HALF);
        var mouseInY = mouseY >= (y - ICON_PADDING_HALF) && mouseY <= (y + ICON_SIZE + ICON_PADDING_HALF);
        if (mouseInX == false || mouseInY == false) {
            return false;
        }

        var tooltip = new LecternScreenTooltipComponent(data.title(), data.tooltipItems());
        graphics.tooltip(screen.getFont(), List.of(tooltip), mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
        return true;
    }

    /**
     * Renders the given text in multiple lines if it is too long. Returns the new vertical position
     * below the text.
     */
    private int renderTextLines(ActiveTextCollector collector, int x, int y, MutableComponent text, ChatFormatting format,
            TextAlignment align, int lineWidth) {

        var component = text.withStyle(format).withoutShadow();
        var lines = screen.getFont().split(component, lineWidth);
        for (var line : lines) {
            collector.accept(align, x, y, line);
            y += TEXT_LINE_HEIGHT;
        }

        return y;
    }

    private int renderTextLines(ActiveTextCollector collector, int x, int y, MutableComponent text, ChatFormatting format,
            TextAlignment align) {

        return renderTextLines(collector, x, y, text, format, align, TEXT_LINE_WIDTH);
    }

    /**
     * Determines the page number indicator of the book
     */
    private String getPageNumberMessage(int currentPage, int totalPages) {
        var parameters = new Object[] { currentPage + 1, Math.max(totalPages, 1) };
        return Component.translatable("book.pageIndicator", parameters).getString();
    }

}
