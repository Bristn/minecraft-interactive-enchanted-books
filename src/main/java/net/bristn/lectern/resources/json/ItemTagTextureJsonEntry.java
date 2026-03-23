package net.bristn.lectern.resources.json;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ItemTagTextureJsonEntry(String tag, String texture, int priority, int order) {

    public static final Codec<ItemTagTextureJsonEntry> CODEC = RecordCodecBuilder.create(instance -> {
        var tagBuilder = Codec.STRING.fieldOf("tag").forGetter(ItemTagTextureJsonEntry::tag);
        var textureBuilder = Codec.STRING.fieldOf("texture").forGetter(ItemTagTextureJsonEntry::texture);
        var priorityBuilder = Codec.INT.fieldOf("priority").forGetter(ItemTagTextureJsonEntry::priority);

        return instance.group(tagBuilder, textureBuilder, priorityBuilder).apply(instance, ((tag, texture, priority) -> {
            return new ItemTagTextureJsonEntry(tag, texture, priority, 1000);
        }));
    });

    public ItemTagTextureJsonEntry withOrder(int order) {
        return new ItemTagTextureJsonEntry(tag, texture, priority, order);
    }
}