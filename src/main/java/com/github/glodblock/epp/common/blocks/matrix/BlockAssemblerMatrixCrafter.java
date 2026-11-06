package com.github.glodblock.epp.common.blocks.matrix;

import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.common.tileentities.matrix.TileAssemblerMatrixCrafter;
import net.minecraft.world.item.Item;

public class BlockAssemblerMatrixCrafter extends BlockAssemblerMatrixBase<TileAssemblerMatrixCrafter> {

    @Override
    public Item getPresentItem() {
        return net.minecraft.world.item.Items.AIR;
    }

}