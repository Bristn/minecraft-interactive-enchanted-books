package net.bristn.lectern.resources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ItemTagTextureJsonEntry(String tag, String texture, int priority, int order) {

    public static final Codec<ItemTagTextureJsonEntry> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    Codec.STRING.fieldOf("tag").forGetter(ItemTagTextureJsonEntry::tag),
                    Codec.STRING.fieldOf("texture").forGetter(ItemTagTextureJsonEntry::texture),
                    Codec.INT.fieldOf("priority").forGetter(ItemTagTextureJsonEntry::priority))
            .apply(instance, ((tag, texture, priority) -> new ItemTagTextureJsonEntry(tag, texture, priority, 1000))));

    public ItemTagTextureJsonEntry withOrder(int order) {
        return new ItemTagTextureJsonEntry(tag, texture, priority, order);
    }
}