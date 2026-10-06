package com.github.glodblock.epp.container;

import appeng.api.config.SecurityPermissions;
import appeng.helpers.iface.PatternProviderLogicHost;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.implementations.PatternProviderMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public class ContainerExPatternProvider extends PatternProviderMenu {

    public static final MenuType<ContainerExPatternProvider> TYPE = MenuTypeBuilder
            .create(ContainerExPatternProvider::new, PatternProviderLogicHost.class)
            .requirePermission(SecurityPermissions.BUILD)
            .build("ex_pattern_provider");

    public ContainerExPatternProvider(int id, Inventory playerInventory, PatternProviderLogicHost host) {
        super(id, playerInventory, host);
    }

    @Override
    public MenuType<?> getType() {
        return TYPE;
    }
}