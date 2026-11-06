package com.github.glodblock.epp.common.blocks.matrix;

import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.common.tileentities.matrix.TileAssemblerMatrixGlass;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockAssemblerMatrixGlass extends BlockAssemblerMatrixBase<TileAssemblerMatrixGlass> {

    public BlockAssemblerMatrixGlass() {
        super(net.minecraft.world.level.block.state.BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.GLASS).noOcclusion());
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter blockGetter, @NotNull BlockPos pos) {
        return 1;
    }

    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter blockGetter, @NotNull BlockPos p4) {
        return true;
    }

    @Override
    public Item getPresentItem() {
        return net.minecraft.world.item.Items.AIR;
    }

}