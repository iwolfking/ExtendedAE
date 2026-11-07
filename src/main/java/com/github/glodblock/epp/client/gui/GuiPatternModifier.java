package com.github.glodblock.epp.client.gui;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import appeng.core.AppEng;
import com.github.glodblock.epp.client.button.ActionEPPButton;
import com.github.glodblock.epp.client.button.EPPIcon;
import com.github.glodblock.epp.container.ContainerPatternModifier;
import com.github.glodblock.epp.network.EPPNetworkHandler;
import com.github.glodblock.epp.network.packet.CUpdatePage;
import com.github.glodblock.epp.network.packet.CGenericPacket;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class GuiPatternModifier extends AEBaseScreen<ContainerPatternModifier> {

    private final ActionEPPButton clone;
    private final Button replace;
    private final List<Button> multiBtns = new ArrayList<>();

    public GuiPatternModifier(ContainerPatternModifier menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        ActionEPPButton changeMode = new ActionEPPButton(b -> EPPNetworkHandler.INSTANCE.sendToServer(new CUpdatePage(() -> (this.menu.page + 1) % 3)), Icon.SCHEDULING_DEFAULT);
        this.clone = new ActionEPPButton(b -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("clone")), EPPIcon.RIGHT);
        changeMode.setMessage(new net.minecraft.network.chat.TranslatableComponent("gui.expatternprovider.pattern_modifier.change"));
        this.clone.setMessage(new net.minecraft.network.chat.TranslatableComponent("gui.expatternprovider.pattern_modifier.clone.desc"));
        addToLeftToolbar(changeMode);
        this.replace = new Button(0, 0, 46, 18, new net.minecraft.network.chat.TranslatableComponent("gui.expatternprovider.pattern_modifier.replace_button"), b -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("replace")));
        this.multiBtns.add(
                new Button(0, 0, 23, 18, new net.minecraft.network.chat.TextComponent("x2"), b -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("modify", 2, false)))
        );
        this.multiBtns.add(
                new Button(0, 0, 23, 18, new net.minecraft.network.chat.TextComponent("x10"), b -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("modify", 10, false)))
        );
        this.multiBtns.add(
                new Button(0, 0, 23, 18, new net.minecraft.network.chat.TextComponent("÷2"), b -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("modify", 2, true)))
        );
        this.multiBtns.add(
                new Button(0, 0, 23, 18, new net.minecraft.network.chat.TextComponent("÷10"), b -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("modify", 10, true)))
        );
        this.multiBtns.add(
                new Button(0, 0, 36, 18, new net.minecraft.network.chat.TranslatableComponent("gui.expatternprovider.pattern_modifier.clear"), b -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("clear")))
        );
        this.imageHeight = 192;
    }

    @Override
    public void drawFG(PoseStack poseStack, int offsetX, int offsetY, int mouseX, int mouseY) {
        this.font.draw(poseStack,
                new net.minecraft.network.chat.TranslatableComponent("gui.expatternprovider.pattern_modifier", getModeName()).getVisualOrderText(),
                8, 6,
                style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB());
        if (this.menu.page == 2) {
            this.font.draw(poseStack,
                    new net.minecraft.network.chat.TranslatableComponent("gui.expatternprovider.pattern_modifier.blank").getVisualOrderText(),
                    52, 57,
                    style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB());
            this.font.draw(poseStack,
                    new net.minecraft.network.chat.TranslatableComponent("gui.expatternprovider.pattern_modifier.target").getVisualOrderText(),
                    52, 25,
                    style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB());
        }
    }

    @Override
    public void init() {
        super.init();
        this.multiBtns.get(0).x = this.leftPos + 7; this.multiBtns.get(0).y = this.topPos + 19;
        this.multiBtns.get(1).x = this.leftPos + 37;
        this.multiBtns.get(1).y = this.topPos + 19;
        this.multiBtns.get(2).x = this.leftPos + 67;
        this.multiBtns.get(2).y = this.topPos + 19;
        this.multiBtns.get(3).x = this.leftPos + 97;
        this.multiBtns.get(3).y = this.topPos + 19;
        this.multiBtns.get(4).x = this.leftPos + 130;
        this.multiBtns.get(4).y = this.topPos + 19;
        this.multiBtns.forEach(this::addRenderableWidget);
        this.clone.x = this.leftPos + 79;
        this.clone.y = this.topPos + 35;
        this.addRenderableWidget(this.clone);
        this.replace.x = this.leftPos + 120;
        this.replace.y = this.topPos + 19;
        this.addRenderableWidget(this.replace);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("show"));
        this.menu.showPage();
        if (this.menu.page == 0) {
            this.clone.visible = false;
            this.multiBtns.forEach(b -> b.visible = true);
            this.replace.visible = false;
        } else if (this.menu.page == 1) {
            this.clone.visible = false;
            this.multiBtns.forEach(b -> b.visible = false);
            this.replace.visible = true;
        } else if (this.menu.page == 2) {
            this.clone.visible = true;
            this.multiBtns.forEach(b -> b.visible = false);
            this.replace.visible = false;
        }
    }

    @Override
    public void drawBG(PoseStack poseStack, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        if (this.menu.page == 0) {
            bindTexture("guis/pattern_editor_1.png");
            blit(poseStack, offsetX, offsetY, 0, 0, 176, 192);
        } else if (this.menu.page == 1) {
            bindTexture("guis/pattern_editor_3.png");
            blit(poseStack, offsetX, offsetY, 0, 0, 176, 192);
        } else if (this.menu.page == 2) {
            bindTexture("guis/pattern_editor_2.png");
            blit(poseStack, offsetX, offsetY, 0, 0, 176, 192);
        }
        super.drawBG(poseStack, offsetX, offsetY, mouseX, mouseY, partialTicks);
    }

    private Component getModeName() {
        return switch (this.menu.page) {
            case 0 -> new net.minecraft.network.chat.TranslatableComponent("gui.expatternprovider.pattern_modifier.multiply");
            case 1 -> new net.minecraft.network.chat.TranslatableComponent("gui.expatternprovider.pattern_modifier.replace");
            case 2 -> new net.minecraft.network.chat.TranslatableComponent("gui.expatternprovider.pattern_modifier.clone");
            default -> new net.minecraft.network.chat.TextComponent("");
        };
    }

}
