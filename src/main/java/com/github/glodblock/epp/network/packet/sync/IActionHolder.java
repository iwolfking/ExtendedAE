package com.github.glodblock.epp.network.packet.sync;

import java.util.Map;
import java.util.function.Consumer;

public interface IActionHolder {
    Map<String, Consumer<Paras>> getActionMap();
    default Map<String, Consumer<Paras>> createHolder() {
        return new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>();
    }
}
