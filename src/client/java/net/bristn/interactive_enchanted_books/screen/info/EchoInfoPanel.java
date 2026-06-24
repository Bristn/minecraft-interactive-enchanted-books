package net.bristn.interactive_enchanted_books.screen.info;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.bristn.interactive_enchanted_books.items.ModItems;
import net.bristn.interactive_enchanted_books.screen.EnchantedBookViewScreenRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;

public class EchoInfoPanel {
    private static final int BUTTON_Y = 8;
    private static final int BUTTON_X_FROM_MIDDLE = EnchantedBookViewScreenRenderer.BACKGROUND_WIDTH / 2 + 8;
    private static final int CONTAINER_HEIGHT = 72;

    private static final Identifier INPUT_SLOT = Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID,
            "textures/gui/info/slot.png");

    private static final Identifier ARROW_DOWN = Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID,
            "textures/gui/info/arrow_down.png");

    private static final Identifier OUTPUT_SLOT = Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID,
            "textures/gui/info/slot_output.png");

    private static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(CommonModInitializer.MOD_ID,
            "textures/gui/info/crafting_background.png");

    private boolean showPanel;
    private TooltipContext context;
    private Font font;

    public EchoInfoPanel() {
        this.showPanel = false;
        this.context = Item.TooltipContext.of(Minecraft.getInstance().level);
        this.font = Minecraft.getInstance().font;
    }

    public void togglePanel() {
        this.showPanel = !this.showPanel;
    }

    public void drawPanel(GuiGraphicsExtractor graphics, int mouseX, int mouseY, int screenWidth) {
        if (CommonModInitializer.isInstalledOnServer == false || CommonModInitializer.areEchosCraftable == false) {
            return;
        }

        var padding = 4;
        var iconSize = 16;

        // Draw the enchantment echo in the button
        var x = screenWidth / 2 + BUTTON_X_FROM_MIDDLE;
        var y = BUTTON_Y;
        graphics.item(ModItems.ENCHANTMENT_ECHO.getDefaultInstance(), x + InfoButton.WIDTH / 2 - iconSize / 2, y + 4);
        y += InfoButton.HEIGHT + padding;

        if (this.showPanel == false) {
            return;
        }

        // Draw the crafting recipe for echos
        {
            var inputA = ItemWithTooltip.of(Items.ENCHANTED_BOOK,
                    Component.translatable("gui.interactive_enchanted_books.echo_info_any_enchanted_item"),
                    Component.translatable("gui.interactive_enchanted_books.echo_info_any_enchantment"),
                    Component.translatable("gui.interactive_enchanted_books.echo_info_not_consumed") //
            );
            var inputB = ItemWithTooltip.of(Items.BOOK);
            var output = ItemWithTooltip.of(ModItems.ENCHANTMENT_ECHO,
                    Component.translatable("gui.interactive_enchanted_books.echo_info_same_enchantments")//
            );
            this.drawTwoIngredientRecipe(graphics, x, y, inputA, inputB, output, mouseX, mouseY);
        }

        // Draw the chiseled bookshelf part of the info
        {
            y += CONTAINER_HEIGHT + padding;

            var inputA = ItemWithTooltip.of(ModItems.ENCHANTMENT_ECHO,
                    Component.translatable("gui.interactive_enchanted_books.echo_info_any_enchantment") //
            );
            var inputB = ItemWithTooltip.of(Items.ECHO_SHARD);
            var output = ItemWithTooltip.of(Items.ENCHANTED_BOOK,
                    Component.translatable("gui.interactive_enchanted_books.echo_info_same_enchantments") //
            );
            this.drawTwoIngredientRecipe(graphics, x, y, inputA, inputB, output, mouseX, mouseY);
        }
    }

    private void drawTwoIngredientRecipe(GuiGraphicsExtractor graphics, int x, int y, ItemWithTooltip inputA,
            ItemWithTooltip inputB, ItemWithTooltip output, int mouseX, int mouseY) {

        graphics.blit(RenderPipelines.GUI_TEXTURED, BACKGROUND, x, y, 0.0F, 0.0F, InfoButton.WIDTH, CONTAINER_HEIGHT,
                InfoButton.WIDTH, CONTAINER_HEIGHT);
        x += 6;
        y += 6;

        var iconSize = 16;
        var inSize = 18;
        var outSize = 25;

        // Draw the first item
        {
            var stack = inputA.item().getDefaultInstance();
            graphics.blit(RenderPipelines.GUI_TEXTURED, INPUT_SLOT, x, y, 0.0F, 0.0F, inSize, inSize, inSize, inSize);
            graphics.item(stack, x + 1, y + 1);

            if (mouseX > x && mouseX < x + inSize) {
                if (mouseY > y && mouseY < y + inSize) {
                    var lines = stack.getTooltipLines(context, null, TooltipFlag.Default.NORMAL);
                    for (var tooltip : inputA.tooltips()) {
                        lines.add(tooltip.withStyle(ChatFormatting.GRAY));
                    }

                    graphics.setComponentTooltipForNextFrame(font, lines, mouseX, mouseY);
                }
            }
        }

        // Draw the second item
        {
            var stack = inputB.item().getDefaultInstance();
            var newX = x + inSize;
            graphics.blit(RenderPipelines.GUI_TEXTURED, INPUT_SLOT, newX, y, 0.0F, 0.0F, inSize, inSize, inSize, inSize);
            graphics.item(stack, newX + 1, y + 1);

            if (mouseX > newX && mouseX < newX + inSize) {
                if (mouseY > y && mouseY < y + inSize) {
                    var lines = stack.getTooltipLines(context, null, TooltipFlag.Default.NORMAL);
                    for (var tooltip : inputB.tooltips()) {
                        lines.add(tooltip.withStyle(ChatFormatting.GRAY));
                    }

                    graphics.setComponentTooltipForNextFrame(font, lines, mouseX, mouseY);
                }
            }
        }

        {
            y += inSize;
            var middleX = x + inSize - iconSize / 2;
            graphics.blit(RenderPipelines.GUI_TEXTURED, ARROW_DOWN, middleX, y, 0.0F, 0.0F, iconSize, iconSize, iconSize,
                    iconSize);
            y += iconSize;
        }

        // Draw the output item
        {
            var stack = output.item().getDefaultInstance();
            var newX = x + (inSize * 2 - outSize) / 2 + 1;
            graphics.blit(RenderPipelines.GUI_TEXTURED, OUTPUT_SLOT, newX, y, 0.0F, 0.0F, outSize, outSize, outSize, outSize);
            graphics.item(stack, newX + 4, y + 4);

            if (mouseX > newX && mouseX < newX + outSize) {
                if (mouseY > y && mouseY < y + outSize) {
                    var lines = stack.getTooltipLines(context, null, TooltipFlag.Default.NORMAL);
                    for (var tooltip : output.tooltips()) {
                        lines.add(tooltip.withStyle(ChatFormatting.GRAY));
                    }

                    graphics.setComponentTooltipForNextFrame(font, lines, mouseX, mouseY);
                }
            }
        }
    }
}
