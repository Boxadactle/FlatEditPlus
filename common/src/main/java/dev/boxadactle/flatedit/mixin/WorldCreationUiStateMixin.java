package dev.boxadactle.flatedit.mixin;

import dev.boxadactle.flatedit.FlatEdit;
import dev.boxadactle.flatedit.FlatEditScreen;
import net.minecraft.client.gui.screens.worldselection.PresetEditor;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldCreationUiState.class)
public abstract class WorldCreationUiStateMixin {

    @Shadow public abstract WorldCreationUiState.WorldTypeEntry getWorldType();

    @Inject(method = "getPresetEditor", at = @At("HEAD"), cancellable = true)
    public void getPresetEditor(CallbackInfoReturnable<PresetEditor> cir) {
        Holder<WorldPreset> holder = getWorldType().preset();

        if (holder != null && holder.is(WorldPresets.FLAT) && FlatEdit.getConfig().enabled) {
            cir.setReturnValue(FlatEditScreen::new);
        }
    }

}
