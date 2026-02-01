package dev.boxadactle.flatedit.gui;

import dev.boxadactle.boxlib.function.Consumer2;
import dev.boxadactle.boxlib.gui.config.BConfigList;
import dev.boxadactle.boxlib.gui.config.BOptionButton;
import dev.boxadactle.boxlib.gui.config.BOptionScreen;
import dev.boxadactle.boxlib.gui.config.widget.button.BCustomButton;
import dev.boxadactle.boxlib.gui.widget.CenteredLabelWidget;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.GuiUtils;
import dev.boxadactle.boxlib.util.RenderUtils;
import dev.boxadactle.flatedit.FlatEdit;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class SelectBlockScreen extends BOptionScreen {
    String search = "";

    Consumer2<Screen, Block> consumer;

    public SelectBlockScreen(Screen parent, Consumer2<Screen, Block> consumer) {
        super(parent, Component.translatable("screen.flatedit.selectblock"));

        this.consumer = consumer;
    }

    @Override
    protected void addContents() {
        configList = new ResettableConfigList(ClientUtils.getClient(), this);
        if (shouldRenderScrollingWidget()) layout.addToContents(configList);

        addOptions();
    }

    @Override
    protected void addTitle() {
        LinearLayout title = layout.addToHeader(LinearLayout.vertical().spacing(getPadding()));

        title.addChild(new CenteredLabelWidget(0, 0, getButtonWidth(ButtonType.NORMAL), 20, this.title));

        EditBox searchField = title.addChild(new EditBox(GuiUtils.getTextRenderer(), 0, 20, Component.translatable("screen.flatedit.selectblock.search")));
        searchField.setResponder(s -> {
            search = s;
            ((ResettableConfigList) configList).clearEntries();
            addOptions();
        });
        searchField.setMaxLength(128);
        searchField.setWidth(getButtonWidth(ButtonType.NORMAL));
    }

    @Override
    protected void initFooter(LinearLayout layout) {
        layout.addChild(createCancelButton(lastScreen));
    }

    @Override
    protected int getHeaderHeight() {
        return super.getHeaderHeight() + getButtonHeight() + getPadding();
    }

    @Override
    protected int getRowWidth() {
        return 250;
    }

    @Override
    protected int getRowHeight() {
        return 30;
    }

    @Override
    protected void addOptions() {
        List<BlockButton> buttons = new ArrayList<>();

        for (Block value : BuiltInRegistries.BLOCK) {
            BlockState block = value.defaultBlockState();

            if (value.asItem() == Items.AIR && !block.isAir()) continue;

            if (!search.isBlank()) {
                String name = block.getBlock().getName().getString();
                if (!name.toLowerCase().contains(search.toLowerCase())) continue;
            }

            buttons.add(new BlockButton(block, lastScreen));

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
        protected void renderContents(GuiGraphics p_458247_, int p_457832_, int p_457537_, float p_457835_) {
            if (isHovered()) {
                RenderUtils.drawSquare(p_458247_, getX(), getY(), getWidth(), getHeight(), 0x405c5c5c);
            }

            p_458247_.renderFakeItem(FlatEdit.getDisplayItem(block), getX() + 3, getY() + 3);
        }

        @Override
        protected void buttonClicked(BOptionButton<?> button) {
            SelectBlockScreen.this.consumer.accept(screen, block.getBlock());
        }
    }

    public class BlockRow extends BConfigList.ConfigEntry {

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
        public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean b, float tickDelta) {
            // render all buttons next to each other in the center of the row maintaining their width; button height always = entryHeight
            int buttonWidth = 20;
            int buttonSpacing = 2;
            int totalWidth = (buttonWidth * buttons.size()) + (buttonSpacing * (buttons.size() - 1));
            int startX = getContentX() + (getContentWidth() / 2) - (totalWidth / 2);
            for (int i = 0; i < buttons.size(); i++) {
                BlockButton button = buttons.get(i);
                button.setX(startX + (i * (buttonWidth + buttonSpacing)));
                button.setY(getContentY());
                button.setWidth(buttonWidth);
                button.setHeight(getContentHeight());
                button.render(guiGraphics, mouseX, mouseY, tickDelta);
            }
        }
    }
}
