package com.github.glodblock.epp.common.tileentities.matrix;

import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.util.FCUtil;
import com.github.glodblock.epp.common.me.matrix.ClusterAssemblerMatrix;
import com.github.glodblock.epp.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileAssemblerMatrixSpeed extends TileAssemblerMatrixFunction {

    public TileAssemblerMatrixSpeed(BlockPos pos, BlockState blockState) {
        super(FCUtil.getTileType(TileAssemblerMatrixSpeed.class, TileAssemblerMatrixSpeed::new, EPPItemAndBlock.ASSEMBLER_MATRIX_SPEED), pos, blockState);
    }

    @Override
    public void add(ClusterAssemblerMatrix c) {
        c.addSpeedCore();
    }

}
