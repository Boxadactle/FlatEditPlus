package dev.boxadactle.flatedit.gui;

import dev.boxadactle.boxlib.gui.config.BOptionScreen;
import dev.boxadactle.boxlib.gui.config.widget.BSpacingEntry;
import dev.boxadactle.boxlib.gui.config.widget.label.BCenteredLabel;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.flatedit.FlatEdit;
import dev.boxadactle.flatedit.FlatEditScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ImportPresetScreen extends BOptionScreen {
    Path path;

    ExportPresetScreen.PathField field;

    public ImportPresetScreen(FlatEditScreen parent) {
        super(parent);

        path = FlatEdit.getDesktop();
    }

    @Override
    protected Component getName() {
        return Component.translatable("screen.flatedit.import");
    }

    @Override
    public void onFilesDrop(List<Path> list) {
        for (Path p : list) {
            if (p.toString().endsWith(FlatEdit.PRESETS_EXTENSION)) {
                path = p;
                field.setValue(field.from(p));
                field.valid();
                break;
            }
        }
    }

    private void importPreset() {
        if (
                path != null &&
                path.toFile().isFile() &&
                path.getFileName().toString().toLowerCase().endsWith(FlatEdit.PRESETS_EXTENSION) &&
                Files.exists(path)
        ) {
            FlatEdit.importPreset(path);
            ClientUtils.setScreen(parent);
        }
    }

    @Override
    protected void initFooter(int startX, int startY) {
        Button b = addRenderableWidget(setSaveButton(createHalfDoneButton(startX, startY, bu -> importPreset())));
        b.setMessage(Component.translatable("screen.flatedit.import.import"));

        addRenderableWidget(createHalfCancelButton(startX + getButtonWidth(ButtonType.SMALL) + getPadding(), startY, parent));
    }

    @Override
    protected void initConfigButtons() {
        addConfigLine(new BCenteredLabel(Component.translatable("screen.flatedit.import.import")));

        addConfigLine(new BSpacingEntry());

        addConfigLine(new BCenteredLabel(Component.translatable("screen.flatedit.import.location")));
        addConfigLine(new BCenteredLabel(Component.translatable("screen.flatedit.import.message")));
        addConfigLine(new BCenteredLabel(Component.translatable("screen.flatedit.import.message.2")));
        field = addConfigLine(new ExportPresetScreen.PathField(path, p -> path = p));
        field.setMaxLength(512);
    }
}
