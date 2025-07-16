package dev.boxadactle.flatedit;

import dev.boxadactle.boxlib.config.BConfig;
import dev.boxadactle.boxlib.config.BConfigFile;
import dev.boxadactle.boxlib.gui.auto.CustomText;
import dev.boxadactle.boxlib.gui.auto.Gui;

@Gui("screen.flatedit.config")
@BConfigFile("flatedit")
public class ModConfig implements BConfig {

    @CustomText("screen.flatedit.config.enabled")
    public boolean enabled = true;

    @CustomText("screen.flatedit.config.showDefaultPresets")
    public boolean showDefaultPresets = true;

    @CustomText("screen.flatedit.config.confirmPresets")
    public boolean confirmPresets = true;

}
