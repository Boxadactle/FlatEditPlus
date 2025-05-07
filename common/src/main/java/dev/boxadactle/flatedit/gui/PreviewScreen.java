package dev.boxadactle.flatedit.gui;

import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.GuiUtils;
import dev.boxadactle.boxlib.util.RenderUtils;
import dev.boxadactle.flatedit.FlatEditScreen;
import dev.boxadactle.flatedit.json.FlatLayer;
import dev.boxadactle.flatedit.json.FlatPreset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PreviewScreen extends Screen {
    List<FlatLayer> layers;

    Previewer previewer;

    FlatEditScreen screen;

    public PreviewScreen(FlatPreset preset, FlatEditScreen screen) {
        super(Component.translatable("screen.flatedit.presets"));

        layers = preset.layers();

        this.screen = screen;
    }

    @Override
    protected void init() {
        super.init();

        previewer = new Previewer(ClientUtils.getClient());
        addRenderableWidget(previewer);

        int skies = 5;

        for (int i = 0; i < skies; i++) {
            previewer.addEntry(new AirLayer(false));
        }

        for (FlatLayer layer : layers) {
            for (int i = 0; i < layer.layers(); i++) {
                previewer.addEntryToTop(new BlockLayer(layer));
            }
        }

        for (int i = 0; i < skies; i++) {
            previewer.addEntryToTop(new AirLayer(true));
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);

        previewer.render(guiGraphics, i, j, f);
    }

    @Override
    public void onClose() {
        ClientUtils.setScreen(screen);
    }

    class Previewer extends ContainerObjectSelectionList<LayerEntry> {
        public Previewer(Minecraft minecraft) {
            super(
                    minecraft,
                    PreviewScreen.this.width,
                    PreviewScreen.this.height,
                    0,
                    16
            );
        }

        @Override
        public int getRowWidth() {
            return PreviewScreen.this.width;
        }

        @Override
        protected boolean scrollbarVisible() {
            return false;
        }

        @Override
        public int addEntry(LayerEntry entry) {
            return super.addEntry(entry);
        }

        @Override
        public void addEntryToTop(LayerEntry entry) {
            super.addEntryToTop(entry);
        }
    }

    static abstract class LayerEntry extends ContainerObjectSelectionList.Entry<LayerEntry> {
        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return List.of();
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return List.of();
        }
    }

    static class BlockLayer extends LayerEntry {
        FlatLayer layer;

        public BlockLayer(FlatLayer layer) {
            this.layer = layer;
        }

        @Override
        public void render(GuiGraphics p_93523_, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            p_93523_.drawString(
                    Minecraft.getInstance().font,
                    layer.block().getName(),
                    x + 2,
                    y + 2,
                    GuiUtils.WHITE
            );

        }
    }

    static class AirLayer extends LayerEntry {
        boolean isSkyLight;

        public AirLayer(boolean isSkyLight) {
            this.isSkyLight = isSkyLight;
        }

        @Override
        public void render(GuiGraphics p_93523_, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            int skyColor = 0x7faaf0;
            int voidColor = 0x171717;
            int color = isSkyLight ? skyColor : voidColor;

            RenderUtils.drawSquare(p_93523_, x, y, entryWidth, entryHeight, color);
        }
    }
}
