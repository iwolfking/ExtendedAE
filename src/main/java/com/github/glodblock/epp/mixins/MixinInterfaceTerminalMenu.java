package com.github.glodblock.epp.mixins;

import appeng.api.networking.IGrid;
import appeng.client.gui.me.interfaceterminal.InterfaceTerminalScreen;
import appeng.menu.implementations.InterfaceTerminalMenu;
import com.github.glodblock.epp.common.parts.PartExPatternProvider;
import com.github.glodblock.epp.common.tileentities.TileExPatternProvider;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

@Mixin(value = InterfaceTerminalMenu.class, remap = false)
public abstract class MixinInterfaceTerminalMenu {

    @WrapOperation(method = "broadcastChanges", at = @At(value = "INVOKE", target = "Lappeng/menu/implementations/InterfaceTerminalMenu;visitInterfaceHosts(Lappeng/api/networking/IGrid;Ljava/lang/Class;Lappeng/menu/implementations/InterfaceTerminalMenu$VisitorState;)V", ordinal = 1))
    private void searchForExtendedProviders(InterfaceTerminalMenu instance, IGrid grid, Class<?> machineClass, @Coerce Object state, Operation<Void> original) {
        original.call(instance, grid, machineClass, state);
        original.call(instance, grid, PartExPatternProvider.class, state);
        original.call(instance, grid, TileExPatternProvider.class, state);
    }
}
