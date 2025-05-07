package dev.boxadactle.flatedit.json;

import dev.boxadactle.flatedit.mixin.FlatSettingsAccessor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FlatPreset {

    public String name;
    public ArrayList<FlatLayer> layers;
    public Holder<Biome> biome;
    public ArrayList<FlatStructures> structures;
    public ArrayList<FlatFeatures> features;
    public boolean decorations;
    public boolean addLakes;

    public FlatPreset(String name, ArrayList<FlatLayer> layers, Holder<Biome> biome, List<FlatStructures> structures, List<FlatFeatures> features, boolean decorations, boolean addLakes) {
        this.name = name;
        this.layers = layers;
        this.biome = biome;
        this.structures = new ArrayList<>(structures);
        this.features = new ArrayList<>(features);
        this.decorations = decorations;
        this.addLakes = addLakes;
    }

    public String name() {
        return name;
    }

    public ArrayList<FlatLayer> layers() {
        return layers;
    }

    public Holder<Biome> biome() {
        return biome;
    }

    public List<FlatStructures> structures() {
        return structures;
    }

    public List<FlatFeatures> features() {
        return features;
    }

    public boolean decorations() {
        return decorations;
    }

    public boolean addLakes() {
        return addLakes;
    }

    public static FlatPreset fromSettings(FlatLevelGeneratorSettings settings) {
        ArrayList<FlatLayer> layers = new ArrayList<>();
        for (FlatLayerInfo flatLayerInfo : settings.getLayersInfo()) {
            layers.add(FlatLayer.fromInfo(flatLayerInfo));
        }

        Holder<Biome> biome = settings.getBiome();

        List<FlatStructures> structures = ((FlatSettingsAccessor) settings).getStructures().get().stream().map(FlatStructures::fromStructureSet).toList();
        List<FlatFeatures> features = ((FlatSettingsAccessor) settings).getLakes().stream().map(FlatFeatures::fromFeature).toList();

        boolean decorations = ((FlatSettingsAccessor) settings).isDecoration();
        boolean addLakes = ((FlatSettingsAccessor) settings).isAddLakes();

        return new FlatPreset("preset", layers, biome, structures, features, decorations, addLakes);
    }

    public FlatLevelGeneratorSettings toSettings(HolderGetter<StructureSet> structures, HolderGetter<PlacedFeature> features) {
        FlatLevelGeneratorSettings settings = new FlatLevelGeneratorSettings(
                Optional.of(HolderSet.direct(structures().stream().map(s -> s.getStructure(structures)).toList())),
                biome,
                features().stream().map(f -> f.getFeature(features)).toList()
            );

        layers().forEach(lay -> settings.getLayersInfo().add(lay.toLayer()));

        if (decorations) settings.setDecoration();
        if (addLakes) settings.setAddLakes();

        settings.updateLayers();

        return settings;
    }

    public void switchRows(int from, int to) {
        FlatLayer layer = layers.get(from);
        layers.set(from, layers.get(to));
        layers.set(to, layer);
    }

    public int getCurrentLayers() {
        int l = 0;

        for (FlatLayer layer : layers) {
            l += layer.layers();
        }

        return l;
    }

}
