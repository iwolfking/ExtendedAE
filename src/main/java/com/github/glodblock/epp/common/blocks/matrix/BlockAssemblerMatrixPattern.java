package com.github.glodblock.epp.common.blocks.matrix;

import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.common.tileentities.matrix.TileAssemblerMatrixPattern;
import net.minecraft.world.item.Item;

public class BlockAssemblerMatrixPattern extends BlockAssemblerMatrixBase<TileAssemblerMatrixPattern> {

    @Override
    public Item getPresentItem() {
        return net.minecraft.world.item.Items.AIR;
    }

}