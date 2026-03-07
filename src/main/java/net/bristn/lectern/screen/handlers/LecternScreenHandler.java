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

    public static final MenuType<LecternScreenHandler> SCREEN_HANDLER = new MenuType<>(LecternScreenHandler::new, FeatureFlags.VANILLA_SET);

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

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == 3) {
            if (!player.mayBuild())
                return false;
            ItemStack itemStack = this.inventory.removeItemNoUpdate(0);
            this.inventory.setChanged();
            if (!player.getInventory().add(itemStack))
                player.drop(itemStack, false);
            if (player.level().getBlockEntity(lecternPos) != null)
                ((LecternAccess) player.level().getBlockEntity(lecternPos)).setIsTomeReaderLectern(false);
            return true;
        } else if (id == 4) {
            ItemStack mainStack = player.getMainHandItem();
            ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
            if (mainStack.getItem() != Items.BOOK || mainStack.getCount() != 1 || (player.experienceLevel < 3 && !player.getAbilities().instabuild))
                return false;
            player.giveExperienceLevels(-3);
            player.level().playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1,
                    Math.max(1F, player.getRandom().nextFloat() + 0.3F));

            ItemEnchantments enchants = this.inventory.getItem(0).get(DataComponents.STORED_ENCHANTMENTS);
            Holder<Enchantment> transferEnchant = enchants.keySet().stream().iterator().next();
            ItemEnchantments.Mutable bookBuilder = new ItemEnchantments.Mutable(enchantedBook.getEnchantments());
            bookBuilder.upgrade(transferEnchant, enchants.getLevel(transferEnchant));
            enchantedBook.set(DataComponents.STORED_ENCHANTMENTS, bookBuilder.toImmutable());
            player.setItemInHand(InteractionHand.MAIN_HAND, enchantedBook);

            ItemEnchantments.Mutable stackBuilder = new ItemEnchantments.Mutable(enchants);
            stackBuilder.removeIf(entry -> entry == transferEnchant);

            this.inventory.getItem(0).set(DataComponents.STORED_ENCHANTMENTS, stackBuilder.toImmutable());
            inventory.setChanged();
            slot.setChanged();
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    public FormattedText getPage(int i) {
        return FormattedText.of("Testcontent");
    }

}