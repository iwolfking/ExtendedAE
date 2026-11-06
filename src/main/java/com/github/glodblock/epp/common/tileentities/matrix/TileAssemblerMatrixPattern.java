package com.github.glodblock.epp.common.tileentities.matrix;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.KeyCounter;
import appeng.blockentity.crafting.IMolecularAssemblerSupportedPattern;
import appeng.crafting.pattern.EncodedPatternItem;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import appeng.util.inv.filter.AEItemFilters;
import appeng.util.inv.filter.IAEItemFilter;
import com.github.glodblock.epp.common.EPPItemAndBlock;
import com.github.glodblock.epp.util.FCUtil;
import com.github.glodblock.epp.common.me.matrix.ClusterAssemblerMatrix;
import com.github.glodblock.epp.util.GlodUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TileAssemblerMatrixPattern extends TileAssemblerMatrixFunction implements InternalInventoryHost, ICraftingProvider {

    public final static int INV_SIZE = 36;
    private final AppEngInternalInventory patternInventory;
    private final FilteredInternalInventory exposedInventory;
    private final List<IPatternDetails> patterns = new ArrayList<>();

    public TileAssemblerMatrixPattern(BlockPos pos, BlockState blockState) {
        super(FCUtil.getTileType(TileAssemblerMatrixPattern.class, TileAssemblerMatrixPattern::new, EPPItemAndBlock.ASSEMBLER_MATRIX_PATTERN), pos, blockState);
        this.patternInventory = new AppEngInternalInventory(this, INV_SIZE, 1);
        this.patternInventory.setFilter(new Filter(this::getLevel));
        this.exposedInventory = new FilteredInternalInventory(this.patternInventory, AEItemFilters.INSERT_ONLY);
        this.getMainNode().addService(ICraftingProvider.class, this);
    }

    @Override
    public void saveAdditional(CompoundTag data) {
        super.saveAdditional(data);
        this.patternInventory.writeToNBT(data, "pattern");
    }

    @Override
    public void loadTag(CompoundTag data) {
        super.loadTag(data);
        this.patternInventory.readFromNBT(data, "pattern");
    }

    public AppEngInternalInventory getPatternInventory() {
        return this.patternInventory;
    }

    public FilteredInternalInventory getExposedInventory() {
        return this.exposedInventory;
    }

    public long getLocateID() {
        return this.worldPosition.asLong();
    }

    public void updatePatterns() {
        this.patterns.clear();
        for (var stack : this.patternInventory) {
            var details = PatternDetailsHelper.decodePattern(stack, this.getLevel());
            if (details != null) {
                patterns.add(details);
            }
        }
        ICraftingProvider.requestUpdate(this.getMainNode());
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);
        for (var pattern : this.patternInventory) {
            drops.add(pattern);
        }
    }

    public void clearContent() {
        for (int i = 0; i < this.patternInventory.size(); i++) {
            this.patternInventory.setItemDirect(i, ItemStack.EMPTY);
        }
    }

    @Override
    public void add(ClusterAssemblerMatrix c) {
        c.addPattern(this);
    }

    @Override
    public void onChangeInventory(InternalInventory inv, int slot) {
        this.saveChanges();
        this.updatePatterns();
    }

    @Override
    public void onReady() {
        super.onReady();
        this.updatePatterns();
    }

    @Override
    public List<IPatternDetails> getAvailablePatterns() {
        return this.patterns;
    }

    @Override
    public boolean pushPattern(IPatternDetails patternDetails, KeyCounter[] inputHolder) {
        if (!isFormed() || !this.getMainNode().isActive() || !this.patterns.contains(patternDetails)) {
            return false;
        }
        return this.cluster.pushCraftingJob(patternDetails, inputHolder);
    }

    @Override
    public boolean isBusy() {
        return this.cluster == null || this.cluster.isBusy();
    }

    // (no override)
    public @Nullable IGrid getGrid() {
        return this.getMainNode().getGrid();
    }


    public record Filter(Supplier<Level> world) implements IAEItemFilter {

        @Override
        public boolean allowInsert(InternalInventory inv, int slot, ItemStack stack) {
            if (stack.getItem() instanceof EncodedPatternItem) {
                return PatternDetailsHelper.decodePattern(stack, world.get()) instanceof IMolecularAssemblerSupportedPattern;
            }
            return false;
        }

    }

}
