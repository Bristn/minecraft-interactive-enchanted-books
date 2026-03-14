package net.bristn.lectern.data;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ItemTagTextureEntry {
    public final TagKey<Item> tagKey;
    public final Identifier texture;

    public ItemTagTextureEntry(TagKey<Item> tagKey, Identifier texture) {
        this.tagKey = tagKey;
        this.texture = texture;
    }
}