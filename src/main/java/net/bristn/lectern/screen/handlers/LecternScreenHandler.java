package net.bristn.lectern.screen.handlers;

import java.util.Objects;

import org.slf4j.Logger;

import net.bristn.lectern.LecternEnchantedBooks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.LecternBlockEntity;

public class LecternScreenHandler extends AbstractContainerMenu {
    private static final Logger LOGGER = LecternEnchantedBooks.LOGGER;

    public static final MenuType<LecternScreenHandler> SCREEN_HANDLER = new MenuType<>(
            ((containerId, inventory) -> new LecternScreenHandler(containerId)),
            FeatureFlags.VANILLA_SET);

    // private final Slot slot;
    // private final BlockPos lecternPos;

    private final Container lectern;
    private final ContainerData lecternData;

    /**
     * Client-side constructor
     * 
     * @param syncId
     * @param playerInventory
     */
    protected LecternScreenHandler(int syncId) {
        this(syncId, new SimpleContainer(1), new SimpleContainerData(1));
    }

    /**
     * Server-side constructor
     * 
     * @param id
     * @param playerInventory
     * @param lectern
     * @param lecternPos
     */
    public LecternScreenHandler(int id, Container lectern, ContainerData lecternData) {
        super(SCREEN_HANDLER, id);

        checkContainerSize(lectern, 1);
        checkContainerDataCount(lecternData, 1);
        this.lectern = lectern;
        this.lecternData = lecternData;
        this.addSlot(new Slot(lectern, 0, 0, 0) {
            {
                Objects.requireNonNull(LecternScreenHandler.this);
            }

            @Override
            public void setChanged() {
                super.setChanged();
                LecternScreenHandler.this.slotsChanged(this.container);
            }
        });

        this.addDataSlots(lecternData);

        // TODO: Does not properly sync the container menu to the client!

        // checkContainerSize(lectern, 1);
        // this.lectern = lectern;
        // lectern.startOpen(playerInventory.player);
        // slot = new Slot(this.lectern, 0, Integer.MAX_VALUE, Integer.MAX_VALUE) {
        // public void setChanged() {
        // super.setChanged();
        // LecternScreenHandler.this.slotsChanged(this.container);
        // }
        // };

        // this.addSlot(slot);
        // this.lecternPos = lecternPos;

        // // TODO: Is it possible to get the ServerPlayer that opened the lectern? If
        // so a
        // // networking payload can be used to send the book item to the Screen

        // if (playerInventory.player instanceof ServerPlayer serverPlayer) {
        // var test = lectern.getItem(0);
        // lectern.setChanged();
        // LOGGER.info("Server: " + test.toString());
        // // ! Is fired (could sync book using this)
        // }

    }

    @Override
    public boolean clickMenuButton(final Player player, final int buttonId) {
        if (buttonId >= 100) {
            int pageToSet = buttonId - 100;
            this.setData(0, pageToSet);
            return true;
        } else {
            switch (buttonId) {
                case 1: {
                    int currentPage = this.lecternData.get(0);
                    this.setData(0, currentPage - 1);
                    return true;
                }
                case 2: {
                    int currentPage = this.lecternData.get(0);
                    this.setData(0, currentPage + 1);
                    return true;
                }
                case 3:
                    if (!player.mayBuild()) {
                        return false;
                    }

                    ItemStack book = this.lectern.removeItemNoUpdate(0);
                    this.lectern.setChanged();
                    if (!player.getInventory().add(book)) {
                        player.drop(book, false);
                    }

                    return true;
                default:
                    return false;
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(final Player player, final int slotIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setData(final int id, final int value) {
        super.setData(id, value);
        this.broadcastChanges();
    }

    @Override
    public boolean stillValid(final Player player) {
        return this.lectern.stillValid(player);
    }

    public ItemStack getBook() {
        return this.lectern.getItem(0);
    }

    public int getPage() {
        return this.lecternData.get(0);
    }
}