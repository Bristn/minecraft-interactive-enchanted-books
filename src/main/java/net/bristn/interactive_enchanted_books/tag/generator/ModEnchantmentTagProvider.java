package net.bristn.interactive_enchanted_books.tag.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.bristn.interactive_enchanted_books.LecternEnchantedBooks;
import net.bristn.interactive_enchanted_books.tag.ModEnchantmentTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class ModEnchantmentTagProvider extends FabricTagsProvider<Enchantment> {
    public ModEnchantmentTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, Registries.ENCHANTMENT, registriesFuture);

        // Initialize the tag list
        ModEnchantmentTags.registerModEnchantmentTags();
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        var registry = registries.lookupOrThrow(Registries.ENCHANTMENT);
        var remainingEnchantments = new ArrayList<ResourceKey<Enchantment>>();

        registry.listElements().forEach(holder -> {
            remainingEnchantments.add(holder.key());
        });

        var groups = new ArrayList<List<ResourceKey<Enchantment>>>();

        // Lectern signal 1 (Cover page - no enchantments)
        groups.add(List.of());

        // Lectern signal 2 (Universal)
        groups.add(List.of(Enchantments.MENDING, Enchantments.UNBREAKING, Enchantments.VANISHING_CURSE));

        // Lectern signal 3 (Universal armor slots)
        groups.add(List.of(Enchantments.PROTECTION, Enchantments.PROJECTILE_PROTECTION, Enchantments.FIRE_PROTECTION,
                Enchantments.BLAST_PROTECTION, Enchantments.THORNS, Enchantments.BINDING_CURSE));

        // Lectern signal 4 (Special armor slots)
        groups.add(List.of(
                // Boots
                Enchantments.FROST_WALKER, Enchantments.DEPTH_STRIDER, Enchantments.FEATHER_FALLING, Enchantments.SOUL_SPEED,
                // Helmets
                Enchantments.AQUA_AFFINITY, Enchantments.RESPIRATION,
                // Leggings
                Enchantments.SWIFT_SNEAK));

        // Lectern signal 5 (Tools)
        groups.add(List.of(Enchantments.FORTUNE, Enchantments.EFFICIENCY, Enchantments.SILK_TOUCH));

        // Lectern signal 6 (Swords)
        groups.add(List.of(
                // Damaging
                Enchantments.SHARPNESS, Enchantments.FIRE_ASPECT, Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS,
                // Utility
                Enchantments.SWEEPING_EDGE, Enchantments.KNOCKBACK, Enchantments.FLAME, Enchantments.LOOTING));

        // Lectern signal 7 (Bow)
        groups.add(List.of(Enchantments.PUNCH, Enchantments.POWER, Enchantments.INFINITY));

        // Lectern signal 8 (Crossbow)
        groups.add(List.of(Enchantments.PIERCING, Enchantments.MULTISHOT, Enchantments.QUICK_CHARGE));

        // Lectern signal 9 (Special tools - Trident, Mace, Spear, Fishing rod)
        groups.add(List.of(
                // Trident
                Enchantments.CHANNELING, Enchantments.LOYALTY, Enchantments.RIPTIDE, Enchantments.IMPALING,
                // Mace
                Enchantments.DENSITY, Enchantments.BREACH, Enchantments.WIND_BURST,
                // Spear
                Enchantments.LUNGE,
                // Fishing rod
                Enchantments.LURE, Enchantments.LUCK_OF_THE_SEA));

        // Lectern signal 10 ()
        groups.add(List.of());

        // Lectern signal 11 ()
        groups.add(List.of());

        // Lectern signal 12 ()
        groups.add(List.of());

        // Lectern signal 13 ()
        groups.add(List.of());

        // Lectern signal 14 ()
        groups.add(List.of());

        // Lectern signal 15 ()
        groups.add(List.of());

        // Create a data json for each of the signal groups
        for (var i = 0; i < groups.size(); i++) {
            var builderRef = builder(ModEnchantmentTags.LECTERN_SIGNAL.get(i));

            for (var enchantment : groups.get(i)) {
                builderRef.add(enchantment);
            }
        }

        // Check if there are any missing enchantments
        for (var group : groups) {
            for (var enchantment : group) {
                var removed = remainingEnchantments.remove(enchantment);

                // Show an additional error if one enchantment is in multiple groups
                if (removed == false) {
                    var message = "Enchantment is present in multiple groups: " + enchantment.toString();
                    LecternEnchantedBooks.LOGGER.error(message);
                }
            }
        }

        // Show the enchantments that are not covered by the groups
        if (remainingEnchantments.isEmpty() == false) {
            var message = "Some enchantments are not covered in the signal data: " + remainingEnchantments.toString();
            LecternEnchantedBooks.LOGGER.error(message);
        }
    }
}