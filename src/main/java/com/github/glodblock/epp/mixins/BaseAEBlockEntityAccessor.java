package com.github.glodblock.epp.mixins;

import appeng.blockentity.AEBaseBlockEntity;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AEBaseBlockEntity.class, remap = false)
public interface BaseAEBlockEntityAccessor {
    @Accessor("up")
    void setUp(Direction up);
    @Accessor("forward")
    void setForward(Direction forward);
}
