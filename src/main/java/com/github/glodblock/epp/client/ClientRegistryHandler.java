package com.github.glodblock.epp.client;

import appeng.client.gui.implementations.PatternProviderScreen;
import appeng.client.gui.layout.SlotGridLayout;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.render.model.AutoRotatingBakedModel;
import appeng.init.client.InitScreens;
import appeng.menu.SlotSemantics;
import com.github.glodblock.epp.EPP;
import com.github.glodblock.epp.client.gui.GuiExDrive;
import com.github.glodblock.epp.client.gui.GuiExIOBus;
import com.github.glodblock.epp.client.gui.GuiExInterface;
import com.github.glodblock.epp.client.gui.GuiExPatternProvider;
import com.github.glodblock.epp.client.gui.GuiIngredientBuffer;
import com.github.glodblock.epp.client.gui.GuiWirelessConnector;
import com.github.glodblock.epp.client.model.AERotatableBlocks;
import com.github.glodblock.epp.client.model.ExDriveModel.Loader;
import com.github.glodblock.epp.client.render.tesr.ExDriveTESR;
import com.github.glodblock.epp.client.render.tesr.IngredientBufferTESR;
import com.github.glodblock.epp.common.tileentities.TileExDrive;
import com.github.glodblock.epp.common.tileentities.TileIngredientBuffer;
import com.github.glodblock.epp.container.ContainerExDrive;
import com.github.glodblock.epp.container.ContainerExIOBus;
import com.github.glodblock.epp.container.ContainerExInterface;
import com.github.glodblock.epp.container.ContainerExPatternProvider;
import com.github.glodblock.epp.container.ContainerIngredientBuffer;
import com.github.glodblock.epp.container.ContainerWirelessConnector;
import com.github.glodblock.epp.util.FCUtil;
import com.google.common.collect.Sets;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;
import java.util.Set;

public class ClientRegistryHandler {

    public static final ClientRegistryHandler INSTANCE = new ClientRegistryHandler();

    public void init() {
        this.registerSemantic();
        this.registerGui();
    }

    public void registerSemantic() {
        ExSemantics.EX_1 = SlotSemantics.register("EX_1", false);
        ExSemantics.EX_2 = SlotSemantics.register("EX_2", false);
        ExSemantics.EX_3 = SlotSemantics.register("EX_3", false);
        ExSemantics.EX_4 = SlotSemantics.register("EX_4", false);
    }

    public void registerGui() {
        InitScreens.<ContainerExPatternProvider, GuiExPatternProvider>register(
                ContainerExPatternProvider.TYPE,
                GuiExPatternProvider::new,
                "/screens/ex_pattern_provider.json"
        );
        InitScreens.<ContainerExInterface, GuiExInterface>register(
                ContainerExInterface.TYPE,
                (menu, playerInv, title, style) -> new GuiExInterface(menu, playerInv, title, style),
                "/screens/ex_interface.json"
        );
        InitScreens.<ContainerExIOBus, GuiExIOBus>register(
                ContainerExIOBus.EXPORT_TYPE,
                (menu, playerInv, title, style) -> new GuiExIOBus(menu, playerInv, title, style),
                "/screens/ex_export_bus.json"
        );
        InitScreens.<ContainerExIOBus, GuiExIOBus>register(
                ContainerExIOBus.IMPORT_TYPE,
                (menu, playerInv, title, style) -> new GuiExIOBus(menu, playerInv, title, style),
                "/screens/ex_import_bus.json"
        );
        InitScreens.<ContainerExDrive, GuiExDrive>register(
                ContainerExDrive.TYPE,
                (menu, playerInv, title, style) -> new GuiExDrive(menu, playerInv, title, style),
                "/screens/ex_drive.json"
        );
        InitScreens.<ContainerIngredientBuffer, GuiIngredientBuffer>register(
                ContainerIngredientBuffer.TYPE,
                (menu, playerInv, title, style) -> new GuiIngredientBuffer(menu, playerInv, title, style),
                "/screens/ingredient_buffer.json"
        );
        InitScreens.<ContainerWirelessConnector, GuiWirelessConnector>register(
                ContainerWirelessConnector.TYPE,
                (menu, playerInv, title, style) -> new GuiWirelessConnector(menu, playerInv, title, style),
                "/screens/wireless_connector.json"
        );
    }

    @SubscribeEvent
    public void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(FCUtil.getTileType(TileExDrive.class), ExDriveTESR::new);
        event.registerBlockEntityRenderer(FCUtil.getTileType(TileIngredientBuffer.class), IngredientBufferTESR::new);
    }

    @SubscribeEvent
    public void registerModelLoaders(ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(ResourceLocation.fromNamespaceAndPath(EPP.MODID, "ex_drive"), new Loader());
    }

    @SubscribeEvent
    public void registerRotatableBlock(ModelBakeEvent event) {
        Map<ResourceLocation, BakedModel> modelRegistry = event.getModelRegistry();
        Set<ResourceLocation> keys = Sets.newHashSet(modelRegistry.keySet());
        BakedModel missingModel = modelRegistry.get(ModelBakery.MISSING_MODEL_LOCATION);
        for (ResourceLocation location : keys) {
            if (location.getNamespace().equals(EPP.MODID)) {
                if (AERotatableBlocks.check(location)) {
                    BakedModel orgModel = modelRegistry.get(location);
                    if (orgModel == missingModel) {
                        continue;
                    }
                    BakedModel newModel = new AutoRotatingBakedModel(orgModel);
                    if (newModel != orgModel) {
                        modelRegistry.put(location, newModel);
                    }
                }
            }
        }
    }
}