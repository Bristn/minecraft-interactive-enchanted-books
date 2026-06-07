
package net.bristn.interactive_enchanted_books.resources.loader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.mojang.serialization.JsonOps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.bristn.interactive_enchanted_books.resources.EnchantmentData;
import net.bristn.interactive_enchanted_books.resources.EnchantmentNamedParameter;
import net.bristn.interactive_enchanted_books.resources.json.EnchantmentDataJson;
import net.bristn.interactive_enchanted_books.resources.json.EnchantmentNamedParameterJson;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public class EnchantmentDataLoader implements PreparableReloadListener {
    private static final String FILE_NAME = "enchantment_setting.jsonc";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<EnchantmentData> DATA = new ArrayList<>();
    private static final HashMap<Identifier, EnchantmentData> DATA_BY_TAG = new HashMap<>();

    @Override
    public CompletableFuture<Void> reload(SharedState currentReload, Executor taskExecutor, PreparationBarrier preparationBarrier,
            Executor reloadExecutor) {

        var manager = currentReload.resourceManager();
        var prepareFuture = CompletableFuture.supplyAsync(() -> loadAllResources(manager), taskExecutor);
        var afterBarrier = preparationBarrier.wait(null).thenCombine(prepareFuture, (v, prepared) -> prepared);
        return afterBarrier.thenAcceptAsync(this::apply, reloadExecutor);
    }

    /**
     * Uses the resource manager to read all relevant data files
     */
    private List<EnchantmentDataJson> loadAllResources(ResourceManager manager) {
        CommonModInitializer.LOGGER.info("EnchantmentDataLoader: Loading data from " + FILE_NAME);

        // Filter out any resource with the given file names
        var modResources = manager.listResourceStacks("data", (identifier) -> {
            var namespace = identifier.getNamespace();
            if (namespace.equals(CommonModInitializer.MOD_ID) == false) {
                return false;
            }

            var path = identifier.getPath();
            return path.endsWith(FILE_NAME);
        });

        // Read each found json file & convert its content
        var result = new ArrayList<EnchantmentDataJson>();
        for (var resourceEntry : modResources.entrySet()) {
            var resources = resourceEntry.getValue();
            for (var resource : resources) {
                try {
                    var modResult = loadResource(resource);
                    result.addAll(modResult);
                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
        }

        return result;
    }

    /**
     * Reads the json data of the resource file and converts the data into a collection of java objects
     * per array entry
     */
    private List<EnchantmentDataJson> loadResource(Resource resource) throws IOException {

        // Read the json file. The root of the file is a json array
        var stream = resource.open();
        var reader = new InputStreamReader(stream);
        var array = GSON.fromJson(reader, JsonArray.class);

        // Convert each array entry to the Java class
        var result = new ArrayList<EnchantmentDataJson>();
        for (var jsonElement : array) {
            var jsonObject = jsonElement.getAsJsonObject();
            var parameters = getNamedParameters(jsonObject);

            // Parse the main json object
            var data = EnchantmentDataJson.CODEC.parse(JsonOps.INSTANCE, jsonObject);
            data.ifSuccess(entry -> {
                result.add(entry.withParameters(parameters));
            });

            data.ifError(error -> {
                CommonModInitializer.LOGGER.info("EnchantmentDataLoader: Error parsing {} {}", FILE_NAME, error);
            });
        }

        return result;
    }

    /**
     * Helper function to parse the nested array of parameters. Each parameter corresponds to one
     * LevelBasedValue. A single LevelBasedValue can have multiple names and formatters. This way the
     * enchantment level may be used to gather different type of display values
     */
    private ArrayList<ArrayList<EnchantmentNamedParameterJson>> getNamedParameters(JsonObject jsonObject) {
        var result = new ArrayList<ArrayList<EnchantmentNamedParameterJson>>();

        // If there is no "parameters" property, return an empty array
        if (jsonObject.has("parameters") == false) {
            return result;
        }

        // Otherwise read the values array. Each entry is another array containing the
        // names for this LevelBasedValue
        var valuesJsonArray = jsonObject.get("parameters").getAsJsonArray();
        for (var valueJson : valuesJsonArray) {
            var namedParametersPerValue = new ArrayList<EnchantmentNamedParameterJson>();
            var parametersJsonArray = valueJson.getAsJsonArray();

            for (var parameterJson : parametersJsonArray) {
                var data = EnchantmentNamedParameterJson.CODEC.parse(JsonOps.INSTANCE, parameterJson);
                data.ifSuccess(entry -> {
                    namedParametersPerValue.add(entry);
                });

                data.ifError(error -> {
                    CommonModInitializer.LOGGER.error("EnchantmentDataLoader: Error parsing {} {}", FILE_NAME, error);
                });
            }

            result.add(namedParametersPerValue);
        }

        return result;
    }

    /**
     * Performs final modifications on all read data in the main thread. Converts the HashMap of
     * multiple mods into a single list that respects the priorities of the json
     */
    private void apply(List<EnchantmentDataJson> prepared) {

        // Merge all the different mod setting into one flat map
        var flatMap = new HashMap<String, EnchantmentDataJson>();
        for (var entry : prepared) {
            var enchantmentName = entry.enchantment();
            var priority = entry.priority();

            // If no entry is registered for the tag, add this one
            if (flatMap.containsKey(enchantmentName) == false) {
                flatMap.put(enchantmentName, entry);
                continue;
            }

            // Otherwise check the current priority and overwrite if necessary
            var existingEntry = flatMap.get(enchantmentName);
            if (priority > existingEntry.priority()) {
                flatMap.put(enchantmentName, entry);

                var enchantment = existingEntry.enchantment();
                var oldParticle = existingEntry.particle();
                var newParticle = entry.particle();
                CommonModInitializer.LOGGER.info("EnchantmentDataLoader: Overwriting {} particle {} with {}", enchantment,
                        oldParticle, newParticle);
            }
        }

        // Convert the temp data to the final form (Get the tag groups & the texture ID)
        DATA.clear();
        DATA_BY_TAG.clear();
        for (var entry : flatMap.values()) {
            var enchantmentIdentifier = Identifier.tryParse(entry.enchantment());
            var particleId = Identifier.tryParse(entry.particle());

            var particleOptional = BuiltInRegistries.PARTICLE_TYPE.get(particleId);
            if (particleOptional.isPresent() == false) {
                continue;
            }

            var particleType = particleOptional.get().value();
            if (particleType instanceof ParticleOptions == false) {
                continue;
            }

            var particle = (ParticleOptions) particleType;
            var parameters = EnchantmentNamedParameter.fromJsonList(entry.parameters());
            var data = new EnchantmentData(enchantmentIdentifier, particle, parameters, entry.isCurse(), particleId);
            DATA.add(data);
            DATA_BY_TAG.put(enchantmentIdentifier, data);
        }

        CommonModInitializer.LOGGER.info("EnchantmentDataLoader: Loaded a total of {} unique entries", DATA.size());
    }

    public static List<EnchantmentData> getList() {
        return Collections.unmodifiableList(DATA);
    }

    public static Map<Identifier, EnchantmentData> getMap() {
        return Collections.unmodifiableMap(DATA_BY_TAG);
    }
}