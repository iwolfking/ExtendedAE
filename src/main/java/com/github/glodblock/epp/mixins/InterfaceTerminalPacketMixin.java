package com.github.glodblock.epp.mixins;


import appeng.core.sync.packets.InterfaceTerminalPacket;
import com.github.glodblock.epp.client.gui.AdvancedInterfaceTerminalScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InterfaceTerminalPacket.class, remap = false)
public class InterfaceTerminalPacketMixin {
    @Shadow
    private boolean clearExistingData;
    @Shadow
    private long inventoryId;
    @Shadow
    private CompoundTag in;

    @Inject(method = "clientPacketData", at = @At("HEAD"))
    public void injectClientPacketData(Player player, CallbackInfo info) {
        if (Minecraft.getInstance().screen instanceof AdvancedInterfaceTerminalScreen<?> interfaceTerminal) {
            interfaceTerminal.postInventoryUpdate(this.clearExistingData, this.inventoryId, this.in);
        }
    }
}