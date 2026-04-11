
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
import net.bristn.lectern.resources.ItemTagTexture;
import net.bristn.lectern.resources.json.ItemTagTextureJson;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ItemTagTextureLoader implements PreparableReloadListener {
    private static final String FILE_NAME = "item_tag_texture.jsonc";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final List<ItemTagTexture> DATA = new ArrayList<>();
    private static final HashMap<TagKey<Item>, ItemTagTexture> DATA_BY_TAG = new HashMap<>();

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
    private List<ItemTagTextureJson> loadAllResources(ResourceManager manager) {
        LecternEnchantedBooks.LOGGER.info("ItemTagTextureLoader: Loading data from " + FILE_NAME);

        // Filter out any resource with the given file name
        var modResources = manager.listResourceStacks("data", (identifier) -> {
            var namespace = identifier.getNamespace();
            if (namespace.equals(LecternEnchantedBooks.MOD_ID) == false) {
                return false;
            }

            var path = identifier.getPath();
            return path.endsWith(FILE_NAME);
        });

        // Read each found json file & convert its content
        var result = new ArrayList<ItemTagTextureJson>();
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
    private List<ItemTagTextureJson> loadResource(Resource resource) throws IOException {

        // Read the json file. The root of the file is a json array
        var stream = resource.open();
        var reader = new InputStreamReader(stream);
        var array = GSON.fromJson(reader, JsonArray.class);

        // Convert each array entry to the Java class
        var result = new ArrayList<ItemTagTextureJson>();
        for (var i = 0; i < array.size(); i++) {
            var jsonElement = array.get(i);

            // TODO: Properly determine order. Allow other mods to insert icons at any point
            var order = i;
            var data = ItemTagTextureJson.CODEC.parse(JsonOps.INSTANCE, jsonElement);
            data.ifSuccess(entry -> {
                var ordered = entry.withOrder(order);
                result.add(ordered);
            });

            data.ifError(error -> {
                LecternEnchantedBooks.LOGGER.info("ItemTagTextureLoader: Error parsing {} {}", FILE_NAME, error);
            });
        }

        return result;
    }

    /**
     * Performs final modifications on all read data in the main thread. Converts
     * the HashMap of multiple mods into a single list that respects the priorities
     * of the json
     */
    private void apply(List<ItemTagTextureJson> prepared) {

        // Merge all the different mod setting into one flat map
        var flatMap = new HashMap<String, ItemTagTextureJson>();
        for (var entry : prepared) {
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
                LecternEnchantedBooks.LOGGER.info("ItemTagTextureLoader: Overwriting {} texture {} with {}", tag, oldTexture,
                        newTexture);
            }
        }

        // Convert the temp data to the final form (Get the tag groups & the texture ID)
        DATA.clear();
        DATA_BY_TAG.clear();
        for (var entry : flatMap.values()) {
            var textureIdentifier = Identifier.tryParse(entry.texture());
            var tagIdentifier = Identifier.tryParse(entry.tag());

            var tagKey = TagKey.create(Registries.ITEM, tagIdentifier);
            var data = new ItemTagTexture(tagKey, textureIdentifier, entry.order());
            DATA.add(data);
            DATA_BY_TAG.put(tagKey, data);
        }

        LecternEnchantedBooks.LOGGER.info("ItemTagTextureLoader: Loaded a total of {} unique entries", DATA.size());
    }

    public static List<ItemTagTexture> getList() {
        return Collections.unmodifiableList(DATA);
    }

    public static Map<TagKey<Item>, ItemTagTexture> getMap() {
        return Collections.unmodifiableMap(DATA_BY_TAG);
    }
}