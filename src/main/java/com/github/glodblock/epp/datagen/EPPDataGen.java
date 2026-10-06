package com.github.glodblock.epp.datagen;

import com.github.glodblock.epp.EPP;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = EPP.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EPPDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent dataEvent) {
        var gen = dataEvent.getGenerator();
        var file = dataEvent.getExistingFileHelper();
        var block = new EPPBlockTagProvider(gen, file);
        gen.addProvider(block);
        gen.addProvider(new EPPRecipeProvider(gen));
        gen.addProvider(new EPPLootTableProvider(gen));
        gen.addProvider(new EPPItemTagsProvider(gen, block, file));
    }

}