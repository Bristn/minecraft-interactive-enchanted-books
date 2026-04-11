package net.bristn.lectern.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.bristn.lectern.LecternEnchantedBooks;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.item.enchantment.LevelBasedValue.Fraction;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.effects.ApplyEntityImpulse;
import net.minecraft.world.item.enchantment.effects.ApplyExhaustion;
import net.minecraft.world.item.enchantment.effects.ApplyMobEffect;
import net.minecraft.world.item.enchantment.effects.ChangeItemDamage;
import net.minecraft.world.item.enchantment.effects.DamageEntity;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.enchantment.effects.ExplodeEffect;
import net.minecraft.world.item.enchantment.effects.Ignite;
import net.minecraft.world.item.enchantment.effects.MultiplyValue;
import net.minecraft.world.item.enchantment.effects.PlaySoundEffect;
import net.minecraft.world.item.enchantment.effects.RemoveBinomial;
import net.minecraft.world.item.enchantment.effects.ReplaceDisk;
import net.minecraft.world.item.enchantment.effects.ScaleExponentially;
import net.minecraft.world.item.enchantment.effects.SetValue;
import net.minecraft.world.item.enchantment.effects.SpawnParticlesEffect;
import net.minecraft.world.item.enchantment.effects.AllOf.EntityEffects;

/**
 * Helper to read the LevelBased values of the enchantment effects
 */
public class EnchantmentValueUtility {

    /**
     * Gets the translation parameters for the given enchantment. Uses the
     * "parameters" of the enchantment json data to assign names to the enchantment
     * effect values. For this to work as expected, the ordering of values must
     * match the ordering of the naming in the json. If the first effect of the
     * enchantment is attribute:protection, then the first naming array element is
     * for this first effect value
     */
    public static HashMap<String, Float> getTranslationParameters(Enchantment enchantment, int enchantmentLevel) {
        var values = new ArrayList<Float>();

        var effectsMap = enchantment.effects();
        for (var effectEntry : effectsMap) {
            if (effectEntry.value() instanceof List<?> list) {
                for (var effectRecord : list) {
                    values.addAll(getValues(effectRecord, enchantmentLevel));
                }
            }
        }

        var translations = EnchantmentUtility.getParticleForEnchantment(enchantment);
        if (translations == null) {
            return new HashMap<>();
        }

        LecternEnchantedBooks.LOGGER
                .info("Enchantment data: " + translations.enchantment.toString() + "  " + translations.parameters.size());

        // Add level as last parameter
        values.add((float) enchantmentLevel);

        var result = new HashMap<String, Float>();
        for (var i = 0; i < values.size(); i++) {
            if (i <= translations.parameters.size() - 1) {
                var parameters = translations.parameters.get(i);
                var value = parameters.transformer.apply(values.get(i));
                result.put(parameters.name, value);
            }
        }

        LecternEnchantedBooks.LOGGER.info("LevelBasedValues: {}, Named parameters: {}", values, result);
        return result;
    }

    private static List<Float> getValues(Object effectRecord, int enchantmentLevel) {
        var result = new ArrayList<Float>();

        if (effectRecord instanceof ConditionalEffect effect) {
            return getEffectValues(effect.effect(), enchantmentLevel);
        } else if (effectRecord instanceof TargetedConditionalEffect effect) {
            return getEffectValues(effect.effect(), enchantmentLevel);
        } else if (effectRecord instanceof EnchantmentAttributeEffect effect) {
            // ! Can be the root effect (e.g. Blast Protection), but also a child
            return List.of(effect.amount().calculate(enchantmentLevel));
        } else {
            if (effectRecord instanceof Holder holder) {
                // ! Ignore sound events
                if (holder.value() instanceof SoundEvent) {
                    return new ArrayList<Float>();
                }
            }

            LecternEnchantedBooks.LOGGER.info("Unrecognized enchantment:");
            LecternEnchantedBooks.LOGGER.info(effectRecord.toString());
        }

        return result;
    }

    /**
     * Handles the different effects the enchantment may have. Reads the
     * LevelBasedValue of the effect and returns it. If the effect has more than one
     * value, the values are returned as part of an array
     */
    private static List<Float> getEffectValues(Object effect, int enchantmentLevel) {
        var result = new ArrayList<Float>();

        switch (effect) {
        case AddValue value:
            result.add(value.value().calculate(enchantmentLevel));
            break;

        case MultiplyValue value:
            result.add(value.factor().calculate(enchantmentLevel));
            break;

        case RemoveBinomial value:
            result.add(value.chance().calculate(enchantmentLevel));
            break;

        case ScaleExponentially value:
            var base = value.base().calculate(enchantmentLevel);
            var exponent = value.exponent().calculate(enchantmentLevel);
            result.add((float) Math.pow(base, exponent));
            break;

        case SetValue value:
            result.add(value.value().calculate(enchantmentLevel));
            break;

        case ApplyMobEffect value:
            result.add(value.minDuration().calculate(enchantmentLevel));
            result.add(value.maxDuration().calculate(enchantmentLevel));
            result.add(value.minAmplifier().calculate(enchantmentLevel));
            result.add(value.maxAmplifier().calculate(enchantmentLevel));
            break;

        case ChangeItemDamage value:
            result.add(value.amount().calculate(enchantmentLevel));
            break;

        case ApplyExhaustion value:
            result.add(value.amount().calculate(enchantmentLevel));
            break;

        case ApplyEntityImpulse value:
            result.add(value.magnitude().calculate(enchantmentLevel));
            break;

        case DamageEntity value:
            result.add(value.minDamage().calculate(enchantmentLevel));
            result.add(value.maxDamage().calculate(enchantmentLevel));
            break;

        case ExplodeEffect value:
            if (value.knockbackMultiplier().isEmpty() == false) {
                result.add(value.knockbackMultiplier().get().calculate(enchantmentLevel));
            } else {
                result.add(0.0F);
            }

            result.add(value.radius().calculate(enchantmentLevel));
            break;

        case Ignite value:
            result.add(value.duration().calculate(enchantmentLevel));
            break;

        case ReplaceDisk value:
            result.add(value.radius().calculate(enchantmentLevel));
            break;

        case EnchantmentAttributeEffect value:
            result.add(value.amount().calculate(enchantmentLevel));
            break;

        case Fraction value:
            result.add(value.calculate(enchantmentLevel));
            break;

        case EntityEffects value:
            var effects = value.effects();
            for (var nestedEffect : effects) {
                result.addAll(getEffectValues(nestedEffect, enchantmentLevel));
            }

            break;

        // Empty case to prevent this effect type from showing in the logs
        case @SuppressWarnings("unused") PlaySoundEffect value:
            break;

        case @SuppressWarnings("unused") SpawnParticlesEffect value:
            break;

        default:
            LecternEnchantedBooks.LOGGER.info("Unrecognized enchantment effect:");
            LecternEnchantedBooks.LOGGER.info(effect.toString());
            break;
        }

        return result;
    }
}
