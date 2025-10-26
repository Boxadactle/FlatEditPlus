package dev.boxadactle.flatedit.gui;

import dev.boxadactle.boxlib.gui.config.BConfigList;
import dev.boxadactle.boxlib.gui.config.BOptionScreen;
import net.minecraft.client.Minecraft;

public class ResettableConfigList extends BConfigList {
    /**
     * Constructs a BConfigList with the specified Minecraft instance and BOptionScreen.
     *
     * @param minecraft The Minecraft instance.
     * @param screen    The BOptionScreen that this list belongs to.
     */
    public ResettableConfigList(Minecraft minecraft, BOptionScreen screen) {
        super(minecraft, screen);
    }

    @Override
    public void clearEntries() {
        super.clearEntries();
    }
}
