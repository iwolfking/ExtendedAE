package com.github.glodblock.epp.common.parts;

import appeng.api.parts.IPartItem;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.reporting.PatternAccessTerminalPart;
import com.github.glodblock.epp.container.AdvancedInterfaceTerminalMenu;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class AdvancedPatternAccessTerminalPart extends PatternAccessTerminalPart {
    public AdvancedPatternAccessTerminalPart(IPartItem<?> partItem) {
        super(partItem);
    }

    @Override
    public boolean onPartActivate(Player player, InteractionHand hand, Vec3 pos) {
        if (!isClientSide()) {
            MenuOpener.open(AdvancedInterfaceTerminalMenu.TYPE, player, MenuLocators.forPart(this));
        }
        return true;
    }
}