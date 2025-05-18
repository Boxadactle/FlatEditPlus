package dev.boxadactle.flatedit.gui;

import dev.boxadactle.boxlib.gui.config.BOptionScreen;
import dev.boxadactle.boxlib.gui.config.widget.BSpacingEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BCustomButton;
import dev.boxadactle.boxlib.gui.config.widget.label.BCenteredLabel;
import dev.boxadactle.boxlib.gui.config.widget.label.BLabel;
import dev.boxadactle.boxlib.prompt.Prompts;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.RenderUtils;
import dev.boxadactle.flatedit.FlatEdit;
import dev.boxadactle.flatedit.FlatEditScreen;
import dev.boxadactle.flatedit.json.FlatPreset;
import dev.boxadactle.flatedit.json.PresetParser;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.HolderGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PresetsScreen extends BOptionScreen {
    HolderGetter<Biome> biomes;

    public PresetsScreen(FlatEditScreen parent, HolderGetter<Biome> biomes) {
        super(parent);

        this.biomes = biomes;
    }

    @Override
    protected Component getName() {
        return Component.translatable("screen.flatedit.presets");
    }

    @Override
    protected void initFooter(int startX, int startY) {
        addRenderableWidget(createCancelButton(startX, startY, parent));

        addRenderableWidget(Button.builder(
                Component.translatable("screen.flatedit.presets.export"),
                b -> ClientUtils.setScreen(new ExportPresetScreen((FlatEditScreen) parent, ((FlatEditScreen) parent).preset))
        ).bounds(startX, startY - 22, getButtonWidth(ButtonType.SMALL), getButtonHeight()).build());

        addRenderableWidget(Button.builder(
                Component.translatable("screen.flatedit.presets.import"),
                b -> ClientUtils.setScreen(new ImportPresetScreen((FlatEditScreen) parent))
        ).bounds(startX + getButtonWidth(ButtonType.SMALL) + getPadding() * 2, startY - 22, getButtonWidth(ButtonType.SMALL), getButtonHeight()).build());

        addRenderableWidget(Button.builder(
                Component.translatable("screen.flatedit.presets.save"),
                b -> Prompts.prompt(
                        parent,
                        Component.translatable("screen.flatedit.export.name"),
                        name -> {
                            if (name == null || name.isBlank()) {
                                return;
                            }
                            FlatPreset preset = ((FlatEditScreen) parent).preset;
                            preset.name = name;
                            FlatEdit.exportPreset(FlatEdit.PRESETS_PATH.resolve(UUID.randomUUID() + FlatEdit.PRESETS_EXTENSION), preset, false);
                        }
                )
        ).bounds(startX, 20, getButtonWidth(ButtonType.NORMAL), getButtonHeight()).build());
    }

    @Override
    protected int getScrollingWidgetStart() {
        return super.getScrollingWidgetStart() + 24;
    }

    @Override
    protected int getScrollingWidgetEnd() {
        return super.getScrollingWidgetEnd() - 20;
    }

    @Override
    protected int getRowWidth() {
        return super.getRowWidth() + 150;
    }

    @Override
    protected int getScrollbarX() {
        return width - 25;
    }

    private Optional<FlatPreset> readDefaultPreset(ResourceLocation location) {
        Optional<Resource> r = ClientUtils.getClient().getResourceManager().getResource(location);

        if (r.isPresent()) {
            try {
                return Optional.ofNullable(PresetParser.deserialize(new InputStreamReader(r.get().open()), biomes));
            } catch (Exception e) {
                FlatEdit.LOGGER.error("Failed to read preset from " + location, e);
                return Optional.empty();
            }
        } else {
            FlatEdit.LOGGER.error("Failed to find preset at " + location);
            return Optional.empty();
        }
    }

    @Override
    protected void initConfigButtons() {
        if (FlatEdit.getConfig().showDefaultPresets) {
            addConfigLine(new BCenteredLabel(Component.translatable("screen.flatedit.presets.default")));
            for (ResourceLocation location : FlatEdit.DEFAULT_PRESETS) {
                Optional<FlatPreset> preset = readDefaultPreset(location);
                if (preset.isPresent()) {
                    addConfigLine(new PresetRow(preset.get(), true, null));
                } else {
                    FlatEdit.LOGGER.error("Failed to read default preset from " + location);
                }
            }

            addConfigLine(new BCenteredLabel(Component.translatable("screen.flatedit.presets.custom")));
        }

        File[] files = FlatEdit.PRESETS_PATH.toFile().listFiles();
        if (files == null || files.length == 0) {
            addConfigLine(new BSpacingEntry());
            addConfigLine(new BCenteredLabel(Component.translatable("screen.flatedit.presets.none")));
        } else {
            for (File presetFile : files) {
                try {
                    if (presetFile.isFile() && presetFile.getName().endsWith(FlatEdit.PRESETS_EXTENSION)) {
                        String filename = presetFile.getName();
                        FlatPreset preset = PresetParser.deserialize(new BufferedReader(new FileReader(presetFile)), biomes);

                        addConfigLine(new PresetRow(preset, false, filename));
                    }
                } catch (Exception e) {
                    ClientUtils.showToast(
                            Component.translatable("screen.flatedit.presets.loaderror"),
                            Component.translatable("screen.flatedit.presets.loaderror.message")
                    );

                    FlatEdit.LOGGER.info("Unable to read file " + presetFile.getAbsolutePath());
                    FlatEdit.LOGGER.printStackTrace(e);
                }
            }
        }
    }

    public class PresetRow extends ConfigList.ConfigEntry {

        FlatPreset preset;

        BLabel nameLabel;

        BCustomButton useButton;
        BCustomButton previewButton;
        BCustomButton deleteButton;

        boolean isDefault;

        public PresetRow(FlatPreset preset, boolean isDefault, @Nullable String filename) {
            this.preset = preset;
            this.isDefault = isDefault;

            nameLabel = new BLabel(Component.literal(preset.name()));

            Runnable setPreset = () -> {
                FlatEditScreen screen = (FlatEditScreen) parent;
                screen.preset = this.preset;
                screen.reload();
                ClientUtils.showToast(
                        Component.translatable("screen.flatedit.presets.use.toast", preset.name()),
                        Component.translatable("screen.flatedit.presets.use.toast.message")
                );

            };
            useButton = BCustomButton.create(
                    Component.translatable("screen.flatedit.presets.use"),
                    () -> {
                        if (FlatEdit.getConfig().confirmPresets) {
                            Prompts.confirm(parent, Component.translatable("screen.flatedit.presets.use.confirm"), bl -> {
                                if (bl) {
                                    setPreset.run();
                                    ClientUtils.setScreen(parent);
                                } else {
                                    ClientUtils.setScreen(PresetsScreen.this);
                                }
                            });
                        } else setPreset.run();
                    }
            );

            Runnable deletePreset = () -> {
                Path path = Path.of(FlatEdit.PRESETS_PATH.toString(), filename);
                if (path.toFile().exists()) {
                    path.toFile().delete();
                }

                ClientUtils.showToast(
                        Component.translatable("screen.flatedit.presets.delete.toast", preset.name()),
                        Component.translatable("screen.flatedit.presets.delete.toast.message")
                );
            };
            deleteButton = BCustomButton.create(
                    Component.translatable("screen.flatedit.presets.delete"),
                    () -> {
                        if (FlatEdit.getConfig().confirmPresets) {
                            Prompts.confirm(parent, Component.translatable("screen.flatedit.presets.delete.confirm"), bl -> {
                                if (bl) {
                                    deletePreset.run();
                                    ClientUtils.setScreen(PresetsScreen.this);
                                } else {
                                    ClientUtils.setScreen(PresetsScreen.this);
                                }
                            });
                        } else {
                            deletePreset.run();
                            ClientUtils.setScreen(PresetsScreen.this);
                        }
                    }
            );

            if (isDefault) {
                deleteButton.active = false;
                deleteButton.setTooltip(Tooltip.create(Component.translatable("screen.flatedit.presets.delete.default")));
            }
        }

        @Override
        public List<? extends AbstractWidget> getWidgets() {
            return List.of(useButton, deleteButton);
        }

        @Override
        public boolean isInvalid() {
            return false;
        }

        @Override
        public void render(GuiGraphics stack, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            ItemStack item = FlatEdit.getDisplayItem(preset.layers.getLast().getBlockState());

            stack.blitSprite(RenderType::guiTextured, FlatEdit.SLOT_SPRITE, x+1, y+1, 18, 18);
            if (!item.isEmpty()) {
                stack.renderFakeItem(item, x+2, y+2);
            }

            nameLabel.setX(x + 24);
            nameLabel.setY(y + 2);
            nameLabel.setWidth(100);
            nameLabel.setHeight(entryHeight - 4);
            nameLabel.render(stack, mouseX, mouseY, tickDelta);

            useButton.setX(entryWidth - getButtonWidth(ButtonType.TINY) - getPadding());
            useButton.setY(y + 2);
            useButton.setWidth(getButtonWidth(ButtonType.TINY));
            useButton.render(stack, mouseX, mouseY, tickDelta);

            deleteButton.setX(entryWidth - getButtonWidth(ButtonType.TINY) - getPadding() - getButtonWidth(ButtonType.TINY) - getPadding());
            deleteButton.setY(y + 2);
            deleteButton.setWidth(getButtonWidth(ButtonType.TINY));
            deleteButton.render(stack, mouseX, mouseY, tickDelta);
        }
    }
}
