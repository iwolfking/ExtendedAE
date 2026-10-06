package com.github.glodblock.epp.mixins;

import appeng.helpers.iface.PatternProviderLogic;
import appeng.menu.SlotSemantic;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.PatternProviderMenu;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PatternProviderMenu.class, remap = false)
public class MixinPatternProviderMenu {


    @Shadow
    @Final
    private PatternProviderLogic logic;

    @ModifyConstant(
        method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lappeng/helpers/iface/PatternProviderLogicHost;)V",
        constant = @Constant(intValue = 9, ordinal = 0)
    )
    private int modifyPatternSlotCount(int original) {
        return this.logic.getPatternInv().size();
    }
}