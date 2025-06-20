package dev.boxadactle.flatedit;

import dev.boxadactle.boxlib.gui.config.BOptionScreen;
import dev.boxadactle.boxlib.gui.config.widget.BSpacingEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.util.ClientUtils;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static dev.boxadactle.flatedit.FlatEdit.getConfig;

public class ConfigScreen extends BOptionScreen {
    public ConfigScreen(Screen parent) {
        super(parent, Component.translatable("screen.flatedit.config", FlatEdit.MOD_VERSION));

        FlatEdit.CONFIG.cacheConfig();
    }

    @Override
    protected void initFooter(LinearLayout layout) {
        layout.addChild(setSaveButton(createSaveButton(b -> {
            FlatEdit.CONFIG.save();
            ClientUtils.setScreen(lastScreen);
        })));

        layout.addChild(createCancelButton(p -> {
            FlatEdit.CONFIG.restoreCache();
            ClientUtils.setScreen(lastScreen);
        }));
    }

    @Override
    protected void addOptions() {
        addConfigLine(new BSpacingEntry());

        addConfigLine(new BBooleanButton(
                "screen.flatedit.config.enabled",
                getConfig().enabled,
                b -> getConfig().enabled = b
        ));

        addConfigLine(new BBooleanButton(
                "screen.flatedit.config.showDefaultPresets",
                getConfig().showDefaultPresets,
                b -> getConfig().showDefaultPresets = b
        ));

        addConfigLine(new BBooleanButton(
                "screen.flatedit.config.confirmPresets",
                getConfig().confirmPresets,
                b -> getConfig().confirmPresets = b
        ));
    }
}
