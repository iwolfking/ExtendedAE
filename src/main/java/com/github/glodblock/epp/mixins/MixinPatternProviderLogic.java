package com.github.glodblock.epp.mixins;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IManagedGridNode;
import appeng.helpers.iface.PatternProviderLogic;
import appeng.helpers.iface.PatternProviderLogicHost;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import appeng.util.inv.filter.IAEItemFilter;
import com.github.glodblock.epp.EPP;
import com.github.glodblock.epp.common.lib.IExtendedPatternProviderHost;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PatternProviderLogic.class, remap = false)
public class MixinPatternProviderLogic {

    @Final
    @Shadow
    private PatternProviderLogicHost host;

    @Final
    @Shadow
    @Mutable
    private AppEngInternalInventory patternInventory;

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    private void resizePatternInventory(IManagedGridNode mainNode, PatternProviderLogicHost host, CallbackInfo ci) {
        EPP.LOGGER.info(host.getClass().toString());
        if (host instanceof IExtendedPatternProviderHost extendedHost) {
            int customSlots = extendedHost.getPatternSlotCount();
            EPP.LOGGER.info(String.valueOf(customSlots));

            this.patternInventory = new AppEngInternalInventory((InternalInventoryHost) this, customSlots);
            EPP.LOGGER.info(String.valueOf(this.patternInventory.size()));

            this.patternInventory.setFilter(new IAEItemFilter() {
                @Override
                public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
                    return PatternDetailsHelper.isEncodedPattern(stack);
                }
            });
        }
    }
}