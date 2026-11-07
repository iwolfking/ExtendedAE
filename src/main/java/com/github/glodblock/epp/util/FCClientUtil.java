package com.github.glodblock.epp.util;

import com.github.glodblock.epp.xmod.LoadList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class FCClientUtil {

    public static String getModName(String inputText) {
        var trimInput = inputText.trim();
        if (trimInput.isEmpty() || trimInput.endsWith(",")) {
            return "";
        }
        var ids = FCUtil.trimSplit(inputText);
        var set = new ObjectOpenHashSet<>(ids);
        for (String mod : LoadList.MOD_NAME) {
            if (set.contains(mod)) {
                continue;
            }
            String modid = ids[ids.length - 1];
            if (mod.startsWith(modid)) {
                int pos = mod.indexOf(modid);
                return mod.substring(pos + modid.length());
            }
        }
        return "";
    }

}
