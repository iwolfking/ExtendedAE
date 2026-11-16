package com.github.glodblock.epp.integration.advanced_ae;

import appeng.api.config.Settings;
import appeng.api.config.ShowPatternProviders;
import appeng.api.config.YesNo;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import com.github.glodblock.epp.container.AdvancedInterfaceTerminalMenu;
import net.minecraft.network.chat.Component;
import net.pedroksl.advanced_ae.common.entities.AdvPatternProviderEntity;
import net.pedroksl.advanced_ae.common.entities.SmallAdvPatternProviderEntity;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;
import net.pedroksl.advanced_ae.common.parts.AdvPatternProviderPart;
import net.pedroksl.advanced_ae.common.parts.SmallAdvPatternProviderPart;

import java.util.*;

public class AdvancedAEHelper {

    public static boolean isAdvancedHost(Object host) {
        return host instanceof AdvPatternProviderLogicHost;
    }

    public static boolean handleRemovingProvider(Object host, IGrid grid) {
        if (host instanceof AdvPatternProviderLogicHost provider) {
            return provider.getGrid() != grid;
        }

        return true;
    }

    public static void addAdvancedPatternProviders(IGrid grid, AdvancedInterfaceTerminalMenu menu) {
        for (var ih : getAllActiveAdvancedMachines(grid)) {
            if (!isHostVisible(ih, menu)) {
                continue;
            }

            InternalInventory inventory = ih.getTerminalPatternInventory();
            Component name;

            if(ih instanceof AdvPatternProviderPart advPatternProviderPart) {
                name = advPatternProviderPart.getCustomInventoryName();
            }
            else {
                name = ih.getBlockEntity().getBlockState().getBlock().getName();
            }

            List<AdvancedInterfaceTerminalMenu.InvTracker> list = menu.diList.computeIfAbsent(ih, i -> new ArrayList<>());

            for (int i = 0; i < inventory.size() / 9; i++) {
                list.add(new AdvancedInterfaceTerminalMenu.InvTracker(inventory, name, i));
            }
        }
    }

    public static void visitAdvancedPatternProviders(IGrid grid, AdvancedInterfaceTerminalMenu.VisitorState state, AdvancedInterfaceTerminalMenu menu) {
        for (var ih : getAllActiveAdvancedMachines(grid)) {
            if (!isHostVisible(ih, menu)) {
                continue;
            }

            if (menu.getShownProviders() == ShowPatternProviders.NOT_FULL) {
                menu.pinnedHosts.add(ih);
            }

            Component name;

            if(ih instanceof AdvPatternProviderPart advPatternProviderPart) {
                name = advPatternProviderPart.getCustomInventoryName();
            }
            else {
                name = ih.getBlockEntity().getBlockState().getBlock().getName();
            }

            for (AdvancedInterfaceTerminalMenu.InvTracker t : menu.diList.getOrDefault(ih, Collections.emptyList())) {
                if (t == null || !t.name.equals(name)) {
                    state.forceFullUpdate = true;
                }
            }

            state.total++;
        }
    }

    private static Set<AdvPatternProviderLogicHost> getAllActiveAdvancedMachines(IGrid grid) {
        Set<AdvPatternProviderLogicHost> machineEntities = new HashSet<>(
                grid.getActiveMachines(AdvPatternProviderEntity.class));

        machineEntities.addAll(
                grid.getActiveMachines(SmallAdvPatternProviderEntity.class));

        machineEntities.addAll(
                grid.getActiveMachines(AdvPatternProviderPart.class));

        machineEntities.addAll(
                grid.getActiveMachines(SmallAdvPatternProviderPart.class));

        return machineEntities;
    }

    private static boolean isHostVisible(AdvPatternProviderLogicHost host, AdvancedInterfaceTerminalMenu menu) {
        var inventory = host.getTerminalPatternInventory();

        boolean isVisible = host.getConfigManager()
                .getSetting(Settings.PATTERN_ACCESS_TERMINAL) == YesNo.YES;

        return switch (menu.getShownProviders()) {
            case VISIBLE -> isVisible;
            case NOT_FULL -> isVisible && (menu.pinnedHosts.contains(host)
                    || !menu.isFull(inventory));
            case ALL -> true;
        };
    }
}
