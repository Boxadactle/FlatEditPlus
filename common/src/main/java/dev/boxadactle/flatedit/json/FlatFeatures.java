package dev.boxadactle.flatedit.json;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public enum FlatFeatures {
    ICE_SPIKE("ice_spikes", MiscOverworldPlacements.ICE_SPIKE),
    ICE_PATCH("ice_patch", MiscOverworldPlacements.ICE_PATCH),
    FOREST_ROCK("forest_rock", MiscOverworldPlacements.FOREST_ROCK),
    ICEBERG_PACKED("iceberg_packed", MiscOverworldPlacements.ICEBERG_PACKED),
    ICEBERG_BLUE("iceberg_blue", MiscOverworldPlacements.ICEBERG_BLUE),
    BLUE_ICE("blue_ice", MiscOverworldPlacements.BLUE_ICE),
    LAKE_LAVA_UNDERGROUND("lake_lava_underground", MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND),
    LAKE_LAVA_SURFACE("lake_lava_surface", MiscOverworldPlacements.LAKE_LAVA_SURFACE),
    DISK_CLAY("disk_clay", MiscOverworldPlacements.DISK_CLAY),
    DISK_GRAVEL("disk_gravel", MiscOverworldPlacements.DISK_GRAVEL),
    DISK_SAND("disk_sand", MiscOverworldPlacements.DISK_SAND),
    DISK_GRASS("disk_grass", MiscOverworldPlacements.DISK_GRASS),
    FREEZE_TOP_LAYER("freeze_top_layer", MiscOverworldPlacements.FREEZE_TOP_LAYER),
    VOID_START_PLATFORM("void_start_platform", MiscOverworldPlacements.VOID_START_PLATFORM),
    DESERT_WELL("desert_well", MiscOverworldPlacements.DESERT_WELL),
    SPRING_LAVA("spring_lava", MiscOverworldPlacements.SPRING_LAVA),
    SPRING_LAVA_FROZEN("spring_lava_frozen", MiscOverworldPlacements.SPRING_LAVA_FROZEN),
    SPING_WATER("spring_water", MiscOverworldPlacements.SPRING_WATER);

    final String id;
    final ResourceKey<PlacedFeature> feature;

    FlatFeatures(String id, ResourceKey<PlacedFeature> feature) {
        this.id = id;
        this.feature = feature;
    }

    public String getId() {
        return id;
    }

    public ResourceKey<PlacedFeature> getFeature() {
        return feature;
    }

    public Holder<PlacedFeature> getFeature(HolderGetter<PlacedFeature> features) {
        return features.getOrThrow(getFeature());
    }

    public static FlatFeatures fromFeature(Holder<PlacedFeature> ft) {
        for (FlatFeatures feature : values()) {
            if (ft.is(feature.getFeature().location())) {
                return feature;
            }
        }
        return null;
    }

    public static FlatFeatures fromId(String id) {
        for (FlatFeatures feature : values()) {
            if (feature.getId().equals(id)) {
                return feature;
            }
        }
        return null;
    }
}
