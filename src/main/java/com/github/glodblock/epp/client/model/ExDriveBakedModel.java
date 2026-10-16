package com.github.glodblock.epp.client.model;

import appeng.client.render.DelegateBakedModel;
import appeng.client.render.model.DriveModelData;
import com.github.glodblock.epp.client.lib.MatrixVertexTransformer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ExDriveBakedModel extends DelegateBakedModel {
    private final Map<Item, BakedModel> bakedCells;
    private final BakedModel defaultCell;

    public ExDriveBakedModel(BakedModel bakedBase, Map<Item, BakedModel> cellModels, BakedModel defaultCell) {
        super(bakedBase);
        this.bakedCells = cellModels;
        this.defaultCell = defaultCell;
    }

    public static void getSlotOrigin(int row, int col, int disk, Vector3f translation) {
        float xOffset = (9.0f - (col * 8.0f)) / 16.0f;
        float yOffset = (13.0f - (row * 3.0f)) / 16.0f;
        float zOffset = disk == 0 ? (1.0f / 16.0f) : (9.0f / 16.0f);
        translation.set(xOffset, yOffset, zOffset);
    }

    private static int getSlotIndex(int row, int col, int disk) {
        return row * 2 + col + (disk * 10);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand,
                                    IModelData extraData) {
        List<BakedQuad> result = new ArrayList<>(super.getQuads(state, side, rand, extraData));

        if (!extraData.hasProperty(DriveModelData.STATE)) {
            return result;
        }

        Item[] cells = extraData.getData(DriveModelData.STATE);
        if (cells == null) {
            return result;
        }

        Vector3f slotTranslation = new Vector3f();
        for (int disk = 0; disk < 2; disk++) {
            for (int row = 0; row < 5; row++) {
                for (int col = 0; col < 2; col++) {
                    int slot = getSlotIndex(row, col, disk);
                    Item cell = slot < cells.length ? cells[slot] : null;
                    if (cell == null || cell == Items.AIR) {
                        continue;
                    }

                    getSlotOrigin(row, col, disk, slotTranslation);

                    Matrix4f transform = new Matrix4f();
                    transform.setTranslation(slotTranslation.x(), slotTranslation.y(), slotTranslation.z());

                    BakedModel cellChassisModel = getCellChassisModel(cell);
                    addModel(state, rand, extraData, result, side, cellChassisModel, transform);
                }
            }
        }

        return result;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    public BakedModel getCellChassisModel(Item cell) {
        if (cell == null) {
            return bakedCells.get(Items.AIR);
        }
        final BakedModel model = bakedCells.get(cell);
        return model != null ? model : defaultCell;
    }

    private static void addModel(@Nullable BlockState state, Random rand, IModelData extraData,
                                 List<BakedQuad> result, Direction side, BakedModel bakedCell, Matrix4f transform) {
        MatrixVertexTransformer transformer = new MatrixVertexTransformer(transform);
        for (BakedQuad bakedQuad : bakedCell.getQuads(state, side, rand, extraData)) {
            BakedQuadBuilder builder = new BakedQuadBuilder();
            transformer.setParent(builder);
            transformer.setVertexFormat(builder.getVertexFormat());
            bakedQuad.pipe(transformer);
            result.add(builder.build());
        }
    }
}