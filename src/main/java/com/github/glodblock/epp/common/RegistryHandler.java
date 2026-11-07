package com.github.glodblock.epp.common;

import appeng.api.client.StorageCellModels;
import appeng.api.parts.PartModels;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.block.AEBaseBlockItem;
import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.client.render.SimpleModelLoader;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.items.parts.PartModelsHelper;
import com.github.glodblock.epp.EPP;
import com.github.glodblock.epp.client.model.ExDriveModel;
import com.github.glodblock.epp.common.inventory.InfinityCellInventory;
import com.github.glodblock.epp.common.items.ItemMEPackingTape;
import com.github.glodblock.epp.common.parts.*;
import com.github.glodblock.epp.config.EPPConfig;
import com.github.glodblock.epp.container.*;
import com.github.glodblock.epp.util.FCUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RegistryHandler {

    public static final RegistryHandler INSTANCE = new RegistryHandler();

    protected final List<Pair<String, Block>> blocks = new ArrayList<>();
    protected final List<Pair<String, Item>> items = new ArrayList<>();
    protected final List<Pair<String, BlockEntityType<?>>> tiles = new ArrayList<>();

    public void block(String name, Block block) {
        blocks.add(Pair.of(name, block));
        if (block instanceof AEBaseEntityBlock<?> tileBlock) {
            tile(name, tileBlock.getBlockEntityType());
        }
    }

    public <T extends AEBaseBlockEntity> void block(String name, AEBaseEntityBlock<T> block, Class<T> clazz, BlockEntityType.BlockEntitySupplier<? extends T> supplier) {
        bindTileEntity(clazz, block, supplier);
        block(name, block);
    }

    public void item(String name, Item item) {
        items.add(Pair.of(name, item));
    }

    public void tile(String name, BlockEntityType<?> type) {
        tiles.add(Pair.of(name, type));
    }

    @SubscribeEvent
    public void runRegister(RegistryEvent.Register<Block> event) {
        this.onRegisterBlocks();
    }

    @SubscribeEvent
    public void runRegisterItems(RegistryEvent.Register<Item> event) {
        this.onRegisterItems();
        this.onRegisterModels();
    }

    @SubscribeEvent
    public void runRegisterTiles(RegistryEvent.Register<BlockEntityType<?>> event) {
        this.onRegisterTileEntities();
    }

    @SubscribeEvent
    public void runRegisterMenus(RegistryEvent.Register<MenuType<?>> event) {
        this.onRegisterContainer(event.getRegistry());
    }

    private <V extends IForgeRegistryEntry<V>> V register(IForgeRegistry<V> registry, String name, V entry) {
        if (entry.getRegistryName() == null) {
            entry.setRegistryName(EPP.id(name));
        }
        registry.register(entry);
        return entry;
    }

    private void onRegisterBlocks() {
        for (Pair<String, Block> entry : blocks) {
            register(ForgeRegistries.BLOCKS, entry.getLeft(), entry.getRight());
        }
    }

    private void onRegisterItems() {
        for (Pair<String, Block> entry : blocks) {
            AEBaseBlockItem item = new AEBaseBlockItem(entry.getRight(), new Item.Properties().tab(EPPItemAndBlock.TAB));
            register(ForgeRegistries.ITEMS, entry.getLeft(), item);
        }
        for (Pair<String, Item> entry : items) {
            register(ForgeRegistries.ITEMS, entry.getLeft(), entry.getRight());
        }
    }

    private void onRegisterTileEntities() {
        for (Pair<String, BlockEntityType<?>> entry : tiles) {
            register(ForgeRegistries.BLOCK_ENTITIES, entry.getLeft(), entry.getRight());
        }
    }

    private void onRegisterContainer(IForgeRegistry<MenuType<?>> registry) {
        registerMenu(registry, ContainerAssemblerMatrix.TYPE);
        registerMenu(registry, ContainerExMolecularAssembler.TYPE);
        registerMenu(registry, ContainerPreciseExportBus.TYPE);
        registerMenu(registry, ContainerPatternModifier.TYPE);
        registerMenu(registry, ContainerActiveFormationPlane.TYPE);
        registerMenu(registry, ContainerModExportBus.TYPE);
        registerMenu(registry, ContainerModStorageBus.TYPE);
        registerMenu(registry, ContainerTagExportBus.TYPE);
        registerMenu(registry, ContainerTagStorageBus.TYPE);
        registerMenu(registry, ContainerExPatternProvider.TYPE);
        registerMenu(registry, ContainerExInterface.TYPE);
        registerMenu(registry, ContainerExIOBus.EXPORT_TYPE);
        registerMenu(registry, ContainerExIOBus.IMPORT_TYPE);
        registerMenu(registry, ContainerExDrive.TYPE);
        registerMenu(registry, ContainerIngredientBuffer.TYPE);
        registerMenu(registry, ContainerWirelessConnector.TYPE);
        registerMenu(registry, ContainerWirelessHub.TYPE);
        registerMenu(registry, AdvancedInterfaceTerminalMenu.TYPE);
    }

    private <T extends MenuType<?>> void registerMenu(IForgeRegistry<MenuType<?>> registry, T menuType) {
        registry.register(menuType);
    }

    private <T extends AEBaseBlockEntity> void bindTileEntity(Class<T> clazz, AEBaseEntityBlock<T> block, BlockEntityType.BlockEntitySupplier<? extends T> supplier) {
        BlockEntityTicker<T> serverTicker = null;
        if (ServerTickingBlockEntity.class.isAssignableFrom(clazz)) {
            serverTicker = (level, pos, state, entity) -> ((ServerTickingBlockEntity) entity).serverTick();
        }
        BlockEntityTicker<T> clientTicker = null;
        if (ClientTickingBlockEntity.class.isAssignableFrom(clazz)) {
            clientTicker = (level, pos, state, entity) -> ((ClientTickingBlockEntity) entity).clientTick();
        }
        block.setBlockEntity(clazz, FCUtil.getTileType(clazz, supplier, block), clientTicker, serverTicker);
    }

    public void onInit() {
        for (Pair<String, Block> entry : blocks) {
            Block block = ForgeRegistries.BLOCKS.getValue(EPP.id(entry.getKey()));
            if (block instanceof AEBaseEntityBlock<?>) {
                AEBaseBlockEntity.registerBlockEntityItem(
                        ((AEBaseEntityBlock<?>) block).getBlockEntityType(),
                        block.asItem()
                );
            }
        }
        this.registerAEUpgrade();
        this.registerStorageHandler();
        this.initPackageList();
    }

    private void initPackageList() {
        EPPConfig.tapeWhitelist.forEach(ItemMEPackingTape::registerPackableDevice);
    }

    private void registerAEUpgrade() {
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.EX_INTERFACE.asItem(), 1, "gui.expatternprovider.ex_interface");
        Upgrades.add(AEItems.CRAFTING_CARD, EPPItemAndBlock.EX_INTERFACE.asItem(), 1, "gui.expatternprovider.ex_interface");
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.EX_INTERFACE_PART.asItem(), 1, "gui.expatternprovider.ex_interface");
        Upgrades.add(AEItems.CRAFTING_CARD, EPPItemAndBlock.EX_INTERFACE_PART.asItem(), 1, "gui.expatternprovider.ex_interface");
        Upgrades.add(AEItems.VOID_CARD, EPPItemAndBlock.INFINITY_CELL, 1, "item.expatternprovider.infinity_cell");
        Upgrades.add(AEItems.CAPACITY_CARD, EPPItemAndBlock.EX_EXPORT_BUS, 2, "group.ex_io_bus_part");
        Upgrades.add(AEItems.REDSTONE_CARD, EPPItemAndBlock.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.EX_EXPORT_BUS, 4, "group.ex_io_bus_part");
        Upgrades.add(AEItems.INVERTER_CARD, EPPItemAndBlock.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.CRAFTING_CARD, EPPItemAndBlock.EX_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.CAPACITY_CARD, EPPItemAndBlock.EX_IMPORT_BUS, 2, "group.ex_io_bus_part");
        Upgrades.add(AEItems.REDSTONE_CARD, EPPItemAndBlock.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.EX_IMPORT_BUS, 4, "group.ex_io_bus_part");
        Upgrades.add(AEItems.INVERTER_CARD, EPPItemAndBlock.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.EX_IMPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.ENERGY_CARD, EPPItemAndBlock.WIRELESS_CONNECTOR, 4, "gui.expatternprovider.wireless_connect");
        Upgrades.add(AEItems.ENERGY_CARD, EPPItemAndBlock.WIRELESS_HUB, 4, "gui.expatternprovider.wireless_hub");

        // Mod Export Bus
        Upgrades.add(AEItems.CAPACITY_CARD, EPPItemAndBlock.MOD_EXPORT_BUS, 2, "group.ex_io_bus_part");
        Upgrades.add(AEItems.REDSTONE_CARD, EPPItemAndBlock.MOD_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.MOD_EXPORT_BUS, 4, "group.ex_io_bus_part");
        Upgrades.add(AEItems.INVERTER_CARD, EPPItemAndBlock.MOD_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.MOD_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.CRAFTING_CARD, EPPItemAndBlock.MOD_EXPORT_BUS, 1, "group.ex_io_bus_part");
        // Tag Export Bus
        Upgrades.add(AEItems.CAPACITY_CARD, EPPItemAndBlock.TAG_EXPORT_BUS, 2, "group.ex_io_bus_part");
        Upgrades.add(AEItems.REDSTONE_CARD, EPPItemAndBlock.TAG_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.SPEED_CARD, EPPItemAndBlock.TAG_EXPORT_BUS, 4, "group.ex_io_bus_part");
        Upgrades.add(AEItems.INVERTER_CARD, EPPItemAndBlock.TAG_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.TAG_EXPORT_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.CRAFTING_CARD, EPPItemAndBlock.TAG_EXPORT_BUS, 1, "group.ex_io_bus_part");
        // Mod Storage Bus
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.MOD_STORAGE_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.INVERTER_CARD, EPPItemAndBlock.MOD_STORAGE_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.CAPACITY_CARD, EPPItemAndBlock.MOD_STORAGE_BUS, 5, "group.ex_io_bus_part");
        Upgrades.add(AEItems.REDSTONE_CARD, EPPItemAndBlock.MOD_STORAGE_BUS, 1, "group.ex_io_bus_part");
        // Tag Storage Bus
        Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.TAG_STORAGE_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.INVERTER_CARD, EPPItemAndBlock.TAG_STORAGE_BUS, 1, "group.ex_io_bus_part");
        Upgrades.add(AEItems.CAPACITY_CARD, EPPItemAndBlock.TAG_STORAGE_BUS, 5, "group.ex_io_bus_part");
        Upgrades.add(AEItems.REDSTONE_CARD, EPPItemAndBlock.TAG_STORAGE_BUS, 1, "group.ex_io_bus_part");
    }

    private void registerStorageHandler() {
        StorageCells.addCellHandler(InfinityCellInventory.HANDLER);
        StorageCellModels.registerModel(EPPItemAndBlock.INFINITY_CELL, EPP.id("block/drive/infinity_cell"));
    }

    private void onRegisterModels() {
        PartModels.registerModels(PartExPatternProvider.MODELS);
        PartModels.registerModels(PartExInterface.MODELS);
        PartModels.registerModels(PartExExportBus.MODELS);
        PartModels.registerModels(PartActiveFormationPlane.MODELS);
        PartModels.registerModels(java.util.Arrays.asList(PartModExportBus.MODEL_BASE));
        PartModels.registerModels(java.util.Arrays.asList(PartModStorageBus.MODEL_BASE));
        PartModels.registerModels(java.util.Arrays.asList(PartTagExportBus.MODEL_BASE));
        PartModels.registerModels(java.util.Arrays.asList(PartTagStorageBus.MODEL_BASE));
        // PartActiveFormationPlane uses client model loader, no PartModels base
        PartModels.registerModels(PartPreciseExportBus.MODELS);
        PartModels.registerModels(PartExImportBus.MODELS);
        PartModels.registerModels(PartModelsHelper.createModels(AdvancedPatternAccessTerminalPart.class));
    }
}