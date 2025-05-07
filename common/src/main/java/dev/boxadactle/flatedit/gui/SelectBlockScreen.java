package dev.boxadactle.flatedit.gui;

import dev.boxadactle.boxlib.function.Consumer2;
import dev.boxadactle.boxlib.gui.config.BOptionButton;
import dev.boxadactle.boxlib.gui.config.BOptionScreen;
import dev.boxadactle.boxlib.gui.config.widget.button.BCustomButton;
import dev.boxadactle.boxlib.util.GuiUtils;
import dev.boxadactle.boxlib.util.RenderUtils;
import dev.boxadactle.flatedit.FlatEdit;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class SelectBlockScreen extends BOptionScreen {
    String search = "";

    Consumer2<Screen, Block> consumer;

    public SelectBlockScreen(Screen parent, Consumer2<Screen, Block> consumer) {
        super(parent);

        this.consumer = consumer;
    }

    @Override
    protected Component getName() {
        return Component.translatable("screen.flatedit.selectblock");
    }

    @Override
    protected void initFooter(int startX, int startY) {
        addRenderableWidget(createCancelButton(startX, startY, parent));

        EditBox searchField = addRenderableWidget(new EditBox(GuiUtils.getTextRenderer(), startX, 20, Component.translatable("screen.flatedit.selectblock.search")));
        searchField.setResponder(s -> {
            search = s;
            configList.children().clear();
            initConfigButtons();
        });
        searchField.setMaxLength(128);
        searchField.setX(startX);
        searchField.setY(20);
        searchField.setWidth(getButtonWidth(ButtonType.NORMAL));
    }

    @Override
    protected int getScrollingWidgetStart() {
        return super.getScrollingWidgetStart() + getButtonHeight() + getPadding();
    }

    @Override
    protected int getRowWidth() {
        return 250;
    }

    @Override
    protected int getRowHeight() {
        return 20;
    }

    @Override
    protected void initConfigButtons() {
        List<BlockButton> buttons = new ArrayList<>();

        for (Block value : BuiltInRegistries.BLOCK) {
            BlockState block = value.defaultBlockState();

            if (value.asItem() == Items.AIR && !block.isAir()) continue;

            if (!search.isBlank()) {
                String name = block.getBlock().getName().getString();
                if (!name.toLowerCase().contains(search.toLowerCase())) continue;
            }

            buttons.add(new BlockButton(block, parent));

            if (buttons.size() == 8) {
                addConfigLine(new BlockRow(buttons));
                buttons = new ArrayList<>();
            }
        }

        if (!buttons.isEmpty()) addConfigLine(new BlockRow(buttons));
    }

    public class BlockButton extends BCustomButton {
        BlockState block;

        Screen screen;

        public BlockButton(BlockState block, Screen screen) {
            super(null);

            this.block = block;

            this.screen = screen;

            setTooltip(Tooltip.create(block.getBlock().getName()));

            setMessage(block.getBlock().getName());
        }

        @Override
        public void renderWidget(GuiGraphics p_93657_, int mouseX, int mouseY, float delta) {
            if (isHovered()) {
                RenderUtils.drawSquare(p_93657_, getX(), getY(), getWidth(), getHeight(), 0x405c5c5c);
            }

            p_93657_.renderFakeItem(FlatEdit.getDisplayItem(block), getX() + 3, getY() + 3);
        }

        @Override
        protected void buttonClicked(BOptionButton<?> button) {
            SelectBlockScreen.this.consumer.accept(screen, block.getBlock());
        }
    }

    public class BlockRow extends ConfigList.ConfigEntry {

        List<BlockButton> buttons;

        public BlockRow(List<BlockButton> buttons) {
            this.buttons = buttons;
        }

        @Override
        public List<? extends AbstractWidget> getWidgets() {
            return new ArrayList<>(buttons);
        }

        @Override
        public boolean isInvalid() {
            return false;
        }

        @Override
        public void render(GuiGraphics stack, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            // render all buttons next to each other in the center of the row maintaining their width; button height always = entryHeight
            int buttonWidth = 20;
            int buttonSpacing = 2;
            int totalWidth = (buttonWidth * buttons.size()) + (buttonSpacing * (buttons.size() - 1));
            int startX = x + (entryWidth / 2) - (totalWidth / 2);
            for (int i = 0; i < buttons.size(); i++) {
                BlockButton button = buttons.get(i);
                button.setX(startX + (i * (buttonWidth + buttonSpacing)));
                button.setY(y);
                button.setWidth(buttonWidth);
                button.setHeight(entryHeight);
                button.render(stack, mouseX, mouseY, tickDelta);
            }
        }
    }
}
