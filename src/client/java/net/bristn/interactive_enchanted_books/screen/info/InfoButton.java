package net.bristn.interactive_enchanted_books.screen.info;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class InfoButton extends Button {
    private static final Identifier BUTTON = Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID,
            "textures/gui/info/info_button.png");

    private static final Identifier INFO_BUTTON_HIGHLIGHTED = Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID,
            "textures/gui/info/info_button_highlighted.png");

    private static final WidgetSprites SPRITES = new WidgetSprites(BUTTON, INFO_BUTTON_HIGHLIGHTED);

    public static int WIDTH = 48;
    public static int HEIGHT = 24;

    public InfoButton(final Button.OnPress onPress, String buttonKey) {
        super(0, 0, WIDTH, HEIGHT, Component.translatable(buttonKey), onPress, DEFAULT_NARRATION);
    }

    public void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        var sprite = SPRITES.get(this.isActive(), this.isHoveredOrFocused());
        var pipeline = RenderPipelines.GUI_TEXTURED;
        graphics.blit(pipeline, sprite, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
    }
}
