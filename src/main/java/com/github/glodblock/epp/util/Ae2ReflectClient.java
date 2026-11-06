package com.github.glodblock.epp.util;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;

import java.lang.reflect.Method;

public class Ae2ReflectClient {
    private static final Method mFindSlot;
    static {
        Method m = null;
        for (String name : new String[]{"findSlot","m_97744_"}) {
            try {
                m = AbstractContainerScreen.class.getDeclaredMethod(name, double.class, double.class);
                break;
            } catch (NoSuchMethodException ignored) {}
        }
        if (m == null) throw new IllegalStateException("findSlot missing");
        m.setAccessible(true);
        mFindSlot = m;
    }
    public static Slot getSlot(AbstractContainerScreen<?> owner, double x, double y) {
        try { return (Slot) mFindSlot.invoke(owner, x, y); }
        catch (Exception e) { throw new IllegalStateException(e); }
    }
}
