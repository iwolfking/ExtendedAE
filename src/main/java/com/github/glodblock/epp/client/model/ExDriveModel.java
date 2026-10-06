package com.github.glodblock.epp.client.model;

import appeng.api.client.StorageCellModels;
import appeng.init.internal.InitStorageCells;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class ExDriveModel implements IModelGeometry<ExDriveModel> {

    private static final ResourceLocation MODEL_BASE = ResourceLocation.fromNamespaceAndPath(
            "expatternprovider", "block/extended_drive/extended_me_drive_base");
    private static final ResourceLocation MODEL_CELL_EMPTY = ResourceLocation.fromNamespaceAndPath(
            "ae2", "block/drive/drive_cell_empty");

    @Nullable
    @Override
    public BakedModel bake(IModelConfiguration owner, ModelBakery baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
        final Map<Item, BakedModel> cellModels = new IdentityHashMap<>();

        for (var entry : StorageCellModels.models().entrySet()) {
            UnbakedModel unbakedCell = baker.getModel(entry.getValue());
            BakedModel cellModel = unbakedCell.bake(baker, spriteGetter, modelTransform, entry.getValue());
            cellModels.put(entry.getKey(), cellModel);
        }

        UnbakedModel unbakedBase = baker.getModel(MODEL_BASE);
        BakedModel baseModel = unbakedBase.bake(baker, spriteGetter, modelTransform, MODEL_BASE);

        UnbakedModel unbakedDefaultCell = baker.getModel(StorageCellModels.getDefaultModel());
        BakedModel defaultCell = unbakedDefaultCell.bake(baker, spriteGetter, modelTransform, StorageCellModels.getDefaultModel());

        UnbakedModel unbakedEmptyCell = baker.getModel(MODEL_CELL_EMPTY);
        cellModels.put(Items.AIR, unbakedEmptyCell.bake(baker, spriteGetter, modelTransform, MODEL_CELL_EMPTY));

        return new ExDriveBakedModel(baseModel, cellModels, defaultCell);
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return getDependencies().stream()
                .map(modelGetter)
                .flatMap(ubm -> ubm.getMaterials(modelGetter, missingTextureErrors).stream())
                .toList();
    }

    public Collection<ResourceLocation> getDependencies() {
        return ImmutableSet.<ResourceLocation>builder()
                .add(MODEL_BASE)
                .add(MODEL_CELL_EMPTY)
                .add(StorageCellModels.getDefaultModel())
                .addAll(InitStorageCells.getModels())
                .addAll(StorageCellModels.models().values())
                .build();
    }

    public static class Loader implements IModelLoader<ExDriveModel> {

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            // No-op for reloading cache in 1.18.2
        }

        @Override
        public ExDriveModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
            return new ExDriveModel();
        }
    }
}