package dev.boxadactle.flatedit.gui;

import dev.boxadactle.boxlib.gui.config.BOptionScreen;
import dev.boxadactle.boxlib.gui.config.BOptionTextField;
import dev.boxadactle.boxlib.gui.config.widget.BSpacingEntry;
import dev.boxadactle.boxlib.gui.config.widget.field.BStringField;
import dev.boxadactle.boxlib.gui.config.widget.label.BCenteredLabel;
import dev.boxadactle.boxlib.gui.config.widget.label.BLabel;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.flatedit.FlatEdit;
import dev.boxadactle.flatedit.FlatEditScreen;
import dev.boxadactle.flatedit.json.FlatPreset;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class ExportPresetScreen extends BOptionScreen {
    FlatPreset preset;

    String name = "";

    Path path;
    PathField field;

    public ExportPresetScreen(FlatEditScreen parent, FlatPreset preset) {
        super(parent);

        this.preset = preset;

        path = FlatEdit.getDesktop();
    }

    @Override
    protected Component getName() {
        return Component.translatable("screen.flatedit.export");
    }

    private void export() {
        preset.name = name;
        FlatEdit.exportPreset(path, preset, true);
        ClientUtils.setScreen(parent);
    }

    @Override
    protected void initFooter(int startX, int startY) {
        Button b = addRenderableWidget(setSaveButton(createHalfDoneButton(startX, startY, ignored -> export())));
        b.setMessage(Component.translatable("screen.flatedit.export.export"));

        addRenderableWidget(createHalfCancelButton(startX + getButtonWidth(ButtonType.SMALL) + getPadding(), startY, parent));
    }

    @Override
    protected void initConfigButtons() {
        addConfigLine(new BCenteredLabel(Component.translatable("screen.flatedit.export.export")));

        addConfigLine(new BSpacingEntry());

        field = new PathField(path, v -> path = v);
        field.setMaxLength(512);

        addConfigLine(new BCenteredLabel(Component.translatable("screen.flatedit.export.name")));
        addConfigLine(new PresetNameField(name, v -> {
            name = v;
            if (path != null && path.getFileName() != null) {
                path = path.resolveSibling(path.getParent().resolve(v.replaceAll(" ", "_") + FlatEdit.PRESETS_EXTENSION));
                field.setValue(field.from(path));
            }
        }));

        addConfigLine(new BSpacingEntry());

        addConfigLine(new BCenteredLabel(Component.translatable("screen.flatedit.export.location")));
        addConfigLine(field, new BLabel(Component.literal(FlatEdit.PRESETS_EXTENSION)));
    }

    public static class PresetNameField extends BStringField {
        public PresetNameField(String value, Consumer<String> function) {
            super(value, function);
        }

        @Override
        public String to(String input) {
            String a = super.to(input);
            setInvalid(a.isBlank() || a.contains("/") || a.contains("\\") || a.contains(":") || a.contains("*") || a.contains("?") || a.contains("\"") || a.contains("<") || a.contains(">") || a.contains("|"));
            return a;
        }
    }

    public static class PathField extends BOptionTextField<Path> {
        public PathField(Path value, Consumer<Path> function) {
            super(value, function);
        }

        public void valid() {
            setInvalid(false);
        }

        @Override
        public Path to(String input) {
            try {
                Path a = Path.of(input).toAbsolutePath();

                // make sure that the path follows a valid format
                setInvalid(a.getFileName() == null);

                return a;
            } catch (Exception ignored) {
                setInvalid(true);
                return null;
            }
        }

        @Override
        public String from(Path input) {
            return input.toString();
        }
    }
}
