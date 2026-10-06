package com.github.glodblock.epp.common.me.wireless;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public enum WirelessFail {

    OUT_OF_RANGE,
    SELF_REFERENCE,
    CROSS_DIMENSION,
    MISSING;

    public Component getTranslation() {
        return new TranslatableComponent("chat.wireless_connect." + this.name().toLowerCase()).withStyle(ChatFormatting.RED);
    }

}
