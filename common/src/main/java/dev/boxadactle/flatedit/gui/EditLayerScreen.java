package dev.boxadactle.flatedit.gui;

import dev.boxadactle.boxlib.gui.config.BOptionScreen;
import dev.boxadactle.boxlib.gui.config.widget.BSpacingEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BConfigScreenButton;
import dev.boxadactle.boxlib.gui.config.widget.field.BIntegerField;
import dev.boxadactle.boxlib.gui.config.widget.label.BCenteredLabel;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.flatedit.FlatEdit;
import dev.boxadactle.flatedit.FlatEditScreen;
import dev.boxadactle.flatedit.json.FlatLayer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class EditLayerScreen extends BOptionScreen {
    int index;
    FlatLayer layer;

    public EditLayerScreen(Screen parent, int index) {
        super(parent);

        this.index = index;
        this.layer = ((FlatEditScreen) parent).preset.layers().get(index);
    }

    @Override
    protected Component getName() {
        return Component.translatable("screen.flatedit.edit");
    }

    @Override
    protected void initFooter(int startX, int startY) {
        addRenderableWidget(setSaveButton(createHalfDoneButton(startX, startY, ignored -> {
            FlatEditScreen screen = (FlatEditScreen) parent;
            FlatEdit.checkLayers(screen.preset.getCurrentLayers(), layer.layers() - screen.preset.layers.get(index).layers(), () -> {
                screen.preset.layers().set(index, layer);
                ClientUtils.setScreen(screen);
            });
        })));

        addRenderableWidget(createHalfCancelButton(startX + getButtonWidth(ButtonType.SMALL) + getPadding(), startY, parent));
    }

    @Override
    protected void initConfigButtons() {
        addConfigLine(new BSpacingEntry());

        addConfigLine(new BCenteredLabel(Component.translatable("screen.flatedit.edit.block", layer.block().getName().getString())));
        addConfigLine(new BConfigScreenButton(Component.translatable("screen.flatedit.edit.changeblock"), this, (p) -> new SelectBlockScreen(p, (parent, block) -> {
            layer = new FlatLayer(block, layer.layers());
            ClientUtils.setScreen(parent);
        })));

        addConfigLine(new BSpacingEntry());

        addConfigLine(new BCenteredLabel(Component.translatable("screen.flatedit.edit.layers")));
        addConfigLine(new BIntegerField(layer.layers(), i-> layer = new FlatLayer(layer.block(), i)));
    }
}
