package com.github.glodblock.epp.common.tileentities.matrix;

import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.util.FCUtil;
import com.github.glodblock.epp.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileAssemblerMatrixFrame extends TileAssemblerMatrixBase {

    public TileAssemblerMatrixFrame(BlockPos pos, BlockState blockState) {
        super(FCUtil.getTileType(TileAssemblerMatrixFrame.class, TileAssemblerMatrixFrame::new, EPPItemAndBlock.ASSEMBLER_MATRIX_FRAME), pos, blockState);
    }

}
