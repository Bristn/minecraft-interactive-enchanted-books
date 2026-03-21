
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
import net.bristn.lectern.resources.ItemTagTextureEntry;
import net.bristn.lectern.resources.json.ItemTagTextureJsonEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import org.slf4j.Logger;

public class ItemTagTextureLoader implements PreparableReloadListener {
    private static final String FILE_NAME = "item_tag_texture.jsonc";
    private static final Logger LOGGER = LecternEnchantedBooks.LOGGER;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<ItemTagTextureEntry> DATA = new ArrayList<>();
    private static final HashMap<TagKey<Item>, ItemTagTextureEntry> DATA_BY_TAG = new HashMap<>();

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
     * 
     * @param manager
     * @return
     */
    private Map<Identifier, List<ItemTagTextureJsonEntry>> loadAllResources(ResourceManager manager) {
        LOGGER.info("ItemTagTextureLoader: Loading data from item_tag_texture.json");

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
        var result = new HashMap<Identifier, List<ItemTagTextureJsonEntry>>();
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
    private HashMap<Identifier, List<ItemTagTextureJsonEntry>> loadResource(Identifier resourceId, Resource resource)
            throws IOException {

        // Read the json file. The root of the file is a json array
        var stream = resource.open();
        var reader = new InputStreamReader(stream);
        var array = GSON.fromJson(reader, JsonArray.class);

        // Convert each array entry to the Java class
        var result = new HashMap<Identifier, List<ItemTagTextureJsonEntry>>();
        for (var i = 0; i < array.size(); i++) {
            var jsonElement = array.get(i);

            // TODO: Properly determine order. Allow other mods to insert icons at any point
            var order = i;
            var data = ItemTagTextureJsonEntry.CODEC.parse(JsonOps.INSTANCE, jsonElement);
            data.ifSuccess(entry -> {
                result.putIfAbsent(resourceId, new ArrayList<ItemTagTextureJsonEntry>());

                var ordered = entry.withOrder(order);
                result.get(resourceId).add(ordered);
            });

            data.ifError(error -> {
                LOGGER.info("ItemTagTextureLoader: Encountered an error whilst loading {} {}", resourceId, error);
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
    private void apply(Map<Identifier, List<ItemTagTextureJsonEntry>> prepared) {

        // Merge all the different mod setting into one flat map
        var flatMap = new HashMap<String, ItemTagTextureJsonEntry>();
        for (var entriesPerMod : prepared.entrySet()) {
            for (var entry : entriesPerMod.getValue()) {
                var tagName = entry.tag();
                var priority = entry.priority();

                // If no entry is registered for the tag, add this one
                if (flatMap.containsKey(tagName) == false) {
                    flatMap.put(tagName, entry);
                    continue;
                }

                // Otherwise check the current priority and overwrite if necessary
                var existingEntry = flatMap.get(tagName);
                if (priority > existingEntry.priority()) {
                    flatMap.put(tagName, entry);

                    var tag = existingEntry.tag();
                    var oldTexture = existingEntry.texture();
                    var newTexture = entry.texture();
                    LOGGER.info("ItemTagTextureLoader: Overwriting {} texture {} with {}", tag, oldTexture, newTexture);
                }
            }
        }

        // Convert the temp data to the final form (Get the tag groups & the texture ID)
        DATA.clear();
        DATA_BY_TAG.clear();
        for (var entry : flatMap.values()) {
            var textureIdentifier = Identifier.tryParse(entry.texture());
            var tagIdentifier = Identifier.tryParse(entry.tag());

            var tagKey = TagKey.create(Registries.ITEM, tagIdentifier);
            var data = new ItemTagTextureEntry(tagKey, textureIdentifier, entry.order());
            DATA.add(data);
            DATA_BY_TAG.put(tagKey, data);
        }

        LOGGER.info("ItemTagTextureLoader: Loaded a total of {} unique entries", DATA.size());
    }

    public static List<ItemTagTextureEntry> getList() {
        return Collections.unmodifiableList(DATA);
    }

    public static Map<TagKey<Item>, ItemTagTextureEntry> getMap() {
        return Collections.unmodifiableMap(DATA_BY_TAG);
    }
}