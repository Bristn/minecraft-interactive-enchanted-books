package net.bristn.lectern.resources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ItemTagTextureJsonEntry(String tag, String texture, int priority) {

    public static final Codec<ItemTagTextureJsonEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("tag").forGetter(ItemTagTextureJsonEntry::tag),
            Codec.STRING.fieldOf("texture").forGetter(ItemTagTextureJsonEntry::texture),
            Codec.INT.fieldOf("priority").forGetter(ItemTagTextureJsonEntry::priority))
            .apply(instance, ItemTagTextureJsonEntry::new));
}