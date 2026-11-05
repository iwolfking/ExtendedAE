package com.github.glodblock.epp.network.packet;

import com.github.glodblock.epp.container.ContainerWirelessHub;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

public class PacketDisconnectWirelessHubPort implements IMessage<PacketDisconnectWirelessHubPort> {

    private int port;

    public PacketDisconnectWirelessHubPort() {}

    public PacketDisconnectWirelessHubPort(int port) {
        this.port = port;
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(port);
    }

    @Override
    public void fromBytes(FriendlyByteBuf buf) {
        this.port = buf.readVarInt();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onMessage(Player player) {
        if (player.containerMenu instanceof ContainerWirelessHub hub) {
            hub.getHost().killPort(this.port);
        }
    }

    @Override
    public Class<PacketDisconnectWirelessHubPort> getPacketClass() {
        return PacketDisconnectWirelessHubPort.class;
    }

    @Override
    public boolean isClient() {
        return false;
    }

}
