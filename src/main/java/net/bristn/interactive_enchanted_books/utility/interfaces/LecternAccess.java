package net.bristn.interactive_enchanted_books.utility.interfaces;

import java.util.List;

import net.bristn.interactive_enchanted_books.utility.wrappers.EnchantmentWrapper;
import net.bristn.interactive_enchanted_books.utility.wrappers.ParticleWrapper;
import net.minecraft.world.item.ItemStack;

public interface LecternAccess {

    public int getPageCount();

    public int getCurrentPage();

    public void setCurrentPage(int page);

    public void updateCacheUsingStack(ItemStack book);

    public List<EnchantmentWrapper> getCachedEnchantments();

    public List<ParticleWrapper> getCachedParticles();

    public ParticleWrapper getParticleForPage(int page);

    public int updateParticleIndex();

    public boolean getWasPowered();

    public void setWasPowered(boolean wasPowered);

    public void setChiseledBookshelfBookCount(int bookCount);

    public int getChiseledBookshelfBookCount();
}
