package com.github.glodblock.epp.client.gui;

import appeng.api.config.YesNo;
import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.style.PaletteColor;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.AETextField;
import appeng.client.gui.widgets.Scrollbar;
import appeng.core.AppEng;
import appeng.core.localization.GuiText;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.InventoryActionPacket;
import appeng.crafting.pattern.EncodedPatternItem;
import appeng.helpers.InventoryAction;
import appeng.util.inv.AppEngInternalInventory;
import com.github.glodblock.epp.client.button.ActionEPPButton;
import com.github.glodblock.epp.client.button.CycleEPPButton;
import com.github.glodblock.epp.client.gui.widget.AssemblerMatrixSlot;
import com.github.glodblock.epp.common.tileentities.matrix.TileAssemblerMatrixPattern;
import com.github.glodblock.epp.container.ContainerAssemblerMatrix;
import com.github.glodblock.epp.network.EPPNetworkHandler;
import com.github.glodblock.epp.network.packet.CGenericPacket;
import com.github.glodblock.epp.util.FCUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenCustomHashSet;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class GuiAssemblerMatrix extends AEBaseScreen<ContainerAssemblerMatrix> implements com.github.glodblock.epp.network.packet.sync.IActionHolder {

    private static final int ROW_HEIGHT = 18;
    private static final int GUI_PADDING_X = 8;
    private static final int SLOT_SIZE = 18;
    private static final ResourceLocation BG = AppEng.makeId("textures/guis/assembler_matrix.png");
    private static final Rect2i EMPTY_ROW1 = new Rect2i(0, 199, 162, 18);
    private static final Rect2i EMPTY_ROW2 = new Rect2i(0, 217, 162, 18);
    private static final Rect2i EMPTY_ROW3 = new Rect2i(0, 235, 162, 18);
    private final Map<String, Consumer<com.github.glodblock.epp.network.packet.sync.Paras>> actions = createHolder();
    private final Scrollbar scrollbar;
    private final Long2ReferenceMap<PatternInfo> infos = new Long2ReferenceOpenHashMap<>();
    private final Set<ItemStack> matchedStack = new ObjectOpenCustomHashSet<>(new Hash.Strategy<>() {
        @Override
        public int hashCode(ItemStack o) {
            return o.getItem().hashCode() ^ (o.hasTag() ? o.getTag().hashCode() : 0xFFFFFFFF);
        }

        @Override
        public boolean equals(ItemStack a, ItemStack b) {
            return a == b || (a != null && b != null && ItemStack.isSameItemSameTags(a, b));
        }
    });
    private final ArrayList<PatternRow> rows = new ArrayList<>();
    private final AETextField searchField;
    private int runningThreads = 0;
    private final CycleEPPButton patternShowBtn = new CycleEPPButton();

    public GuiAssemblerMatrix(ContainerAssemblerMatrix menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.scrollbar = widgets.addScrollBar("scrollbar");
        this.searchField = widgets.addTextField("search");
        this.searchField.setResponder(str -> this.refreshList());
        this.searchField.setPlaceholder(GuiText.SearchPlaceholder.text());
        this.searchField.setTooltipMessage(Collections.singletonList(new TranslatableComponent("gui.expatternprovider.assembler_matrix.tooltip")));
        this.actions.put("running_update", o -> this.runningThreads = o.get(0));
        this.actions.put("pattern_mode_update", o -> this.patternShowBtn.setState(o.get(0)));
        var cancel = new ActionEPPButton(b -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("cancel")), Icon.CLEAR);
        cancel.setMessage(new TranslatableComponent("gui.expatternprovider.assembler_matrix.cancel"));
        this.patternShowBtn.addActionPair(Icon.PATTERN_ACCESS_SHOW, GuiText.PatternAccessTerminalHint.text(), b -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("pattern_mode", YesNo.NO.name())));
        this.patternShowBtn.addActionPair(Icon.PATTERN_ACCESS_HIDE, GuiText.PatternAccessTerminalHint.text(), b -> EPPNetworkHandler.INSTANCE.sendToServer(new CGenericPacket("pattern_mode", YesNo.YES.name())));
        addToLeftToolbar(cancel);
        addToLeftToolbar(patternShowBtn);
    }

    @Override
    public void init() {
        super.init();
        this.setInitialFocus(this.searchField);
        this.resetScrollbar();
    }

    @Override
    public boolean mouseClicked(double xCoord, double yCoord, int btn) {
        if (btn == 1 && this.searchField.isMouseOver(xCoord, yCoord)) {
            this.searchField.setValue("");
        }
        return super.mouseClicked(xCoord, yCoord, btn);
    }

    @Override
    public void drawFG(PoseStack poseStack, int offsetX, int offsetY, int mouseX, int mouseY) {
        this.menu.slots.removeIf(slot -> slot instanceof AssemblerMatrixSlot);
        int textColor = style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB();
        final int scrollLevel = scrollbar.getCurrentScroll();
        for (int i = 0; i < 4; i++) {
            if (scrollLevel + i < this.rows.size()) {
                var row = this.rows.get(scrollLevel + i);
                for (int col = 0; col < row.slots; col++) {
                    var slot = new AssemblerMatrixSlot(row.inventory, col, row.offset, row.id, col * SLOT_SIZE + GUI_PADDING_X, (i + 1) * SLOT_SIZE + 13);
                    this.menu.slots.add(slot);
                    if (!this.searchField.getValue().isEmpty()) {
                        if (this.matchedStack.contains(slot.getItem())) {
                            fillRect(poseStack, new Rect2i(slot.x, slot.y, 16, 16), 0x8A00FF00);
                        } else {
                            fillRect(poseStack, new Rect2i(slot.x, slot.y, 16, 16), 0x6A000000);
                        }
                    }
                }
            }
        }
        this.font.draw(poseStack, new TranslatableComponent("gui.expatternprovider.assembler_matrix.threads", this.runningThreads).getVisualOrderText(), 80, 19, textColor);
    }

    @Override
    public void drawBG(PoseStack poseStack, int offsetX, int offsetY, int mouseX, int mouseY, float partialTicks) {
        super.drawBG(poseStack, offsetX, offsetY, mouseX, mouseY, partialTicks);
        int size = this.rows.size();
        if (size < 4) {
            while (size < 4) {
                if (size == 0) {
                    this.bindTexture("guis/assembler_matrix.png");
                    blit(poseStack, offsetX + GUI_PADDING_X - 1, offsetY + SLOT_SIZE * size + 30, EMPTY_ROW1.getX(), EMPTY_ROW1.getY(), EMPTY_ROW1.getWidth(), EMPTY_ROW1.getHeight());
                } else if (size == 3) {
                    this.bindTexture("guis/assembler_matrix.png");
                    blit(poseStack, offsetX + GUI_PADDING_X - 1, offsetY + SLOT_SIZE * size + 30, EMPTY_ROW3.getX(), EMPTY_ROW3.getY(), EMPTY_ROW3.getWidth(), EMPTY_ROW3.getHeight());
                } else {
                    this.bindTexture("guis/assembler_matrix.png");
                    blit(poseStack, offsetX + GUI_PADDING_X - 1, offsetY + SLOT_SIZE * size + 30, EMPTY_ROW2.getX(), EMPTY_ROW2.getY(), EMPTY_ROW2.getWidth(), EMPTY_ROW2.getHeight());
                }
                size++;
            }
        }
    }

    @Override
    protected void slotClicked(Slot slot, int slotIdx, int mouseButton, ClickType clickType) {
        if (slot instanceof AssemblerMatrixSlot machineSlot) {
            InventoryAction action = null;
            switch (clickType) {
                case PICKUP:
                    action = mouseButton == 1 ? InventoryAction.SPLIT_OR_PLACE_SINGLE : InventoryAction.PICKUP_OR_SET_DOWN;
                    break;
                case QUICK_MOVE:
                    action = mouseButton == 1 ? InventoryAction.PICKUP_SINGLE : InventoryAction.SHIFT_CLICK;
                    break;
                case CLONE:
                    if (getPlayer().getAbilities().instabuild) {
                        action = InventoryAction.CREATIVE_DUPLICATE;
                    }
                    break;
                default:
                case THROW:
            }
            if (action != null) {
                final InventoryActionPacket p = new InventoryActionPacket(action, machineSlot.getActuallySlot(), machineSlot.getID());
                NetworkHandler.instance().sendToServer(p);
            }
            return;
        }
        super.slotClicked(slot, slotIdx, mouseButton, clickType);
    }

    public void receiveUpdate(long id, Int2ObjectMap<ItemStack> updateMap) {
        var info = this.infos.computeIfAbsent(id, PatternInfo::new);
        for (var entry : updateMap.int2ObjectEntrySet()) {
            var row = info.getRowBySlot(entry.getIntKey());
            row.setItemByInvSlot(entry.getIntKey(), entry.getValue());
        }
        this.refreshList();
    }

    private void refreshList() {
        this.rows.clear();
        this.matchedStack.clear();
        for (var entry : this.infos.long2ReferenceEntrySet()) {
            var info = entry.getValue();
            for (var row : info.internalRows) {
                if (filterRows(row)) {
                    this.rows.add(row);
                }
            }
        }
        this.resetScrollbar();
    }

    private boolean filterRows(PatternRow row) {
        var filter = this.searchField.getValue();
        if (filter.isBlank()) {
            return true;
        }
        final var token = FCUtil.tokenize(filter);
        boolean anyMatch = false;
        for (var stack : row.inventory) {
            if (itemStackMatchesSearchTerm(stack, token)) {
                anyMatch = true;
            }
        }
        return anyMatch;
    }

    private boolean itemStackMatchesSearchTerm(ItemStack itemStack, List<String> searchTokens) {
        IPatternDetails result = null;
        if (itemStack.getItem() instanceof EncodedPatternItem) {
            result = PatternDetailsHelper.decodePattern(itemStack, this.menu.getPlayer().level);
        }
        if (result == null) {
            return false;
        }
        for (var item : result.getOutputs()) {
            if (item != null) {
                final var nameTokens = FCUtil.tokenize(item.what().getDisplayName().getString());
                if (FCUtil.compareTokens(searchTokens, nameTokens)) {
                    this.matchedStack.add(itemStack);
                    return true;
                }
            }
        }
        for (var item : result.getInputs()) {
            if (item != null) {
                final var nameTokens = FCUtil.tokenize(item.getPossibleInputs()[0].what().getDisplayName().getString());
                if (FCUtil.compareTokens(searchTokens, nameTokens)) {
                    this.matchedStack.add(itemStack);
                    return true;
                }
            }
        }
        return false;
    }

    @NotNull
    @Override
    public Map<String, Consumer<com.github.glodblock.epp.network.packet.sync.Paras>> getActionMap() {
        return this.actions;
    }

    private void resetScrollbar() {
        this.scrollbar.setHeight(4 * ROW_HEIGHT - 2);
        this.scrollbar.setRange(0, this.rows.size() - 4, 2);
    }

    private static class PatternInfo {

        private final List<PatternRow> internalRows = new ArrayList<>();

        PatternInfo(long id) {
            int left = TileAssemblerMatrixPattern.INV_SIZE;
            int offset = 0;
            do {
                this.internalRows.add(new PatternRow(id, offset, Math.min(left, 9)));
                left -= 9;
                offset += 9;
            } while (left > 0);
        }

        PatternRow getRowBySlot(int slot) {
            return this.internalRows.get(slot / 9);
        }

    }

    private static class PatternRow {

        private final AppEngInternalInventory inventory;
        private final long id;
        private final int offset;
        private final int slots;

        PatternRow(long patternID, int offset, int slots) {
            this.id = patternID;
            this.offset = offset;
            this.slots = slots;
            this.inventory = new AppEngInternalInventory(slots);
        }

        void setItemByInvSlot(int slot, ItemStack stack) {
            this.inventory.setItemDirect(slot - offset, stack);
        }

    }

}
