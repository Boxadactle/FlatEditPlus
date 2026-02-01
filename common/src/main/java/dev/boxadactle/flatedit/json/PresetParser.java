package dev.boxadactle.flatedit.json;

import com.google.gson.*;
import dev.boxadactle.flatedit.FlatEdit;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.biome.Biome;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PresetParser {

    public static String serialize(FlatPreset preset) {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(FlatPreset.class, new PresetSerializer());
            Gson gson = builder.create();

            return gson.toJson(preset);
        } catch (Exception e) {
            FlatEdit.LOGGER.error("Failed to serialize preset", e);
            return null;
        }
    }

    public static FlatPreset deserialize(String json, HolderGetter<Biome> biomes) {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(FlatPreset.class, new PresetDeserializer(biomes));
            Gson gson = builder.create();

            return gson.fromJson(json, FlatPreset.class);
        } catch (Exception e) {
            FlatEdit.LOGGER.error("Failed to deserialize preset", e);
            return null;
        }
    }

    public static FlatPreset deserialize(Reader json, HolderGetter<Biome> biomes) {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(FlatPreset.class, new PresetDeserializer(biomes));
            Gson gson = builder.create();

            return gson.fromJson(json, FlatPreset.class);
        } catch (Exception e) {
            FlatEdit.LOGGER.error("Failed to deserialize preset", e);
            return null;
        }
    }

    public static class PresetSerializer implements JsonSerializer<FlatPreset> {

        @Override
        public JsonElement serialize(FlatPreset preset, Type typeOfSrc, JsonSerializationContext context) {
            Gson gson = new Gson();

            JsonObject o = new JsonObject();

            o.addProperty("name", preset.name());

            o.add("layers", gson.toJsonTree(preset.layers().stream().map(FlatLayer::serialize).toList()));
            o.addProperty("biome", preset.biome().unwrapKey().get().identifier().toString());
            o.addProperty("decorations", preset.decorations());
            o.addProperty("addLakes", preset.addLakes());

            o.add("structures", gson.toJsonTree(preset.structures().stream().map(stru -> stru.id).toList()));
            o.add("features", gson.toJsonTree(preset.features().stream().map(feat -> feat.id).toList()));

            return o;
        }
    }

    public static class PresetDeserializer implements JsonDeserializer<FlatPreset> {
        HolderGetter<Biome> biomes;

        public PresetDeserializer(HolderGetter<Biome> biomes) {
            this.biomes = biomes;
        }

        @Override
        public FlatPreset deserialize(JsonElement json, Type typeOfT, com.google.gson.JsonDeserializationContext context) {
            JsonObject o = json.getAsJsonObject();

            String name = o.get("name").getAsString();

            ArrayList<FlatLayer> layers = new ArrayList<>(o.getAsJsonArray("layers").asList().stream().map(e -> FlatLayer.deserialize(e.getAsJsonObject())).toList());
            Holder<Biome> biome = biomes.getOrThrow(FlatEdit.getBiome(o.get("biome").getAsString()));
            boolean decorations = o.get("decorations").getAsBoolean();
            boolean addLakes = o.get("addLakes").getAsBoolean();

            List<FlatStructures> structures = o.get("structures").getAsJsonArray().asList().stream().map(s -> {
                String id = s.getAsString();
                return FlatStructures.fromId(id);
            }).filter(Objects::nonNull).toList();
            List<FlatFeatures> features = o.get("features").getAsJsonArray().asList().stream().map(s -> {
                String id = s.getAsString();
                return FlatFeatures.fromId(id);
            }).filter(Objects::nonNull).toList();

            return new FlatPreset(name, layers, biome, structures, features, decorations, addLakes);
        }
    }

}
