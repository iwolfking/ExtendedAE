package com.github.glodblock.epp.client.gui;

import appeng.api.config.RedstoneMode;
import appeng.api.config.Settings;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.definitions.AEItems;
import appeng.client.gui.widgets.AETextField;
import com.github.glodblock.epp.container.ContainerTagExportBus;
import com.github.glodblock.epp.network.EPPNetworkHandler;
import com.github.glodblock.epp.network.packet.CGenericPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.regex.Pattern;

public class GuiTagExportBus extends UpgradeableScreen<ContainerTagExportBus> {

    private final SettingToggleButton<RedstoneMode> redstoneMode;

    private final AETextField filterInputs;
    private final AETextField filterInputs2;

    private static final Pattern ORE_DICTIONARY_FILTER = Pattern.compile("[0-9a-zA-Z* &|^!():/_\\n]*");

    public GuiTagExportBus(ContainerTagExportBus menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.redstoneMode = new ServerSettingToggleButton<>(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        addToLeftToolbar(this.redstoneMode);

        var placeholder = new net.minecraft.network.chat.TranslatableComponent("gui.expatternprovider.tag_storage_bus.tooltip");

        this.filterInputs = widgets.addTextField("filter_input");
        this.filterInputs.setMaxLength(1024);
        this.filterInputs.setPlaceholder(placeholder);
        this.filterInputs.setValue(menu.exp);
        this.filterInputs.setResponder(s -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("set", s, true)));

        this.filterInputs2 = widgets.addTextField("filter_input_2");
        this.filterInputs2.setMaxLength(1024);
        this.filterInputs2.setPlaceholder(placeholder);
        this.filterInputs2.setValue(menu.exp2);
        this.filterInputs2.setResponder(s -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("set", s, false)));

        this.setInitialFocus(this.filterInputs);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.redstoneMode.set(menu.getRedStoneMode());
        this.redstoneMode.setVisibility(menu.hasUpgrade(AEItems.REDSTONE_CARD));
    }

    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        if (btn == 1 && this.filterInputs.isMouseOver(xCoord, yCoord)) {
            this.filterInputs.setValue("");
        }
        if (btn == 1 && this.filterInputs2.isMouseOver(xCoord, yCoord)) {
            this.filterInputs2.setValue("");
        }
        return super.mouseClicked(xCoord, yCoord, btn);
    }

}
