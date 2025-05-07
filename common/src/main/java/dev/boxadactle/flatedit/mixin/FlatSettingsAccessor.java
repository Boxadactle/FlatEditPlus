package dev.boxadactle.flatedit.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;
import java.util.Optional;

@Mixin(FlatLevelGeneratorSettings.class)
public interface FlatSettingsAccessor {

    @Accessor("decoration")
    boolean isDecoration();

    @Accessor("addLakes")
    boolean isAddLakes();

    @Accessor("structureOverrides")
    Optional<HolderSet<StructureSet>> getStructures();

    @Accessor("lakes")
    List<Holder<PlacedFeature>> getLakes();

}
