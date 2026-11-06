package com.github.glodblock.epp.common.tileentities.matrix;

import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.util.FCUtil;
import com.github.glodblock.epp.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class TileAssemblerMatrixWall extends TileAssemblerMatrixBase {

    public TileAssemblerMatrixWall(BlockPos pos, BlockState blockState) {
        super(FCUtil.getTileType(TileAssemblerMatrixWall.class, TileAssemblerMatrixWall::new, EPPItemAndBlock.ASSEMBLER_MATRIX_WALL), pos, blockState);
    }

    public TileAssemblerMatrixWall(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

}
