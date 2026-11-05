package com.github.glodblock.epp.util.helper;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;

public interface CraftingRecipePattern {

    ItemStack getOutput();

    CraftingRecipe getRecipeHolder();

}
