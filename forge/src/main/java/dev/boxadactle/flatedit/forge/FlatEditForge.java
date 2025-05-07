package dev.boxadactle.flatedit.forge;

import dev.boxadactle.flatedit.ConfigScreen;
import dev.boxadactle.flatedit.FlatEdit;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod(FlatEdit.MOD_ID)
public class FlatEditForge {

    public FlatEditForge() {
        FlatEdit.init();

        ModList.get().getModContainerById(FlatEdit.MOD_ID).ifPresent(modContainer -> modContainer.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
                new ConfigScreenHandler.ConfigScreenFactory(((minecraft, screen) -> new ConfigScreen(screen)))
        ));
    }

}
