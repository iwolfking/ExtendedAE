package com.github.glodblock.epp.client.gui;

import appeng.api.config.RedstoneMode;
import appeng.api.config.SchedulingMode;
import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.stacks.GenericStack;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ServerSettingToggleButton;
import appeng.client.gui.widgets.SettingToggleButton;
import appeng.core.definitions.AEItems;
import appeng.core.localization.ButtonToolTips;
import appeng.core.localization.Tooltips;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.helpers.InventoryAction;
import com.github.glodblock.epp.api.BatchMode;
import com.github.glodblock.epp.client.button.CycleEPPButton;
import com.github.glodblock.epp.client.button.EPPIcon;
import com.github.glodblock.epp.client.gui.subgui.SetAmount;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.container.ContainerPreciseExportBus;
import com.github.glodblock.epp.network.EPPNetworkHandler;
import com.github.glodblock.epp.util.Ae2ReflectClient;
import com.github.glodblock.epp.network.packet.CGenericPacket;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GuiPreciseExportBus extends UpgradeableScreen<ContainerPreciseExportBus> {

    private final SettingToggleButton<RedstoneMode> redstoneMode;
    private final SettingToggleButton<YesNo> craftMode;
    private final SettingToggleButton<SchedulingMode> schedulingMode;
    private final CycleEPPButton batchMode;

    public GuiPreciseExportBus(ContainerPreciseExportBus menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.redstoneMode = new ServerSettingToggleButton<>(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        addToLeftToolbar(this.redstoneMode);

        if (menu.getHost().getConfigManager().hasSetting(Settings.CRAFT_ONLY)) {
            this.craftMode = new ServerSettingToggleButton<>(Settings.CRAFT_ONLY, YesNo.NO);
            addToLeftToolbar(this.craftMode);
        } else {
            this.craftMode = null;
        }

        if (menu.getHost().getConfigManager().hasSetting(Settings.SCHEDULING_MODE)) {
            this.schedulingMode = new ServerSettingToggleButton<>(Settings.SCHEDULING_MODE, SchedulingMode.DEFAULT);
            addToLeftToolbar(this.schedulingMode);
        } else {
            this.schedulingMode = null;
        }

        this.batchMode = new CycleEPPButton();
        this.batchMode.addActionPair(EPPIcon.EXACT, new net.minecraft.network.chat.TranslatableComponent("gui.expatternprovider.precise_export_bus.exact"), b -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("set", BatchMode.BATCH.ordinal())));
        this.batchMode.addActionPair(EPPIcon.BATCH, new net.minecraft.network.chat.TranslatableComponent("gui.expatternprovider.precise_export_bus.batch"), b -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("set", BatchMode.EXACT.ordinal())));
        this.addToLeftToolbar(this.batchMode);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.redstoneMode.set(menu.getRedStoneMode());
        this.redstoneMode.setVisibility(menu.hasUpgrade(AEItems.REDSTONE_CARD));
        if (this.craftMode != null) {
            this.craftMode.set(menu.getCraftingMode());
            this.craftMode.setVisibility(menu.hasUpgrade(AEItems.CRAFTING_CARD));
        }
        if (this.schedulingMode != null) {
            this.schedulingMode.set(menu.getSchedulingMode());
        }
        this.batchMode.setState(menu.getMode().ordinal());
    }

    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        assert this.minecraft != null;
        if (this.minecraft.options.keyPickItem.matchesMouse(btn)) {
            var slot = Ae2ReflectClient.getSlot(this, xCoord, yCoord);
            if (isValidSlot(slot)) {
                var currentStack = GenericStack.fromItemStack(slot.getItem());
                if (currentStack != null) {
                    var screen = new SetAmount<>(
                            this,
                            new ItemStack(EPPItemAndBlock.PRECISE_EXPORT_BUS),
                            currentStack,
                            newStack -> NetworkHandler.instance().sendToServer(new InventoryActionPacket(
                                    InventoryAction.SET_FILTER, slot.index,
                                    GenericStack.wrapInItemStack(newStack))));
                    switchToScreen(screen);
                    return true;
                }
            }
        }
        return super.mouseClicked(xCoord, yCoord, btn);
    }

    @Override
    protected void renderTooltip(@NotNull PoseStack guiGraphics, int x, int y) {
        if (this.menu.getCarried().isEmpty() && this.isValidSlot(this.hoveredSlot)) {
            var itemTooltip = new ArrayList<>(getTooltipFromItem(this.hoveredSlot.getItem()));
            var unwrapped = GenericStack.fromItemStack(this.hoveredSlot.getItem());
            if (unwrapped != null) {
                itemTooltip.add(Tooltips.getAmountTooltip(ButtonToolTips.Amount, unwrapped));
            }
            itemTooltip.add(Tooltips.getSetAmountTooltip());
            drawTooltip(guiGraphics, x, y, itemTooltip);
        } else {
            super.renderTooltip(guiGraphics, x, y);
        }
    }

    @Override
    public void drawFG(PoseStack poseStack, int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(poseStack, offsetX, offsetY, mouseX, mouseY);
        poseStack.pushPose();
        poseStack.translate(10, 17, 0);
        poseStack.scale(0.6f, 0.6f, 1);
        var color = style.getColor(PaletteColor.DEFAULT_TEXT_COLOR);
        font.draw(poseStack, new net.minecraft.network.chat.TranslatableComponent("gui.expatternprovider.precise_export_bus.set_amount").getVisualOrderText(), 0, 0, color.toARGB());
        poseStack.popPose();
    }

    private boolean isValidSlot(Slot slot) {
        return slot != null && slot.isActive() && slot.hasItem() && this.menu.isConfigSlot(slot);
    }

}
