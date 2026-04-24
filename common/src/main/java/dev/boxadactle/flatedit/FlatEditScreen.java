package dev.boxadactle.flatedit;

import dev.boxadactle.boxlib.gui.config.BConfigList;
import dev.boxadactle.boxlib.gui.config.BOptionButton;
import dev.boxadactle.boxlib.gui.config.BOptionScreen;
import dev.boxadactle.boxlib.gui.config.widget.button.BCustomButton;
import dev.boxadactle.boxlib.gui.config.widget.label.BLabel;
import dev.boxadactle.boxlib.gui.config.widget.label.BRightLabel;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.RenderUtils;
import dev.boxadactle.flatedit.gui.*;
import dev.boxadactle.flatedit.json.FlatLayer;
import dev.boxadactle.flatedit.json.FlatPreset;
import dev.boxadactle.flatedit.json.PresetParser;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.ArrayList;
import java.util.List;

public class FlatEditScreen extends BOptionScreen {

    WorldCreationContext context;

    public FlatPreset preset;

    public HolderGetter<Biome> biomes;
    public HolderGetter<StructureSet> structures;
    public HolderGetter<PlacedFeature> features;

    Button addLayersButton;

    public FlatEditScreen(CreateWorldScreen parent, WorldCreationContext context) {
        super(parent, Component.translatable("screen.flatedit.title"));

        this.context = context;

        ChunkGenerator generator = context.selectedDimensions().overworld();
        RegistryAccess registryAccess = context.worldgenLoadContext();
        biomes = registryAccess.lookupOrThrow(Registries.BIOME);
        structures = registryAccess.lookupOrThrow(Registries.STRUCTURE_SET);
        features = registryAccess.lookupOrThrow(Registries.PLACED_FEATURE);

        FlatLevelGeneratorSettings settings = generator instanceof FlatLevelSource ?
                 ((FlatLevelSource)generator).settings() :
                 FlatLevelGeneratorSettings.getDefault(biomes, structures, features);

        this.preset = FlatPreset.fromSettings(settings);

        String o = PresetParser.serialize(preset);
        FlatEdit.LOGGER.info(o);
        FlatEdit.LOGGER.info(PresetParser.deserialize(o, biomes));
    }

    @Override
    protected void addContents() {
        configList = new ResettableConfigList(ClientUtils.getClient(), this);
        if (shouldRenderScrollingWidget()) layout.addToContents(configList);

        addOptions();
    }

    public void reload() {
        ((ResettableConfigList)configList).clearEntries();
        addOptions();
    }

    @Override
    protected int getRowWidth() {
        return width - 50;
    }

    @Override
    protected int getFooterHeight() {
        return super.getFooterHeight() + getButtonHeight();
    }

    @Override
    protected int getScrollbarX() {
        return super.getScrollbarX() + 60;
    }

    @Override
    protected void initFooter(LinearLayout layout) {
        int p = getPadding();

        addLayersButton = addRenderableWidget(Button.builder(Component.literal("+"), (b) -> ClientUtils.setScreen(new SelectBlockScreen(this, (ignored, bl) -> ClientUtils.setScreen(new AddLayerScreen(this, bl))))).bounds(width - 22, 2, 20, 20).build());
        if (!FlatEdit.canIAddMoreLayers(preset.getCurrentLayers(), 1)) {
            addLayersButton.active = false;
            addLayersButton.setTooltip(Tooltip.create(Component.translatable("message.flatedit.toomanylayers")));
        }

        LinearLayout layout1 = layout.addChild(LinearLayout.vertical().spacing(p));

        LinearLayout things = layout1.addChild(LinearLayout.horizontal().spacing(p));

        things.addChild(Button.builder(Component.translatable("button.flatedit.presets"), (b) -> ClientUtils.setScreen(new PresetsScreen(this, biomes))).build());

        things.addChild(Button.builder(Component.translatable("screen.flatedit.worldsettings"), b -> ClientUtils.setScreen(new WorldSettingsScreen(this))).build());

        LinearLayout exit = layout1.addChild(LinearLayout.horizontal().spacing(p));

        exit.addChild(createDoneButton((b) -> {
            ClientUtils.setScreen(lastScreen);

            ((CreateWorldScreen) lastScreen).getUiState().updateDimensions(flatWorldConfigurator());
        }));

        exit.addChild(createCancelButton(lastScreen));

//        addRenderableWidget(Button.builder(Component.translatable("button.flatedit.preview"), b -> ClientUtils.setScreen(new PreviewScreen(preset, this))).bounds(p, p, getButtonWidth(ButtonType.TINY) - p, h).build());
    }

    private WorldCreationContext.DimensionsUpdater flatWorldConfigurator() {
        return (frozen, worldDimensions) -> {
            ChunkGenerator chunkGenerator = new FlatLevelSource(preset.toSettings(structures, features));
            FlatEdit.LOGGER.info(chunkGenerator);
            return worldDimensions.replaceOverworldGenerator(frozen, chunkGenerator);
        };
    }

    @Override
    protected void addOptions() {
        List<LayerEntry> entries = new ArrayList<>();
        for (int i = 0; i < preset.layers().size(); i++) {
            LayerEntry entry = new LayerEntry(preset.layers().get(i), i);
            entries.add(entry);
        }
        entries.reversed().forEach(this::addConfigLine);
        FlatEdit.LOGGER.info(preset.layers());
    }

    class LayerEntry extends BConfigList.ConfigEntry {
        FlatLayer layer;

        BCustomButton remove;
        BCustomButton edit;
        BCustomButton up;
        BCustomButton down;

        BLabel name;
        BLabel layers;

        public LayerEntry(FlatLayer layer, int index) {
            this.layer = layer;

            remove = new BCustomButton(Component.translatable("screen.flatedit.removelayer")) {
                @Override
                protected void buttonClicked(BOptionButton<?> button) {
                    FlatEditScreen.this.preset.layers.remove(index);
                    FlatEditScreen.this.reload();
                    FlatEdit.LOGGER.info(FlatEditScreen.this.preset.layers());
                }
            };

            edit = new BCustomButton(Component.translatable("screen.flatedit.editlayer")) {
                @Override
                protected void buttonClicked(BOptionButton<?> button) {
                    ClientUtils.setScreen(new EditLayerScreen(FlatEditScreen.this, index));
                }
            };

            up = new BCustomButton(Component.literal("▲")) {
                @Override
                protected void buttonClicked(BOptionButton<?> button) {
                    FlatEditScreen.this.preset.switchRows(index, index + 1);
                    FlatEditScreen.this.reload();
                }
            };

            down = new BCustomButton(Component.literal("▼")) {
                @Override
                protected void buttonClicked(BOptionButton<?> button) {
                    FlatEditScreen.this.preset.switchRows(index, index - 1);
                    FlatEditScreen.this.reload();
                }
            };

            name = new BLabel(layer.block().getName());

            if (index == FlatEditScreen.this.preset.layers().size() - 1) {
                layers = new BRightLabel(Component.translatable("createWorld.customize.flat.layer.top", layer.layers()));
                up.active = false;
            } else if (index == 0) {
                layers = new BRightLabel(Component.translatable("createWorld.customize.flat.layer.bottom", layer.layers()));
                down.active = false;
            } else {
                layers = new BRightLabel(Component.literal(Integer.toString(layer.layers())));
            }

            if (FlatEditScreen.this.preset.layers.size() == 1) {
                down.active = false;
                up.active = false;
            }

            if (FlatEditScreen.this.preset.layers().size() == 1) {
                remove.active = false;
                remove.setTooltip(Tooltip.create(Component.translatable("message.flatedit.onelayer")));
            }
        }

        @Override
        public List<? extends AbstractWidget> getWidgets() {
            return List.of(edit, remove, up, down);
        }

        @Override
        public boolean isInvalid() {
            return false;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor stack, int mouseX, int mouseY, boolean b, float tickDelta) {
            ItemStack item = FlatEdit.getDisplayItem(layer.getBlockState());

            int x= getX();
            int y = getY();
            int entryWidth = getContentWidth();


            stack.blitSprite(RenderPipelines.GUI_TEXTURED, FlatEdit.SLOT_SPRITE, x+1, y+1, 18, 18);
            if (!item.isEmpty()) {
                stack.fakeItem(item, x+2, y+2);
            }

            name.setX(x+ 30);
            name.setY(y + 2);
            name.extractRenderState(stack, mouseX, mouseY, tickDelta);

            int e = entryWidth - 50;

            remove.setX(e - getPadding());
            remove.setY(y + getPadding());
            remove.setWidth(getButtonWidth(ButtonType.TINY));
            remove.setHeight(getButtonHeight());
            remove.extractRenderState(stack, mouseX, mouseY, tickDelta);

            edit.setX(e - getPadding() - getButtonWidth(ButtonType.TINY) - getPadding());
            edit.setY(y + getPadding());
            edit.setWidth(getButtonWidth(ButtonType.TINY));
            edit.setHeight(getButtonHeight());
            edit.extractRenderState(stack, mouseX, mouseY, tickDelta);

            up.setX(e - getPadding() - getButtonWidth(ButtonType.TINY) - getPadding() - 22);
            up.setY(y + getPadding());
            up.setWidth(getButtonHeight());
            up.setHeight(getButtonHeight());
            up.extractRenderState(stack, mouseX, mouseY, tickDelta);

            down.setX(e - getPadding() - getButtonWidth(ButtonType.TINY) - getPadding() - 44);
            down.setY(y + getPadding());
            down.setWidth(getButtonHeight());
            down.setHeight(getButtonHeight());
            down.extractRenderState(stack, mouseX, mouseY, tickDelta);

            layers.setX(e - getPadding() - getButtonWidth(ButtonType.TINY) - getPadding() - 44 - 50);
            layers.setY(y + getPadding());
            layers.setWidth(45);
            layers.setHeight(getButtonHeight());
            layers.extractRenderState(stack, mouseX, mouseY, tickDelta);
        }
    }
}
