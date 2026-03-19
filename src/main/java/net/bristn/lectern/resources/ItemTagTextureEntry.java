package net.bristn.lectern.resources;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ItemTagTextureEntry {
    public final TagKey<Item> tagKey;
    public final Identifier texture;
    public final int order;

    public ItemTagTextureEntry(TagKey<Item> tagKey, Identifier texture, int order) {
        this.tagKey = tagKey;
        this.texture = texture;
        this.order = order;
    }
}