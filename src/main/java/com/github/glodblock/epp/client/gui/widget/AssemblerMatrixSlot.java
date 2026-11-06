package com.github.glodblock.epp.client.gui.widget;

import appeng.crafting.pattern.EncodedPatternItem;
import appeng.menu.slot.AppEngSlot;
import appeng.util.inv.AppEngInternalInventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class AssemblerMatrixSlot extends AppEngSlot {

    private final AppEngInternalInventory machineInv;
    private final long id;
    private final int offset;

    public AssemblerMatrixSlot(AppEngInternalInventory machineInv, int machineInvSlot, int offset, long id, int x, int y) {
        super(machineInv, machineInvSlot);
        this.machineInv = machineInv;
        this.id = id;
        this.offset = offset;
        try {
            java.lang.reflect.Field xf = net.minecraft.world.inventory.Slot.class.getDeclaredField("f_40220_");
            xf.setAccessible(true);
            xf.setInt(this, x);
            java.lang.reflect.Field yf = net.minecraft.world.inventory.Slot.class.getDeclaredField("f_40221_");
            yf.setAccessible(true);
            yf.setInt(this, y);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public int getActuallySlot() {
        return this.getSlotIndex() + this.offset;
    }

    public long getID() {
        return this.id;
    }

    @Override
    public ItemStack getDisplayStack() {
        if (isRemote()) {
            final ItemStack is = super.getDisplayStack();
            if (!is.isEmpty() && is.getItem() instanceof EncodedPatternItem iep) {
                final ItemStack out = iep.getOutput(is);
                if (!out.isEmpty()) {
                    return out;
                }
            }
        }
        return super.getDisplayStack();
    }

    @Override
    public boolean hasItem() {
        return !this.getItem().isEmpty();
    }

    public AppEngInternalInventory getMachineInv() {
        return this.machineInv;
    }

    @Override
    public final boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public final void set(ItemStack stack) {
    }

    // (no override in 1.18)
    public void initialize(ItemStack stack) {
    }

    @Override
    public final int getMaxStackSize() {
        return 0;
    }

    @Override
    public final ItemStack remove(int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public final boolean mayPickup(Player player) {
        return false;
    }
}
