package dev.boxadactle.flatedit;

import com.google.common.collect.ImmutableList;
import dev.boxadactle.boxlib.config.BConfigClass;
import dev.boxadactle.boxlib.config.BConfigHandler;
import dev.boxadactle.boxlib.gui.auto.AutoConfigGui;
import dev.boxadactle.boxlib.gui.config.BOptionScreen;
import dev.boxadactle.boxlib.util.ClientUtils;
import dev.boxadactle.boxlib.util.GuiUtils;
import dev.boxadactle.boxlib.util.ModLogger;
import dev.boxadactle.flatedit.json.FlatPreset;
import dev.boxadactle.flatedit.json.PresetParser;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FlatEdit {

    public static final String MOD_NAME = "FlatEdit";
    public static final String MOD_ID = "flatedit";
    public static final String MOD_VERSION = "4.0.0";
    public static final String VERSION_STRING = MOD_NAME + " v" + MOD_VERSION;

    public static final ModLogger LOGGER = new ModLogger(MOD_NAME);

    public static final Path PRESETS_PATH = Path.of(ClientUtils.getConfigFolder().toString(), "/" + MOD_ID + "-presets/");
    public static final String PRESETS_EXTENSION = ".flatedit";

    public static Identifier SLOT_SPRITE = Identifier.withDefaultNamespace("container/slot");

    public static BConfigClass<ModConfig> CONFIG;

    public static final List<Identifier> DEFAULT_PRESETS = ImmutableList.of(
            Identifier.fromNamespaceAndPath(MOD_ID, "presets/classic_flat.flatedit"),
            Identifier.fromNamespaceAndPath(MOD_ID, "presets/tunnelers_dream.flatedit"),
            Identifier.fromNamespaceAndPath(MOD_ID, "presets/water_world.flatedit"),
            Identifier.fromNamespaceAndPath(MOD_ID, "presets/overworld.flatedit"),
            Identifier.fromNamespaceAndPath(MOD_ID, "presets/snowy_kingdom.flatedit"),
            Identifier.fromNamespaceAndPath(MOD_ID, "presets/bottomless_pit.flatedit"),
            Identifier.fromNamespaceAndPath(MOD_ID, "presets/desert.flatedit"),
            Identifier.fromNamespaceAndPath(MOD_ID, "presets/redstone_ready.flatedit"),
            Identifier.fromNamespaceAndPath(MOD_ID, "presets/the_void.flatedit")
    );

    public static void init() {
        CONFIG = BConfigHandler.registerConfig(ModConfig.class);

        // create the presets folder if it doesn't exist
        if (!Files.exists(PRESETS_PATH)) {
            try {
                Files.createDirectory(PRESETS_PATH);
            } catch (java.io.IOException e) {
                LOGGER.error("Failed to create presets folder", e);
            }
        }

        LOGGER.info("Initialized " + VERSION_STRING);
    }

    public static ModConfig getConfig() {
        return CONFIG.get();
    }

    public static ItemStack getDisplayItem(BlockState arg) {
        Item item = arg.getBlock().asItem();
        if (item == Items.AIR) {
            if (arg.is(Blocks.WATER)) {
                item = Items.WATER_BUCKET;
            } else if (arg.is(Blocks.LAVA)) {
                item = Items.LAVA_BUCKET;
            } else if (arg.is(Blocks.AIR)) {
                item = Items.BARRIER;
            }
        }

        return new ItemStack(item);
    }

    public static ResourceKey<Biome> getBiome(String key) {
        Optional<ResourceKey<Biome>> thing = Optional.ofNullable(Identifier.tryParse(key)).map((resourceLocation) -> ResourceKey.create(Registries.BIOME, resourceLocation));
        Objects.requireNonNull(thing);

        return thing.get();
    }

    public static Path getDesktop() {
        try {
            return Path.of(System.getProperty("user.home"), "Desktop").toAbsolutePath();
        } catch (Exception e) {
            LOGGER.error("Failed to get desktop path", e);
            return Path.of(System.getProperty("user.home"));
        }
    }

    public static boolean canIAddMoreLayers(int currentLayers, int moreLayers) {
        return currentLayers + moreLayers <= 320 + 64;
    }

    public static int howManyMoreLayers(int currentLayers) {
        return 320 + 64 - currentLayers;
    }

    public static void checkLayers(int currentLayers, int moreLayers, Runnable runnable) {
        if (canIAddMoreLayers(currentLayers, moreLayers)) {
            runnable.run();
        } else {
            ClientUtils.showToast(
                    Component.translatable("message.flatedit.toomanylayers"),
                    Component.translatable("message.flatedit.toomanylayers.amount", howManyMoreLayers(currentLayers))
            );
        }
    }

    public static void exportPreset(Path path, FlatPreset preset, boolean showToast) {
        String presetString = PresetParser.serialize(preset);
        if (!path.endsWith(PRESETS_EXTENSION)) {
            path = path.resolveSibling(path.getFileName() + PRESETS_EXTENSION);
        }

        try {
            Files.writeString(path, presetString);
            LOGGER.info("Successfully exported preset to " + path);

            if (showToast) ClientUtils.showToast(
                    Component.translatable("message.flatedit.export"),
                    Component.translatable("message.flatedit.export.success", path.toString())
            );
        } catch (java.io.IOException e) {
            LOGGER.error("Failed to export preset to " + path, e);

            if (showToast) ClientUtils.showToast(
                    Component.translatable("message.flatedit.export"),
                    Component.translatable("message.flatedit.export.fail")
            );
        }
    }

    public static void importPreset(Path path) {
        try {
            // copy file into presets folder
            Path destination = PRESETS_PATH.resolve(path.getFileName());
            Files.copy(path, destination);

            LOGGER.info("Successfully imported preset: " + path.getFileName());

            ClientUtils.showToast(
                    Component.translatable("message.flatedit.import"),
                    Component.translatable("message.flatedit.import.success", path.getFileName().toString())
            );
        } catch (java.io.IOException e) {
            LOGGER.error("Failed to import preset from " + path, e);

            ClientUtils.showToast(
                    Component.translatable("message.flatedit.import"),
                    Component.translatable("message.flatedit.import.fail")
            );
        }
    }

    public static BOptionScreen createConfigScreen(Screen parent) {
        return AutoConfigGui.start(CONFIG.get(), parent)
                .setFooterProvider((layout, s) -> layout.addChild((new Button.Builder(GuiUtils.SAVE, (b) -> {
                        FlatEdit.CONFIG.save();
                        ClientUtils.setScreen(s);
                })).build()))
                .build();
    }
}
