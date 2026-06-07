package net.bristn.interactive_enchanted_books.screen.info;

import java.util.Map;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.bristn.interactive_enchanted_books.mixin.MinecraftAccessor;
import net.bristn.interactive_enchanted_books.mixin.ParticleResourcesAccessor;
import net.bristn.interactive_enchanted_books.screen.EnchantedBookViewScreenRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;

public class ParticleInfoPanel {
    private static final int BUTTON_Y = 8;
    private static final int BUTTON_X_FROM_MIDDLE = EnchantedBookViewScreenRenderer.BACKGROUND_WIDTH / 2 + InfoButton.WIDTH + 8;
    private static final int CONTAINER_HEIGHT = 120;

    private static final Identifier INPUT_SLOT = Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID,
            "textures/gui/info/slot.png");

    private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID,
            "textures/gui/info/particle_background.png");

    private static final Identifier CARET_DOWN = Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID,
            "textures/gui/info/caret_down.png");

    private static final Identifier PLUS = Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID,
            "textures/gui/info/plus.png");

    private boolean showPanel;
    private TooltipContext context;
    private Font font;
    private final Map<Identifier, SpriteSet> particleSpriteSets;

    public ParticleInfoPanel() {
        this.showPanel = false;
        this.context = Item.TooltipContext.of(Minecraft.getInstance().level);
        this.font = Minecraft.getInstance().font;

        var minecraft = (MinecraftAccessor) Minecraft.getInstance();
        var resources = (ParticleResourcesAccessor) minecraft.getParticleResources();
        this.particleSpriteSets = resources.getSpriteSets();
    }

    public void togglePanel() {
        this.showPanel = !this.showPanel;
    }

    public void drawPanel(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int screenWidth, Identifier particleId) {
        if (CommonModInitializer.isInstalledOnServer == false) {
            return;
        }

        // The cover page does not have a particle. Therefore use a fallback
        if (particleId == null) {
            particleId = Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID, "frost_walker");
        }

        var iconSize = 16;
        var padding = 4;

        // Draw the enchantment echo in the button
        var x = screenWidth / 2 - BUTTON_X_FROM_MIDDLE;
        var y = BUTTON_Y;
        drawParticleTexture(graphics, x + InfoButton.WIDTH / 2 - iconSize / 2, y + 4, particleId, mouseX, mouseY);
        y += InfoButton.HEIGHT + padding;

        if (this.showPanel == false) {
            return;
        }

        // Draw the background
        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, 0.0F, 0.0F, InfoButton.WIDTH, CONTAINER_HEIGHT,
                InfoButton.WIDTH, CONTAINER_HEIGHT);
        x += 15;
        y += 6;

        var slotSize = 18;
        var caretSize = 8;
        var plusSize = 16;

        // Draw the lectern part
        {
            var inputA = ItemWithTooltip.of(Items.ENCHANTED_BOOK, //
                    Component.translatable("gui.interactive_enchanted_books.particle_info_or_echo"), //
                    Component.translatable("gui.interactive_enchanted_books.particle_info_in_lectern") //
            );
            var inputB = ItemWithTooltip.of(Items.LECTERN);
            drawTwoRequirements(graphics, x, y, inputA, inputB, mouseX, mouseY);
        }

        y += slotSize * 2 + caretSize + 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, PLUS, x, y, 0.0F, 0.0F, slotSize, slotSize, slotSize, slotSize);
        y += plusSize + 2;

        // Draw the bookshelf part
        {
            var inputA = ItemWithTooltip.of(Items.BOOK, //
                    Component.translatable("gui.interactive_enchanted_books.particle_info_any_book"), //
                    Component.translatable("gui.interactive_enchanted_books.particle_info_in_bookshelf") //
            );
            var inputB = ItemWithTooltip.of(Items.CHISELED_BOOKSHELF, //
                    Component.translatable("gui.interactive_enchanted_books.particle_info_touching_lectern") //
            );
            drawTwoRequirements(graphics, x, y, inputA, inputB, mouseX, mouseY);
        }
    }

    private void drawTwoRequirements(GuiGraphicsExtractor graphics, int x, int y, ItemWithTooltip inputA, ItemWithTooltip inputB,
            int mouseX, int mouseY) {

        var inSize = 18;
        var caretSize = 8;

        // Draw the first item
        {
            var stack = inputA.item().getDefaultInstance();
            graphics.blit(RenderPipelines.GUI_TEXTURED, INPUT_SLOT, x, y, 0.0F, 0.0F, inSize, inSize, inSize, inSize);
            graphics.item(stack, x + 1, y + 1);

            if (mouseX > x && mouseX < x + inSize) {
                if (mouseY > y && mouseY < y + inSize) {
                    var lines = stack.getTooltipLines(context, null, TooltipFlag.Default.NORMAL);
                    for (var tooltip : inputA.tooltips()) {
                        lines.add(tooltip.withColor(ChatFormatting.GRAY.getColor()));
                    }

                    graphics.setComponentTooltipForNextFrame(font, lines, mouseX, mouseY);
                }
            }
        }

        // Draw the caret
        y += inSize;
        graphics.blit(RenderPipelines.GUI_TEXTURED, CARET_DOWN, x + 5, y, 0.0F, 0.0F, caretSize, caretSize, caretSize, caretSize);
        y += caretSize;

        // Draw the second item
        {
            var stack = inputB.item().getDefaultInstance();
            graphics.blit(RenderPipelines.GUI_TEXTURED, INPUT_SLOT, x, y, 0.0F, 0.0F, inSize, inSize, inSize, inSize);
            graphics.item(stack, x + 1, y + 1);

            if (mouseX > x && mouseX < x + inSize) {
                if (mouseY > y && mouseY < y + inSize) {
                    var lines = stack.getTooltipLines(context, null, TooltipFlag.Default.NORMAL);
                    for (var tooltip : inputB.tooltips()) {
                        lines.add(tooltip.withColor(ChatFormatting.GRAY.getColor()));
                    }

                    graphics.setComponentTooltipForNextFrame(font, lines, mouseX, mouseY);
                }
            }
        }

    }

    /**
     * Helper method to draw the particle texture at any point in the gut.
     */
    private void drawParticleTexture(GuiGraphicsExtractor graphics, int x, int y, Identifier particleId, int mouseX, int mouseY) {
        var iconSize = 16;

        // Draw the particle texture
        var spriteSet = particleSpriteSets.get(particleId);
        var sprite = spriteSet.first();
        graphics.blit(sprite.atlasLocation(), //
                x, y, // Top left position
                x + iconSize, y + iconSize, // Bottom right position
                sprite.getU0(), sprite.getU1(), sprite.getV0(), sprite.getV1());

    }
}
