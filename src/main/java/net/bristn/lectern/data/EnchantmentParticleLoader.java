
package net.bristn.lectern.data;

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
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import org.slf4j.Logger;

public class EnchantmentParticleLoader implements PreparableReloadListener {
    private static final String FILE_NAME = "enchantment_particle.jsonc";
    private static final Logger LOGGER = LecternEnchantedBooks.LOGGER;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<ItemTagTextureEntry> DATA = new ArrayList<>();
    private static final HashMap<TagKey<Item>, ItemTagTextureEntry> DATA_BY_TAG = new HashMap<>();

    @Override
    public CompletableFuture<Void> reload(SharedState currentReload, Executor taskExecutor,
            PreparationBarrier preparationBarrier, Executor reloadExecutor) {

        var manager = currentReload.resourceManager();
        var prepareFuture = CompletableFuture.supplyAsync(() -> loadAllResources(manager), taskExecutor);
        var afterBarrier = preparationBarrier.wait(null).thenCombine(prepareFuture, (v, prepared) -> prepared);
        return afterBarrier.thenAcceptAsync(this::apply, reloadExecutor);
    }

    /**
     * Uses the resource manager to read all relevant data files
     * 
     * @param manager
     * @return
     */
    private Map<Identifier, List<EnchantmentParticleJsonEntry>> loadAllResources(ResourceManager manager) {
        LOGGER.info("EnchantmentParticleLoader: Loading data from item_tag_texture.json");

        // Filter out any resource with the given file name
        var modResources = manager.listResources("data", (identifier) -> {
            var namespace = identifier.getNamespace();
            if (namespace.equals(LecternEnchantedBooks.MOD_ID) == false) {
                return false;
            }

            var path = identifier.getPath();
            return path.endsWith(FILE_NAME);
        });

        // Read each found json file & convert its content
        var result = new HashMap<Identifier, List<EnchantmentParticleJsonEntry>>();
        for (var resourceEntry : modResources.entrySet()) {
            var resourceId = resourceEntry.getKey();
            var resource = resourceEntry.getValue();

            try {
                var modResult = loadResource(resourceId, resource);
                result.putAll(modResult);
            } catch (IOException error) {
                error.printStackTrace();
            }
        }

        return result;
    }

    /**
     * Reads the json data of the resource file and converts the data into a
     * collection of java objects per array entry
     * 
     * @param resourceId
     * @param resource
     * @return
     * @throws IOException
     */
    private HashMap<Identifier, List<EnchantmentParticleJsonEntry>> loadResource(Identifier resourceId,
            Resource resource)
            throws IOException {

        // Read the json file. The root of the file is a json array
        var stream = resource.open();
        var reader = new InputStreamReader(stream);
        var array = GSON.fromJson(reader, JsonArray.class);

        // Convert each array entry to the Java class
        var result = new HashMap<Identifier, List<EnchantmentParticleJsonEntry>>();
        for (var jsonElement : array) {

            var data = EnchantmentParticleJsonEntry.CODEC.parse(JsonOps.INSTANCE, jsonElement);
            data.ifSuccess(entry -> {
                result.putIfAbsent(resourceId, new ArrayList<EnchantmentParticleJsonEntry>());
                result.get(resourceId).add(entry);
            });

            data.ifError(error -> {
                LOGGER.info("EnchantmentParticleLoader: Encountered an error whilst loading {} {}", resourceId, error);
            });
        }

        return result;
    }

    /**
     * Performs final modifications on all read data in the main thread. Converts
     * the HashMap of multiple mods into a single list that respects the priorities
     * of the json
     * 
     * @param prepared
     */
    private void apply(Map<Identifier, List<EnchantmentParticleJsonEntry>> prepared) {

        // Merge all the different mod setting into one flat map
        var flatMap = new HashMap<String, EnchantmentParticleJsonEntry>();
        for (var entriesPerMod : prepared.entrySet()) {
            for (var entry : entriesPerMod.getValue()) {
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
                    LOGGER.info("EnchantmentParticleLoader: Overwriting {} particle {} with {}", enchantment,
                            oldParticle, newParticle);
                }
            }
        }

        // Convert the temp data to the final form (Get the tag groups & the texture ID)
        DATA.clear();
        DATA_BY_TAG.clear();
        for (var entry : flatMap.values()) {
            var textureIdentifier = Identifier.tryParse(entry.particle());
            var tagIdentifier = Identifier.tryParse(entry.particle());

            var tagKey = TagKey.create(Registries.ITEM, tagIdentifier);
            var data = new ItemTagTextureEntry(tagKey, textureIdentifier);
            DATA.add(data);
            DATA_BY_TAG.put(tagKey, data);
        }

        LOGGER.info("EnchantmentParticleLoader: Loaded a total of {} unique entries", DATA.size());
    }

    public static List<ItemTagTextureEntry> getList() {
        return Collections.unmodifiableList(DATA);
    }

    public static Map<TagKey<Item>, ItemTagTextureEntry> getMap() {
        return Collections.unmodifiableMap(DATA_BY_TAG);
    }
}