package com.github.glodblock.epp.common.me.wireless;

import appeng.api.networking.IGridNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface WirelessNode {

    long getFrequency();


    IGridNode getGridNode();

    BlockEntity getBlockEntity();
}
