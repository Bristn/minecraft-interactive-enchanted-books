package net.bristn.interactive_enchanted_books.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.bristn.interactive_enchanted_books.CommonModInitializer;
import net.fabricmc.fabric.impl.tag.client.ClientTagsImpl;
import net.fabricmc.fabric.impl.tag.client.ClientTagsLoader.LoadedTag;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ClientTagUtility {

    /**
     * Gets a current list of client-side only fabric tags. Used if the server does
     * not run the mod. This way, the client can determine the supported icon groups
     * even if the server is not using the mod
     */
    @SuppressWarnings("unchecked")
    public static List<TagKey<Item>> getFabricItemTags() {
        var result = new ArrayList<TagKey<Item>>();

        try {
            // Accesses the internal private field to get all currently registered tags
            var field = ClientTagsImpl.class.getDeclaredField("LOCAL_TAG_HIERARCHY");
            field.setAccessible(true);

            var value = (Map<TagKey<?>, LoadedTag>) field.get(null);
            for (var entry : value.entrySet()) {
                var tag = entry.getKey();
                var isItemTag = tag.registry().equals(Registries.ITEM);

                if (isItemTag) {
                    result.add((TagKey<Item>) tag);
                }
            }
        } catch (Exception error) {
            CommonModInitializer.LOGGER.info(error.toString());
        }

        return result;
    }

}
