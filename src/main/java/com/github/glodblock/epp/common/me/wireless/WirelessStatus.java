package com.github.glodblock.epp.common.me.wireless;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;

public enum WirelessStatus {

    UNCONNECTED,
    WORKING,
    REMOTE_ERROR,
    NO_POWER;

    public MutableComponent getTranslation() {
        return new TranslatableComponent("gui.wireless_connect.status." + this.name().toLowerCase());
    }

    public MutableComponent getDesc() {
        return new TranslatableComponent("gui.wireless_connect.status."  + this.name().toLowerCase() + ".desc");
    }

}
