package dev.boxadactle.flatedit.gui;

import dev.boxadactle.boxlib.gui.config.BOptionScreen;
import dev.boxadactle.boxlib.gui.config.BOptionTextField;
import dev.boxadactle.boxlib.gui.config.widget.BSpacingEntry;
import dev.boxadactle.boxlib.gui.config.widget.button.BBooleanButton;
import dev.boxadactle.boxlib.gui.config.widget.button.BScreenButton;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.flatedit.FlatEdit;
import dev.boxadactle.flatedit.FlatEditScreen;
import dev.boxadactle.flatedit.json.FlatFeatures;
import dev.boxadactle.flatedit.json.FlatStructures;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.function.Consumer;

public class WorldSettingsScreen extends BOptionScreen {
    public WorldSettingsScreen(FlatEditScreen parent) {
        super(parent, Component.translatable("screen.flatedit.world"));
    }

    @Override
    protected void initFooter(LinearLayout layout) {
        layout.addChild(createDoneButton(lastScreen));
    }

    @Override
    protected void addOptions() {
        FlatEditScreen screen = (FlatEditScreen) lastScreen;

        BBooleanButton deco = addConfigLine(new BBooleanButton(
                "screen.flatedit.world.decorations",
                screen.preset.decorations,
                v -> screen.preset.decorations = v
        ));
        deco.setTooltip(Tooltip.create(Component.translatable("screen.flatedit.world.decorations.tooltip")));

        BBooleanButton addLakes = addConfigLine(new BBooleanButton(
                "screen.flatedit.world.addLakes",
                screen.preset.addLakes,
                v -> screen.preset.addLakes = v
        ));
        addLakes.setTooltip(Tooltip.create(Component.translatable("screen.flatedit.world.addLakes.tooltip")));

        addConfigLine(new BScreenButton(Component.translatable("screen.flatedit.world.biome"), this, (p) -> new BiomeSelection(p, screen.biomes)));

        addConfigLine(new BScreenButton(Component.translatable("screen.flatedit.world.features"), this, FeatureSelection::new));

        addConfigLine(new BScreenButton(Component.translatable("screen.flatedit.world.structures"), this, StructureSelection::new));
    }

    private String capitalize(String id) {
        String[] parts = id.split("_");
        StringBuilder capitalized = new StringBuilder();
        for (String part : parts) {
            capitalized.append(part.substring(0, 1).toUpperCase()).append(part.substring(1)).append(" ");
        }
        return capitalized.toString().trim();
    }

    class BiomeSelection extends BOptionScreen {
        Holder<Biome> biome;

        HolderGetter<Biome> biomes;

        public BiomeSelection(Screen parent, HolderGetter<Biome> biomes) {
            super(parent, Component.translatable("screen.flatedit.world.biome"));

            biome = ((FlatEditScreen) WorldSettingsScreen.this.lastScreen).preset.biome;
            this.biomes = biomes;
        }

        @Override
        protected void initFooter(LinearLayout layout) {
            layout.addChild(setSaveButton(createDoneButton(b -> {
                ((FlatEditScreen) WorldSettingsScreen.this.lastScreen).preset.biome = biome;

                ClientUtils.setScreen(lastScreen);
            })));
        }

        @Override
        protected void addOptions() {
            addConfigLine(new BSpacingEntry());
            addConfigLine(new BSpacingEntry());
            addConfigLine(new BSpacingEntry());
            addConfigLine(new BiomeEntry(
                    biome,
                    b -> biome = b,
                    biomes
            ));
        }

        static class BiomeEntry extends BOptionTextField<Holder<Biome>> {
            HolderGetter<Biome> biomes;

            public BiomeEntry(Holder<Biome> value, Consumer<Holder<Biome>> function, HolderGetter<Biome> biomes) {
                super(value, function);

                this.biomes = biomes;

                setInvalid(false);
            }

            @Override
            public Holder<Biome> to(String input) {
                try {
                    ResourceKey<Biome> biomeKey = FlatEdit.getBiome(input);
                    Holder<Biome> biome = biomes.getOrThrow(biomeKey);

                    setInvalid(false);

                    return biome;
                } catch (Exception ignored) {
                    setInvalid(true);
                    return null;
                }
            }

            @Override
            public String from(Holder<Biome> input) {
                return input.unwrapKey().get().location().toString();
            }
        }
    }

    class FeatureSelection extends BOptionScreen {

        public FeatureSelection(Screen parent) {
            super(parent, Component.translatable("screen.flatedit.world.features"));
        }

        @Override
        protected void initFooter(LinearLayout layout) {
            layout.addChild(createDoneButton(lastScreen));
        }

        @Override
        protected void addOptions() {
            FlatEditScreen screen = (FlatEditScreen) WorldSettingsScreen.this.lastScreen;

            for (FlatFeatures feature : FlatFeatures.values()) {
                addConfigLine(new BBooleanButton(
                        capitalize(feature.getId()) + ": %s",
                        screen.preset.features.contains(feature),
                        bl -> {
                            if (bl) {
                                screen.preset.features.add(feature);
                            } else {
                                screen.preset.features.remove(feature);
                            }
                        }
                ));
            }
        }
    }

    class StructureSelection extends BOptionScreen {
        public StructureSelection(Screen parent) {
            super(parent, Component.translatable("screen.flatedit.world.structures"));
        }

        @Override
        protected void initFooter(LinearLayout layout) {
            layout.addChild(createDoneButton(lastScreen));
        }

        @Override
        protected void addOptions() {
            FlatEditScreen screen = (FlatEditScreen) WorldSettingsScreen.this.lastScreen;

            for (FlatStructures structure : FlatStructures.values()) {
                addConfigLine(new BBooleanButton(
                        capitalize(structure.getId()) + ": %s",
                        screen.preset.structures.contains(structure),
                        bl -> {
                            if (bl) {
                                screen.preset.structures.add(structure);
                            } else {
                                screen.preset.structures.remove(structure);
                            }
                        }
                ));
            }
        }
    }
}
