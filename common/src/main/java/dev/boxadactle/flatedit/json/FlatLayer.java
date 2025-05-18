package dev.boxadactle.flatedit.json;

import com.google.gson.JsonObject;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;

import java.util.Optional;

public record FlatLayer(Block block, int layers) {
    public static FlatLayer fromInfo(FlatLayerInfo info) {
        return new FlatLayer(
                info.getBlockState().getBlock(),
                info.getHeight()
        );
    }

    public ResourceLocation getBlockId() {
        return BuiltInRegistries.BLOCK.getKey(block);
    }

    public BlockState getBlockState() {
        return block.defaultBlockState();
    }

    public FlatLayerInfo toLayer() {
        return new FlatLayerInfo(layers(), block());
    }

    public JsonObject serialize() {
        JsonObject o = new JsonObject();
        o.addProperty("block", getBlockId().toString());
        o.addProperty("layers", layers);
        return o;
    }

    public static FlatLayer deserialize(JsonObject o) {
        ResourceLocation id = ResourceLocation.parse(o.get("block").getAsString());
        Optional<Holder.Reference<Block>> block = BuiltInRegistries.BLOCK.get(id);
        int layers = o.get("layers").getAsInt();
        return new FlatLayer(block.get().value(), layers);
    }
}
