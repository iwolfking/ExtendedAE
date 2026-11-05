package com.github.glodblock.epp.client.gui;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import com.github.glodblock.epp.container.ContainerWirelessHub;
import com.github.glodblock.epp.network.EPPNetworkHandler;
import com.github.glodblock.epp.network.packet.PacketDisconnectWirelessHubPort;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class GuiWirelessHub extends UpgradeableScreen<ContainerWirelessHub> {

    private static final int COL1_X = 37;
    private static final int COL2_X = 120;
    private static final int COL_Y = 44;
    private static final int Y_OFFSET = 28;
    private final Button[] disconnectBtn = new Button[com.github.glodblock.epp.common.tileentities.TileWirelessHub.MAX_PORT];
    private final Button[] highlightBtn = new Button[com.github.glodblock.epp.common.tileentities.TileWirelessHub.MAX_PORT];

    public GuiWirelessHub(ContainerWirelessHub menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        for (int i = 0; i < com.github.glodblock.epp.common.tileentities.TileWirelessHub.MAX_PORT; i++) {
            final int port = i;
            disconnectBtn[i] = new Button(0, 0, 16, 16, new net.minecraft.network.chat.TextComponent("X"), b ->
                    EPPNetworkHandler.INSTANCE.sendToServer(new PacketDisconnectWirelessHubPort(port)));
            highlightBtn[i] = new Button(0, 0, 16, 16, new net.minecraft.network.chat.TextComponent("H"), b -> {
                var rp = this.menu.getRemotePosition(port);
                var p = net.minecraft.client.Minecraft.getInstance().player;
                if (p != null) {
                    p.displayClientMessage(new net.minecraft.network.chat.TextComponent("Wireless Hub link: " + rp.getX() + ", " + rp.getY() + ", " + rp.getZ()), false);
                }
            });
        }
    }

    @Override
    public void init() {
        super.init();
        for (int i = 0; i < com.github.glodblock.epp.common.tileentities.TileWirelessHub.MAX_PORT; i++) {
            int x = (i < 4 ? COL1_X : COL2_X) + 18;
            int y = COL_Y + (i % 4) * Y_OFFSET;
            disconnectBtn[i].x = this.leftPos + x;
            disconnectBtn[i].y = this.topPos + y;
            this.addRenderableWidget(disconnectBtn[i]);
            highlightBtn[i].x = this.leftPos + (i < 4 ? COL1_X : COL2_X) - 18;
            highlightBtn[i].y = this.topPos + y;
            this.addRenderableWidget(highlightBtn[i]);
        }
    }

    @Override
    public void drawFG(PoseStack poseStack, int offsetX, int offsetY, int mouseX, int mouseY) {
        int textColor = this.style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB();
        this.font.draw(poseStack, new net.minecraft.network.chat.TextComponent("Power: "+ String.format("%.2f", this.menu.powerUse / 100f)), 8, 18, textColor);
        this.font.draw(poseStack, new net.minecraft.network.chat.TextComponent("Channels: " + this.menu.usedChannel + "/" + this.menu.maxChannel), 8, 30, textColor);
        for (int i = 0; i < com.github.glodblock.epp.common.tileentities.TileWirelessHub.MAX_PORT; i++) {
            int x = (i < 4 ? COL1_X : COL2_X);
            int y = COL_Y + (i % 4) * Y_OFFSET;
            var status = this.menu.getStatus(i);
            int color;
            switch (status) {
                case WORKING -> color = 0xFF30B54B;
                case NO_POWER -> color = 0xFFF5C842;
                case REMOTE_ERROR -> color = 0xFFD53C3C;
                default -> color = 0xFF777777;
            }
            this.fill(poseStack, x, y, x + 16, y + 16, color);
            this.font.draw(poseStack, new net.minecraft.network.chat.TextComponent(String.valueOf(i + 1)), x + 4, y + 4, 0xFFFFFFFF);
        }
    }

}
