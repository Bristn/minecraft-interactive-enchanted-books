package net.bristn.interactive_enchanted_books.resources;

import java.util.ArrayList;
import java.util.function.Function;

import net.bristn.interactive_enchanted_books.resources.json.EnchantmentNamedParameterJson;

public class EnchantmentNamedParameter {
    public final String name;
    public final Function<Float, Float> transformer;

    public EnchantmentNamedParameter(String name, Function<Float, Float> transformer) {
        this.name = name;
        this.transformer = transformer;
    }

    public static EnchantmentNamedParameter fromJson(EnchantmentNamedParameterJson json) {
        return new EnchantmentNamedParameter(json.name(), json.transformer());
    }

    public static ArrayList<ArrayList<EnchantmentNamedParameter>> fromJsonList(
            ArrayList<ArrayList<EnchantmentNamedParameterJson>> json) {

        var result = new ArrayList<ArrayList<EnchantmentNamedParameter>>();
        for (var array : json) {
            var parametersPerValue = new ArrayList<EnchantmentNamedParameter>();
            for (var entry : array) {
                parametersPerValue.add(fromJson(entry));
            }

            result.add(parametersPerValue);
        }

        return result;
    }
}
