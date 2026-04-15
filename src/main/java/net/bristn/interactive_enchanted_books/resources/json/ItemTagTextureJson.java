package net.bristn.interactive_enchanted_books.resources.json;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ItemTagTextureJson(String tag, String texture, int priority, int order) {

    public static final Codec<ItemTagTextureJson> CODEC = RecordCodecBuilder.create(instance -> {
        var tagBuilder = Codec.STRING.fieldOf("tag").forGetter(ItemTagTextureJson::tag);
        var textureBuilder = Codec.STRING.fieldOf("texture").forGetter(ItemTagTextureJson::texture);
        var priorityBuilder = Codec.INT.optionalFieldOf("priority", 0).forGetter(ItemTagTextureJson::priority);

        return instance.group(tagBuilder, textureBuilder, priorityBuilder).apply(instance, ((tag, texture, priority) -> {
            return new ItemTagTextureJson(tag, texture, priority, 1000);
        }));
    });

    public ItemTagTextureJson withOrder(int order) {
        return new ItemTagTextureJson(tag, texture, priority, order);
    }
}