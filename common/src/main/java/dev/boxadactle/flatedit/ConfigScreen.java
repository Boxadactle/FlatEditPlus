package dev.boxadactle.flatedit;

import dev.boxadactle.boxlib.gui.config.BOptionScreen;
import dev.boxadactle.boxlib.gui.config.widget.BSpacingEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.util.ClientUtils;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static dev.boxadactle.flatedit.FlatEdit.getConfig;

public class ConfigScreen extends BOptionScreen {
    public ConfigScreen(Screen parent) {
        super(parent);

        FlatEdit.CONFIG.cacheConfig();
    }

    @Override
    protected Component getName() {
        return Component.translatable("screen.flatedit.config", FlatEdit.MOD_VERSION);
    }

    @Override
    protected void initFooter(int startX, int startY) {
        addRenderableWidget(setSaveButton(createHalfSaveButton(startX, startY, b -> {
            FlatEdit.CONFIG.save();
            ClientUtils.setScreen(parent);
        })));

        addRenderableWidget(createHalfCancelButton(startX, startY, p -> {
            FlatEdit.CONFIG.restoreCache();
            ClientUtils.setScreen(parent);
        }));
    }

    @Override
    protected void initConfigButtons() {
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
