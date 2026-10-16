package com.github.glodblock.epp.client.model;

import appeng.api.client.StorageCellModels;
import appeng.client.render.BasicUnbakedModel;
import appeng.client.render.model.AutoRotatingBakedModel;
import appeng.init.internal.InitStorageCells;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.model.IModelConfiguration;

import java.util.*;
import java.util.function.Function;

public class ExDriveModel implements BasicUnbakedModel<ExDriveModel> {

    private static final ResourceLocation MODEL_BASE = ResourceLocation.fromNamespaceAndPath(
            "expatternprovider", "block/extended_drive/extended_me_drive_base");
    private static final ResourceLocation MODEL_CELL_EMPTY = ResourceLocation.fromNamespaceAndPath(
            "ae2", "block/drive/drive_cell_empty");

    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery bakery,
                            Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
                            ItemOverrides overrides, ResourceLocation modelLocation) {
        final Map<Item, BakedModel> cellModels = new IdentityHashMap<>();

        for (Map.Entry<Item, ResourceLocation> entry : StorageCellModels.models().entrySet()) {
            BakedModel cellModel = bakery.bake(entry.getValue(), modelTransform, spriteGetter);
            cellModels.put(entry.getKey(), cellModel);
        }

        final BakedModel baseModel = bakery.bake(MODEL_BASE, modelTransform, spriteGetter);
        final BakedModel defaultCell = bakery.bake(StorageCellModels.getDefaultModel(), modelTransform,
                spriteGetter);
        cellModels.put(Items.AIR, bakery.bake(MODEL_CELL_EMPTY, modelTransform, spriteGetter));

        return new AutoRotatingBakedModel(new ExDriveBakedModel(baseModel, cellModels, defaultCell));
    }

    @Override
    public Collection<ResourceLocation> getModelDependencies() {
        return ImmutableSet.<ResourceLocation>builder().add(StorageCellModels.getDefaultModel())
                .addAll(InitStorageCells.getModels())
                .addAll(StorageCellModels.models().values()).build();
    }
}