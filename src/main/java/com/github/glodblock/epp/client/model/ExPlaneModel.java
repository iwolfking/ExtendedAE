package com.github.glodblock.epp.client.model;

import appeng.client.render.BasicUnbakedModel;
import appeng.core.AppEng;
import appeng.parts.automation.PlaneModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;

public class ExPlaneModel implements BasicUnbakedModel<ExPlaneModel> {

    private final PlaneModel delegate;

    public ExPlaneModel(ResourceLocation frontTexture) {
        this.delegate = new PlaneModel(frontTexture, AppEng.makeId("part/plane_sides"), AppEng.makeId("part/transition_plane_back"));
    }

    @Override
    public BakedModel bake(net.minecraftforge.client.model.IModelConfiguration owner, ModelBakery bakery,
                          java.util.function.Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform,
                          ItemOverrides overrides, ResourceLocation modelLocation) {
        return this.delegate.bake(owner, bakery, spriteGetter, modelTransform, overrides, modelLocation);
    }

    public static ExPlaneModel create(ResourceLocation frontTexture) {
        return new ExPlaneModel(frontTexture);
    }

}
