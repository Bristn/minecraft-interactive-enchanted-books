package net.bristn.lectern.screen;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;

public class EnchantedBookViewScreen extends Screen {
    private static final Component TITLE = Component.translatable("book.view.title");
    private static final Style PAGE_TEXT_STYLE = Style.EMPTY.withoutShadow().withColor(-16777216);
    public static final EnchantedBookAccess EMPTY_ACCESS = new EnchantedBookAccess(List.of());

    private EnchantedBookAccess bookAccess;
    private int currentPage = 0;
    private PageButton forwardButton;
    private PageButton backButton;
    private final boolean playTurnSound;

    /** Delegates rendering the elements to a separate script */
    private EnchantedBookViewScreenRenderer renderer;

    public EnchantedBookViewScreen(final EnchantedBookAccess bookAccess) {
        this(bookAccess, true);
    }

    public EnchantedBookViewScreen() {
        this(EMPTY_ACCESS, false);
    }

    private EnchantedBookViewScreen(final EnchantedBookAccess bookAccess, final boolean playTurnSound) {
        super(TITLE);
        this.bookAccess = bookAccess;
        this.playTurnSound = playTurnSound;
    }

    public void setBookAccess(final EnchantedBookAccess bookAccess) {
        this.bookAccess = bookAccess;
        this.currentPage = Mth.clamp(this.currentPage, 0, bookAccess.getPageCount());
        this.updateButtonVisibility();
    }

    public boolean setPage(final int page) {
        int clampedPage = Mth.clamp(page, 0, this.bookAccess.getPageCount() - 1);
        if (clampedPage != this.currentPage) {
            this.currentPage = clampedPage;
            this.updateButtonVisibility();
            return true;
        }

        return false;
    }

    protected boolean forcePage(final int page) {
        return this.setPage(page);
    }

    protected void init() {
        this.renderer = new EnchantedBookViewScreenRenderer(this);
        this.createMenuControls();
        this.createPageControlButtons();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        if (renderer != null) {
            renderer.renderBackground(graphics);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        if (currentPage == -1 || renderer == null) {
            return;
        }

        var pages = this.bookAccess.pages();
        if (this.currentPage >= pages.size()) {
            return;
        }

        var page = pages.get(this.currentPage);
        renderer.renderForeground(graphics, mouseX, mouseY, page, this.currentPage, this.getNumPages());
    }

    public Component getNarrationMessage() {
        var components = new ArrayList<Component>();
        components.add(super.getNarrationMessage());
        components.add(this.getPageNumberMessage());
        // TODO: Get page content narration component
        return CommonComponents.joinLines(components);
    }

    private Component getPageNumberMessage() {
        var parameters = new Object[] { this.currentPage + 1, Math.max(this.getNumPages(), 1) };
        return Component.translatable("book.pageIndicator", parameters).withStyle(PAGE_TEXT_STYLE);
    }

    /**
     * Creates the "done" button and sets up its handler
     */
    protected void createMenuControls() {
        var button = Button.builder(CommonComponents.GUI_DONE, (btn) -> this.onClose());
        button.pos((this.width - 200) / 2, 196);
        button.width(200);
        this.addRenderableWidget(button.build());
    }

    /**
     * Creates the paging buttons and sets up their button handlers
     */
    protected void createPageControlButtons() {
        var borderPadding = 25;
        var buttonWidth = 24;

        var y = 159;
        var backgroundWidth = EnchantedBookViewScreenRenderer.BACKGROUND_WIDTH;
        var left = width / 2 - backgroundWidth / 2 + borderPadding;
        var right = width / 2 + backgroundWidth / 2 - borderPadding - buttonWidth;
        this.forwardButton = this.addRenderableWidget(new PageButton(right, y, true, button -> this.pageForward(), true));
        this.backButton = this.addRenderableWidget(new PageButton(left, y, false, button -> this.pageBack(), true));
        this.updateButtonVisibility();
    }

    private int getNumPages() {
        return this.bookAccess.getPageCount();
    }

    protected void pageBack() {
        if (this.currentPage > 0) {
            this.currentPage--;
        }

        this.updateButtonVisibility();
    }

    protected void pageForward() {
        if (this.currentPage < this.getNumPages() - 1) {
            this.currentPage++;
        }

        this.updateButtonVisibility();
    }

    /**
     * Show & hide the page buttons based on the current page and number of total
     * pages
     */
    private void updateButtonVisibility() {
        this.forwardButton.visible = this.currentPage < this.getNumPages() - 1;
        this.backButton.visible = this.currentPage > 0;
    }

    /**
     * Handle keyboard inputs from the user to allow changing pages
     */
    public boolean keyPressed(final KeyEvent event) {
        if (super.keyPressed(event)) {
            return true;
        }

        if (event.key() == 266) {
            this.backButton.onPress(event);
            return true;
        }

        if (event.key() == 267) {
            this.forwardButton.onPress(event);
            return true;
        }

        return false;

    }

    /** Method from BookViewScreen */
    protected void closeContainerOnServer() {
    }

    /** Method from BookViewScreen */
    public boolean isInGameUi() {
        return true;
    }
}
