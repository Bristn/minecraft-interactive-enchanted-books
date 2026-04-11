
package net.bristn.lectern.resources.loader;

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

import net.bristn.lectern.LecternEnchantedBooks;
import net.bristn.lectern.resources.EnchantmentDataEntry;
import net.bristn.lectern.resources.EnchantmentTranslationEntry;
import net.bristn.lectern.resources.json.EnchantmentDataJsonEntry;
import net.bristn.lectern.resources.json.EnchantmentTranslationJsonEntry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public class EnchantmentDataLoader implements PreparableReloadListener {
    private static final String FILE_NAME = "enchantment_setting.jsonc";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<EnchantmentDataEntry> DATA = new ArrayList<>();
    private static final HashMap<Identifier, EnchantmentDataEntry> DATA_BY_TAG = new HashMap<>();

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
    private List<EnchantmentDataJsonEntry> loadAllResources(ResourceManager manager) {
        LecternEnchantedBooks.LOGGER.info("EnchantmentDataLoader: Loading data from " + FILE_NAME);

        // Filter out any resource with the given file names
        var modResources = manager.listResourceStacks("data", (identifier) -> {
            var namespace = identifier.getNamespace();
            if (namespace.equals(LecternEnchantedBooks.MOD_ID) == false) {
                return false;
            }

            var path = identifier.getPath();
            return path.endsWith(FILE_NAME);
        });

        // Read each found json file & convert its content
        var result = new ArrayList<EnchantmentDataJsonEntry>();
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
     * Reads the json data of the resource file and converts the data into a
     * collection of java objects per array entry
     */
    private List<EnchantmentDataJsonEntry> loadResource(Resource resource) throws IOException {

        // Read the json file. The root of the file is a json array
        var stream = resource.open();
        var reader = new InputStreamReader(stream);
        var array = GSON.fromJson(reader, JsonArray.class);

        // Convert each array entry to the Java class
        var result = new ArrayList<EnchantmentDataJsonEntry>();
        for (var jsonElement : array) {
            var jsonObject = jsonElement.getAsJsonObject();

            // Read the optional translation parameters from the json object
            var parameters = new ArrayList<EnchantmentTranslationJsonEntry>();
            if (jsonObject.has("parameters") == true) {
                var parametersJson = jsonObject.get("parameters").getAsJsonArray();
                for (var parameter : parametersJson) {
                    var data = EnchantmentTranslationJsonEntry.CODEC.parse(JsonOps.INSTANCE, parameter);
                    data.ifSuccess(entry -> {
                        parameters.add(entry);
                    });

                    data.ifError(error -> {
                        LecternEnchantedBooks.LOGGER.error("EnchantmentDataLoader: Error parsing {} {}", FILE_NAME, error);
                    });
                }
            }

            // Parse the main json object
            var data = EnchantmentDataJsonEntry.CODEC.parse(JsonOps.INSTANCE, jsonObject);
            data.ifSuccess(entry -> {
                result.add(entry.withParameters(parameters));
            });

            data.ifError(error -> {
                LecternEnchantedBooks.LOGGER.info("EnchantmentDataLoader: Error parsing {} {}", FILE_NAME, error);
            });
        }

        return result;
    }

    /**
     * Performs final modifications on all read data in the main thread. Converts
     * the HashMap of multiple mods into a single list that respects the priorities
     * of the json
     */
    private void apply(List<EnchantmentDataJsonEntry> prepared) {

        // Merge all the different mod setting into one flat map
        var flatMap = new HashMap<String, EnchantmentDataJsonEntry>();
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
                LecternEnchantedBooks.LOGGER.info("EnchantmentDataLoader: Overwriting {} particle {} with {}", enchantment,
                        oldParticle, newParticle);
            }
        }

        // Convert the temp data to the final form (Get the tag groups & the texture ID)
        DATA.clear();
        DATA_BY_TAG.clear();
        for (var entry : flatMap.values()) {
            var enchantmentIdentifier = Identifier.tryParse(entry.enchantment());
            var particleIdentifier = Identifier.tryParse(entry.particle());

            var particleOptional = BuiltInRegistries.PARTICLE_TYPE.get(particleIdentifier);
            if (particleOptional.isPresent() == false) {
                continue;
            }

            var particle = particleOptional.get().value();
            if (particle instanceof ParticleOptions == false) {
                continue;
            }

            var parameters = EnchantmentTranslationEntry.fromJsonList(entry.parameters());
            var data = new EnchantmentDataEntry(enchantmentIdentifier, (ParticleOptions) particle, parameters);
            DATA.add(data);
            DATA_BY_TAG.put(enchantmentIdentifier, data);
        }

        LecternEnchantedBooks.LOGGER.info("EnchantmentDataLoader: Loaded a total of {} unique entries", DATA.size());
    }

    public static List<EnchantmentDataEntry> getList() {
        return Collections.unmodifiableList(DATA);
    }

    public static Map<Identifier, EnchantmentDataEntry> getMap() {
        return Collections.unmodifiableMap(DATA_BY_TAG);
    }
}