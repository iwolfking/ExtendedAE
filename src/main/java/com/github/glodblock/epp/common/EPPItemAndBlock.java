package com.github.glodblock.epp.common;

import appeng.items.parts.PartItem;
import com.github.glodblock.epp.common.blocks.BlockExDrive;
import com.github.glodblock.epp.common.blocks.BlockExInterface;
import com.github.glodblock.epp.common.blocks.BlockExPatternProvider;
import com.github.glodblock.epp.common.blocks.BlockIngredientBuffer;
import com.github.glodblock.epp.common.blocks.BlockWirelessConnector;
import com.github.glodblock.epp.common.blocks.BlockWirelessHub;
import com.github.glodblock.epp.common.blocks.BlockExMolecularAssembler;
import com.github.glodblock.epp.common.blocks.matrix.BlockAssemblerMatrixBase;
import com.github.glodblock.epp.common.blocks.matrix.BlockAssemblerMatrixCrafter;
import com.github.glodblock.epp.common.blocks.matrix.BlockAssemblerMatrixFrame;
import com.github.glodblock.epp.common.blocks.matrix.BlockAssemblerMatrixGlass;
import com.github.glodblock.epp.common.blocks.matrix.BlockAssemblerMatrixPattern;
import com.github.glodblock.epp.common.blocks.matrix.BlockAssemblerMatrixSpeed;
import com.github.glodblock.epp.common.blocks.matrix.BlockAssemblerMatrixWall;
import com.github.glodblock.epp.common.items.*;
import com.github.glodblock.epp.common.parts.*;
import com.github.glodblock.epp.common.tileentities.TileExDrive;
import com.github.glodblock.epp.common.tileentities.TileExInterface;
import com.github.glodblock.epp.common.tileentities.TileExPatternProvider;
import com.github.glodblock.epp.common.tileentities.TileIngredientBuffer;
import com.github.glodblock.epp.common.tileentities.TileWirelessConnector;
import com.github.glodblock.epp.common.tileentities.TileWirelessHub;
import com.github.glodblock.epp.common.tileentities.TileExMolecularAssembler;
import com.github.glodblock.epp.common.tileentities.matrix.TileAssemblerMatrixCrafter;
import com.github.glodblock.epp.common.tileentities.matrix.TileAssemblerMatrixFrame;
import com.github.glodblock.epp.common.tileentities.matrix.TileAssemblerMatrixGlass;
import com.github.glodblock.epp.common.tileentities.matrix.TileAssemblerMatrixPattern;
import com.github.glodblock.epp.common.tileentities.matrix.TileAssemblerMatrixSpeed;
import com.github.glodblock.epp.common.tileentities.matrix.TileAssemblerMatrixWall;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;

public class EPPItemAndBlock {

    public static final CreativeModeTab TAB = new CreativeModeTab("epp") {
        @Nonnull
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(EX_PATTERN_PROVIDER);
        }
    };

    public static BlockExPatternProvider EX_PATTERN_PROVIDER;
    public static PartItem<PartExPatternProvider> EX_PATTERN_PROVIDER_PART;
    public static PartItem<AdvancedPatternAccessTerminalPart> EX_PATTERN_ACCESS_TERMINAL;
    public static BlockExInterface EX_INTERFACE;
    public static PartItem<PartExInterface> EX_INTERFACE_PART;
    public static InfinityCell INFINITY_CELL;
    public static PartItem<PartExExportBus> EX_EXPORT_BUS;
    public static PartItem<PartPreciseExportBus> PRECISE_EXPORT_BUS;
    public static PartItem<PartExImportBus> EX_IMPORT_BUS;
    public static ItemPatternProviderUpgrade PATTERN_PROVIDER_UPGRADE;
    public static ItemInterfaceUpgrade INTERFACE_UPGRADE;
    public static ItemIOBusUpgrade IO_BUS_UPGRADE;
    public static ItemMEPackingTape PACKING_TAPE;
    public static ItemPackedDevice PACKAGE;
    public static BlockExDrive EX_DRIVE;
    public static ItemDriveUpgrade DRIVE_UPGRADE;
    public static BlockIngredientBuffer INGREDIENT_BUFFER;
    public static BlockWirelessConnector WIRELESS_CONNECTOR;
    public static BlockWirelessHub WIRELESS_HUB;
    public static BlockExMolecularAssembler EX_ASSEMBLER;
    public static ItemWirelessConnectTool WIRELESS_TOOL;
    public static ItemWirelessAdvancedConnectTool WIRELESS_ADVANCED_TOOL;
    public static BlockAssemblerMatrixFrame ASSEMBLER_MATRIX_FRAME;
    public static BlockAssemblerMatrixGlass ASSEMBLER_MATRIX_GLASS;
    public static BlockAssemblerMatrixWall ASSEMBLER_MATRIX_WALL;
    public static BlockAssemblerMatrixPattern ASSEMBLER_MATRIX_PATTERN;
    public static BlockAssemblerMatrixCrafter ASSEMBLER_MATRIX_CRAFTER;
    public static BlockAssemblerMatrixSpeed ASSEMBLER_MATRIX_SPEED;

    public static void init(RegistryHandler regHandler) {
        EX_PATTERN_PROVIDER = new BlockExPatternProvider();
        EX_PATTERN_PROVIDER_PART = new PartItem<>(new Item.Properties().tab(TAB), PartExPatternProvider.class, PartExPatternProvider::new);
        EX_PATTERN_ACCESS_TERMINAL = new PartItem<>(new Item.Properties().tab(TAB), AdvancedPatternAccessTerminalPart.class, AdvancedPatternAccessTerminalPart::new);
        EX_INTERFACE = new BlockExInterface();
        EX_INTERFACE_PART = new PartItem<>(new Item.Properties().tab(TAB), PartExInterface.class, PartExInterface::new);
        INFINITY_CELL = new InfinityCell();
        EX_EXPORT_BUS = new PartItem<>(new Item.Properties().tab(TAB), PartExExportBus.class, PartExExportBus::new);
        PRECISE_EXPORT_BUS = new PartItem<>(new Item.Properties().tab(TAB), PartPreciseExportBus.class, PartPreciseExportBus::new);
        EX_IMPORT_BUS = new PartItem<>(new Item.Properties().tab(TAB), PartExImportBus.class, PartExImportBus::new);
        IO_BUS_UPGRADE = new ItemIOBusUpgrade();
        INTERFACE_UPGRADE = new ItemInterfaceUpgrade();
        PATTERN_PROVIDER_UPGRADE = new ItemPatternProviderUpgrade();
        PACKING_TAPE = new ItemMEPackingTape();
        PACKAGE = new ItemPackedDevice();
        EX_DRIVE = new BlockExDrive();
        DRIVE_UPGRADE = new ItemDriveUpgrade();
        INGREDIENT_BUFFER = new BlockIngredientBuffer();
        WIRELESS_CONNECTOR = new BlockWirelessConnector();
        WIRELESS_HUB = new BlockWirelessHub();
        EX_ASSEMBLER = new BlockExMolecularAssembler();
        WIRELESS_TOOL = new ItemWirelessConnectTool();
        WIRELESS_ADVANCED_TOOL = new ItemWirelessAdvancedConnectTool();
        ASSEMBLER_MATRIX_FRAME = new BlockAssemblerMatrixFrame();
        ASSEMBLER_MATRIX_GLASS = new BlockAssemblerMatrixGlass();
        ASSEMBLER_MATRIX_WALL = new BlockAssemblerMatrixWall();
        ASSEMBLER_MATRIX_PATTERN = new BlockAssemblerMatrixPattern();
        ASSEMBLER_MATRIX_CRAFTER = new BlockAssemblerMatrixCrafter();
        ASSEMBLER_MATRIX_SPEED = new BlockAssemblerMatrixSpeed();
        regHandler.block("ex_pattern_provider", EX_PATTERN_PROVIDER, TileExPatternProvider.class, TileExPatternProvider::new);
        regHandler.block("ex_interface", EX_INTERFACE, TileExInterface.class, TileExInterface::new);
        regHandler.block("ex_drive", EX_DRIVE, TileExDrive.class, TileExDrive::new);
        regHandler.block("ingredient_buffer", INGREDIENT_BUFFER, TileIngredientBuffer.class, TileIngredientBuffer::new);
        regHandler.block("wireless_connect", WIRELESS_CONNECTOR, TileWirelessConnector.class, TileWirelessConnector::new);
        regHandler.block("wireless_hub", WIRELESS_HUB, TileWirelessHub.class, TileWirelessHub::new);
        regHandler.block("ex_molecular_assembler", EX_ASSEMBLER, TileExMolecularAssembler.class, TileExMolecularAssembler::new);
        regHandler.block("assembler_matrix_frame", ASSEMBLER_MATRIX_FRAME, TileAssemblerMatrixFrame.class, TileAssemblerMatrixFrame::new);
        regHandler.block("assembler_matrix_glass", ASSEMBLER_MATRIX_GLASS, TileAssemblerMatrixGlass.class, TileAssemblerMatrixGlass::new);
        regHandler.block("assembler_matrix_wall", ASSEMBLER_MATRIX_WALL, TileAssemblerMatrixWall.class, TileAssemblerMatrixWall::new);
        regHandler.block("assembler_matrix_pattern", ASSEMBLER_MATRIX_PATTERN, TileAssemblerMatrixPattern.class, TileAssemblerMatrixPattern::new);
        regHandler.block("assembler_matrix_crafter", ASSEMBLER_MATRIX_CRAFTER, TileAssemblerMatrixCrafter.class, TileAssemblerMatrixCrafter::new);
        regHandler.block("assembler_matrix_speed", ASSEMBLER_MATRIX_SPEED, TileAssemblerMatrixSpeed.class, TileAssemblerMatrixSpeed::new);
        regHandler.item("ex_pattern_provider_part", EX_PATTERN_PROVIDER_PART);
        regHandler.item("ex_interface_part", EX_INTERFACE_PART);
        regHandler.item("ex_pattern_access_terminal", EX_PATTERN_ACCESS_TERMINAL);
        regHandler.item("pattern_provider_upgrade", PATTERN_PROVIDER_UPGRADE);
        regHandler.item("interface_upgrade", INTERFACE_UPGRADE);
        regHandler.item("io_bus_upgrade", IO_BUS_UPGRADE);
        regHandler.item("infinity_cell", INFINITY_CELL);
        regHandler.item("ex_export_bus_part", EX_EXPORT_BUS);
        regHandler.item("precise_export_bus", PRECISE_EXPORT_BUS);
        regHandler.item("ex_import_bus_part", EX_IMPORT_BUS);
        regHandler.item("me_packing_tape", PACKING_TAPE);
        regHandler.item("package", PACKAGE);
        regHandler.item("drive_upgrade", DRIVE_UPGRADE);
        regHandler.item("wireless_tool", WIRELESS_TOOL);
        regHandler.item("wireless_advanced_tool", WIRELESS_ADVANCED_TOOL);
    }

}
