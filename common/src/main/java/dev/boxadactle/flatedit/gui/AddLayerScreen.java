package dev.boxadactle.flatedit.gui;

import dev.boxadactle.boxlib.gui.config.BOptionScreen;
import dev.boxadactle.boxlib.gui.config.widget.BSpacingEntry;
import dev.boxadactle.boxlib.gui.config.widget.field.BIntegerField;
import dev.boxadactle.boxlib.gui.config.widget.label.BCenteredLabel;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.flatedit.FlatEdit;
import dev.boxadactle.flatedit.FlatEditScreen;
import dev.boxadactle.flatedit.json.FlatLayer;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class AddLayerScreen extends BOptionScreen {

    Block selectedBlock;

    int layers = 1;

    public AddLayerScreen(FlatEditScreen parent, Block selectedBlock) {
        super(parent, Component.translatable("screen.flatedit.addlayer", selectedBlock.getName()));

        this.selectedBlock = selectedBlock;
    }

    @Override
    protected void initFooter(LinearLayout layout) {
        setSaveButton(layout.addChild(createDoneButton((b -> {
            FlatEditScreen screen = (FlatEditScreen) lastScreen;
            FlatEdit.checkLayers(screen.preset.getCurrentLayers(), layers, () -> {
                screen.preset.layers().add(new FlatLayer(selectedBlock, layers));
                screen.reload();
                ClientUtils.setScreen(screen);
            });
        }))));

        layout.addChild(createCancelButton(lastScreen));
    }

    @Override
    protected void addOptions() {
        addConfigLine(new BSpacingEntry());
        addConfigLine(new BSpacingEntry());
        addConfigLine(new BSpacingEntry());

        addConfigLine(new BCenteredLabel(Component.translatable("screen.flatedit.edit.layers")));
        addConfigLine(new LayersField(layers, v -> layers = v));
    }

    static class LayersField extends BIntegerField {
        public LayersField(Integer value, Consumer<Integer> function) {
            super(value, function);
        }

        @Override
        public Integer handleInput(Integer input) {
            int a = super.handleInput(input);
            hasInvalidValue = a < 1;
            return a;
        }
    }
}
