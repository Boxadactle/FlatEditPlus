package dev.boxadactle.flatedit.fabric;

import dev.boxadactle.flatedit.FlatEdit;
import net.fabricmc.api.ClientModInitializer;

public class FlatEditFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FlatEdit.init();
    }
}
