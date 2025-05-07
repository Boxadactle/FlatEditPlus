package dev.boxadactle.flatedit;

import dev.boxadactle.boxlib.config.BConfig;
import dev.boxadactle.boxlib.config.BConfigFile;

@BConfigFile("flatedit")
public class ModConfig implements BConfig {

    public boolean enabled = true;

    public boolean showDefaultPresets = true;

    public boolean confirmPresets = true;

}
