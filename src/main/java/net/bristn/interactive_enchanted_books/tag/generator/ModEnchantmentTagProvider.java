package net.bristn.interactive_enchanted_books.tag.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
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

        // ! Lectern signal 1 (Cover page - no enchantments)
        groups.add(List.of());

        // ! Lectern signal 2 (Universal)
        groups.add(List.of(Enchantments.MENDING, Enchantments.UNBREAKING));

        // ! Lectern signal 3 (Curses)
        groups.add(List.of(Enchantments.VANISHING_CURSE, Enchantments.BINDING_CURSE));

        // ! Lectern signal 4 (All armor slots)
        groups.add(List.of(Enchantments.PROTECTION, Enchantments.PROJECTILE_PROTECTION, Enchantments.FIRE_PROTECTION,
                Enchantments.BLAST_PROTECTION));

        // ! Lectern signal 5 (Special armor helmets)
        groups.add(List.of(Enchantments.AQUA_AFFINITY, Enchantments.RESPIRATION));

        // ! Lectern signal 6 (Special armor chestplates)
        // Not really chestplate exclusive, but in this section to have some entries
        groups.add(List.of(Enchantments.THORNS));

        // ! Lectern signal 7 (Special armor leggings)
        groups.add(List.of(Enchantments.SWIFT_SNEAK));

        // ! Lectern signal 8 (Special armor boots)
        groups.add(List.of(Enchantments.FROST_WALKER, Enchantments.DEPTH_STRIDER, Enchantments.FEATHER_FALLING,
                Enchantments.SOUL_SPEED));

        // ! Lectern signal 9 (Mining Tools)
        groups.add(List.of(Enchantments.FORTUNE, Enchantments.EFFICIENCY, Enchantments.SILK_TOUCH));

        // ! Lectern signal 10 (Weapons Melee - Damage)
        groups.add(List.of(
                // Swords
                Enchantments.SHARPNESS, Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS, Enchantments.IMPALING,
                // Maces
                Enchantments.DENSITY, Enchantments.BREACH));

        // ! Lectern signal 11 (Weapons Melee - Utility)
        groups.add(List.of(
                // Swords
                Enchantments.FIRE_ASPECT, Enchantments.SWEEPING_EDGE, Enchantments.KNOCKBACK, Enchantments.LOOTING,
                // Maces
                Enchantments.WIND_BURST,
                // Spears
                Enchantments.LUNGE));

        // ! Lectern signal 12 (Weapons Ranged - Damage)
        groups.add(List.of(Enchantments.POWER));

        // ! Lectern signal 13 (Weapons Ranged - Utility)
        groups.add(List.of(
                // Bow
                Enchantments.PUNCH, Enchantments.INFINITY, Enchantments.FLAME,
                // Crossbow
                Enchantments.MULTISHOT, Enchantments.QUICK_CHARGE, Enchantments.PIERCING,
                // Trident
                Enchantments.CHANNELING, Enchantments.LOYALTY, Enchantments.RIPTIDE));

        // ! Lectern signal 14 (Other Tools - Utility)
        groups.add(List.of(Enchantments.LURE, Enchantments.LUCK_OF_THE_SEA));

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
                    CommonModInitializer.LOGGER.error(message);
                }
            }
        }

        // Show the enchantments that are not covered by the groups
        if (remainingEnchantments.isEmpty() == false) {
            var message = "Some enchantments are not covered in the signal data: " + remainingEnchantments.toString();
            CommonModInitializer.LOGGER.error(message);
        }
    }
}