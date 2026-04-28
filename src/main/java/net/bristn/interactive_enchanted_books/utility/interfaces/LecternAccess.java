package net.bristn.interactive_enchanted_books.utility.interfaces;

import java.util.List;

import net.bristn.interactive_enchanted_books.utility.wrappers.EnchantmentWrapper;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.item.ItemStack;

public interface LecternAccess {

    public int getPageCount();

    public int getCurrentPage();

    public void setCurrentPage(int page);

    public void updateCacheUsingStack(ItemStack book);

    public List<EnchantmentWrapper> getCachedEnchantments();

    public List<ParticleOptions> getCachedParticles();

    public int updateParticleIndex();

    public boolean getWasPowered();

    public void setWasPowered(boolean wasPowered);
}
