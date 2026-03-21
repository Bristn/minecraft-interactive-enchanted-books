package net.bristn.lectern.payloads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;

import net.bristn.lectern.EnchantmentUtility;
import net.bristn.lectern.LecternEnchantedBooks;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
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
import net.minecraft.world.item.enchantment.effects.AllOf.EntityEffects;

public class EnchantmentValueHelper {
    private static final Logger LOGGER = LecternEnchantedBooks.LOGGER;

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

        var result = new HashMap<String, Float>();
        for (var i = 0; i < values.size(); i++) {
            if (i <= translations.parameters.size() - 1) {
                var parameters = translations.parameters.get(i);
                result.put(parameters.name, values.get(i));
            }
        }

        LOGGER.info(result.toString());

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
            LOGGER.info("Unrecognized enchantment:");
            LOGGER.info(effectRecord.toString());
        }

        return result;
    }

    /**
     * 
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

        case EntityEffects value:
            var effects = value.effects();
            for (var nestedEffect : effects) {
                result.addAll(getEffectValues(nestedEffect, enchantmentLevel));
            }

            break;

        // Empty case to prevent this effect type from showing in the logs
        case @SuppressWarnings("unused") PlaySoundEffect value:
            break;

        default:
            LOGGER.info("Unrecognized enchantment effect:");
            LOGGER.info(effect.toString());
            break;
        }

        return result;
    }

}
