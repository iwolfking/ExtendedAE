package com.github.glodblock.epp.client.model;

import appeng.client.render.DelegateBakedModel;
import appeng.client.render.model.DriveModelData;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ExDriveBakedModel extends DelegateBakedModel {
    private final Map<Item, BakedModel> cellModels;
    private final BakedModel defaultCellModel;
    private final ModelState modelTransform;

    public ExDriveBakedModel(BakedModel bakedBase, Map<Item, BakedModel> cellModels, BakedModel defaultCell, ModelState modelTransform) {
        super(bakedBase);
        this.cellModels = cellModels;
        this.defaultCellModel = defaultCell;
        this.modelTransform = modelTransform;
    }

    public static void getSlotOrigin(int row, int col, int disk, Vector3f translation) {
        float xOffset = (9 - col * 8) / 16.0f;
        float yOffset = (13 - row * 3) / 16.0f;
        float zOffset = disk == 0 ? (1 / 16.0f) : (10 / 16.0f);
        translation.set(xOffset, yOffset, zOffset);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData extraData) {
        List<BakedQuad> result = new ArrayList<>(super.getQuads(state, side, rand, extraData));

        if (!extraData.hasProperty(DriveModelData.STATE)) {
            return result;
        }

        Item[] cells = extraData.getData(DriveModelData.STATE);

        Vector3f rawSlotTranslation = new Vector3f();
        if (cells != null) {
            Transformation transformation = modelTransform.getRotation();

            for (int disk = 0; disk < 2; disk++) {
                for (int row = 0; row < 5; row++) {
                    for (int col = 0; col < 2; col++) {
                        int slot = getSlotIndex(row, col, disk);

                        getSlotOrigin(row, col, disk, rawSlotTranslation);

                        // Rotate the translation offset vector according to the blockstate rotation
                        Vector3f rotatedTranslation = rotateOffset(rawSlotTranslation, transformation);

                        Item cell = slot < cells.length ? cells[slot] : null;
                        BakedModel cellChassisModel = getCellChassisModel(cell);

                        if (cellChassisModel != null) {
                            for (BakedQuad quad : cellChassisModel.getQuads(state, side, rand, extraData)) {
                                result.add(translateQuad(quad, rotatedTranslation));
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    private static Vector3f rotateOffset(Vector3f offset, Transformation transformation) {
        if (transformation.isIdentity()) {
            return offset;
        }
        Vector4f pos = new Vector4f(offset.x() - 0.5f, offset.y() - 0.5f, offset.z() - 0.5f, 1.0f);
        pos.transform(transformation.getMatrix());
        return new Vector3f(pos.x() + 0.5f, pos.y() + 0.5f, pos.z() + 0.5f);
    }

    private static BakedQuad translateQuad(BakedQuad quad, Vector3f offset) {
        int[] vertexData = quad.getVertices().clone();
        int step = vertexData.length / 4;

        for (int i = 0; i < 4; i++) {
            int index = i * step;
            float x = Float.intBitsToFloat(vertexData[index]);
            float y = Float.intBitsToFloat(vertexData[index + 1]);
            float z = Float.intBitsToFloat(vertexData[index + 2]);

            vertexData[index] = Float.floatToRawIntBits(x + offset.x());
            vertexData[index + 1] = Float.floatToRawIntBits(y + offset.y());
            vertexData[index + 2] = Float.floatToRawIntBits(z + offset.z());
        }

        return new BakedQuad(vertexData, quad.getTintIndex(), quad.getDirection(), quad.getSprite(), quad.isShade());
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    public BakedModel getCellChassisModel(Item cell) {
        if (cell == null) {
            return cellModels.get(Items.AIR);
        }
        final BakedModel model = cellModels.get(cell);
        return model != null ? model : defaultCellModel;
    }

    private static int getSlotIndex(int row, int col, int disk) {
        return row * 2 + col + disk * 10;
    }
}