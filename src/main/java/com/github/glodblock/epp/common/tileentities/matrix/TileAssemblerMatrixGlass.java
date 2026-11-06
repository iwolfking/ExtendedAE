package com.github.glodblock.epp.common.tileentities.matrix;

import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.util.FCUtil;
import com.github.glodblock.epp.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileAssemblerMatrixGlass extends TileAssemblerMatrixWall {

    public TileAssemblerMatrixGlass(BlockPos pos, BlockState blockState) {
        super(FCUtil.getTileType(TileAssemblerMatrixGlass.class, TileAssemblerMatrixGlass::new, EPPItemAndBlock.ASSEMBLER_MATRIX_GLASS), pos, blockState);
    }

}
