package dev.boxadactle.neoforge;

import dev.boxadactle.flatedit.ConfigScreen;
import dev.boxadactle.flatedit.FlatEdit;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(FlatEdit.MOD_ID)
public class FlatEditNeoforge {

    public FlatEditNeoforge() {
        FlatEdit.init();

        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () ->
                (minecraft, screen) -> new ConfigScreen(screen)
        );
    }

}
