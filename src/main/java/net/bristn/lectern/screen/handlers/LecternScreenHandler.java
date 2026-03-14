package net.bristn.lectern.screen.handlers;

import net.bristn.lectern.LecternAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class LecternScreenHandler extends AbstractContainerMenu {

    public static final MenuType<LecternScreenHandler> SCREEN_HANDLER = new MenuType<>(LecternScreenHandler::new,
            FeatureFlags.VANILLA_SET);

    private final Container inventory;
    private final Slot slot;
    private final BlockPos lecternPos;

    /**
     * Constructor for the screen handler
     * 
     * @param syncId
     * @param playerInventory
     */
    protected LecternScreenHandler(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(1), new BlockPos(0, 0, 0));
    }

    /**
     * 
     * @param id
     * @param playerInventory
     * @param inventory
     * @param lecternPos
     */
    public LecternScreenHandler(int id, Inventory playerInventory, Container inventory, BlockPos lecternPos) {
        super(SCREEN_HANDLER, id);

        // TODO: Does not properly sync the container menu to the client!

        checkContainerSize(inventory, 1);
        this.inventory = inventory;
        inventory.startOpen(playerInventory.player);
        slot = new Slot(this.inventory, 0, Integer.MAX_VALUE, Integer.MAX_VALUE) {
            public void setChanged() {
                super.setChanged();
                LecternScreenHandler.this.slotsChanged(this.container);
            }
        };

        this.addSlot(slot);
        this.lecternPos = lecternPos;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slot) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public Container getInventory() {
        return inventory;
    }

    @Override
    public MenuType<?> getType() {
        return SCREEN_HANDLER;
    }

    public FormattedText getPage(int i) {
        return FormattedText.of("Testcontent");
    }

}