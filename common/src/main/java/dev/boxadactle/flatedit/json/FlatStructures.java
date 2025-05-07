package dev.boxadactle.flatedit.json;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public enum FlatStructures {
    VILLAGES("villages", BuiltinStructureSets.VILLAGES),
    DESERT_PYRAMIDS("desert_pyramids", BuiltinStructureSets.DESERT_PYRAMIDS),
    IGLOOS("igloos", BuiltinStructureSets.IGLOOS),
    JUNGLE_TENMPLES("jungle_temples", BuiltinStructureSets.JUNGLE_TEMPLES),
    SWAMP_HUTS("swamp_huts", BuiltinStructureSets.SWAMP_HUTS),
    PILLAGER_OUTPOSTS("pillager_outposts", BuiltinStructureSets.PILLAGER_OUTPOSTS),
    OCEAN_MONUMENTS("ocean_monuments", BuiltinStructureSets.OCEAN_MONUMENTS),
    WOODLAND_MANSIONS("woodland_mansions", BuiltinStructureSets.WOODLAND_MANSIONS),
    BURIED_TREASURES("buried_treasures", BuiltinStructureSets.BURIED_TREASURES),
    MINESHAFTS("mineshafts", BuiltinStructureSets.MINESHAFTS),
    RUINED_PORTALS("ruined_portals", BuiltinStructureSets.RUINED_PORTALS),
    SHIPWRECKS("shipwrecks", BuiltinStructureSets.SHIPWRECKS),
    OCEAN_RUINS("ocean_ruins", BuiltinStructureSets.OCEAN_RUINS),
    NETHER_COMPLEXES("nether_complexes", BuiltinStructureSets.NETHER_COMPLEXES),
    END_CITIES("end_cities", BuiltinStructureSets.END_CITIES),
    ANCIENT_CITIES("ancient_cities", BuiltinStructureSets.ANCIENT_CITIES),
    STRONGHOLDS("strongholds", BuiltinStructureSets.STRONGHOLDS),
    TRAIL_RUINS("trail_ruins", BuiltinStructureSets.TRAIL_RUINS),
    TRIAL_CHAMBERS("trial_chambers", BuiltinStructureSets.TRIAL_CHAMBERS),;

    final String id;
    final ResourceKey<StructureSet> structure;

    FlatStructures(String id, ResourceKey<StructureSet> structure) {
        this.id = id;
        this.structure = structure;
    }

    public String getId() {
        return id;
    }

    public ResourceKey<StructureSet> getStructure() {
        return structure;
    }

    public Holder<StructureSet> getStructure(HolderGetter<StructureSet> structures) {
        return structures.getOrThrow(getStructure());
    }

    public static FlatStructures fromStructureSet(Holder<StructureSet> structureSet) {
        for (FlatStructures structure : values()) {
            if (structureSet.is(structure.getStructure().location())) {
                return structure;
            }
        }
        return null;
    }

    public static FlatStructures fromId(String id) {
        for (FlatStructures structure : values()) {
            if (structure.getId().equals(id)) {
                return structure;
            }
        }
        return null;
    }

}
