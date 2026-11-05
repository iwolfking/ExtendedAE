package com.github.glodblock.epp.util;
import com.github.glodblock.epp.EPP;

import com.github.glodblock.epp.common.me.wireless.WirelessFail;
import com.github.glodblock.epp.common.tileentities.TileWirelessConnector;
import com.github.glodblock.epp.common.tileentities.TileWirelessHub;
import com.github.glodblock.epp.config.EPPConfig;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

public class WirelessHelpers {
    public static boolean hasNoPorts(TileWirelessHub tile, Player player) {
        if (tile.allocatePort() < 0) {
            player.displayClientMessage(WirelessFail.OUT_OF_PORT.getTranslation(), true);

            return true;
        }

        return false;
    }

    public static void bindWireless(CompoundTag nbt, long freq, GlobalPos pos) {
        nbt.putLong("freq", freq);

        GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, pos)
            .result()
            .ifPresent(tag -> nbt.put("bind", tag));
    }

    public static InteractionResult connectWireless(Consumer<Long> setThisFreq, CompoundTag nbt, Level world, BlockPos thisPos, Player player, Runnable onSuccess) {
        return connectWireless(setThisFreq, nbt, world, thisPos, player, onSuccess, () -> {});
    }

    public static InteractionResult connectWireless(Consumer<Long> setThisFreq, CompoundTag nbt, Level world, BlockPos thisPos, Player player, Runnable onSuccess, Runnable onBroken) {
        if (!nbt.contains("bind")) {
            player.displayClientMessage(WirelessFail.MISSING.getTranslation(), true);

            return InteractionResult.FAIL;
        }

        var globalPos = GlobalPos.CODEC.decode(NbtOps.INSTANCE, nbt.get("bind"))
            .resultOrPartial(Util.prefix("Connector position", s -> {}))
            .map(Pair::getFirst)
            .orElse(null);

        if (globalPos == null) {
            player.displayClientMessage(WirelessFail.MISSING.getTranslation(), true);

            return InteractionResult.FAIL;
        }

        var otherPos = globalPos.pos();
        var otherWorld = globalPos.dimension();
        var thisWorld = world.dimension();

        if (otherPos.equals(thisPos) && otherWorld.equals(thisWorld)) {
            player.displayClientMessage(WirelessFail.SELF_REFERENCE.getTranslation(), true);

            return InteractionResult.FAIL;
        }

        if (!otherWorld.equals(thisWorld)) {
            player.displayClientMessage(WirelessFail.CROSS_DIMENSION.getTranslation(), true);

            return InteractionResult.FAIL;
        }

        if (Math.sqrt(otherPos.distSqr(thisPos)) > EPPConfig.wirelessMaxRange) {
            player.displayClientMessage(WirelessFail.OUT_OF_RANGE.getTranslation(), true);

            return InteractionResult.FAIL;
        }

        var otherWorldInstance = world.getServer().getLevel(otherWorld);

        if (otherWorldInstance == null) {
            player.displayClientMessage(WirelessFail.MISSING.getTranslation(), true);

            return InteractionResult.FAIL;
        }

        var otherTile = otherWorldInstance.getBlockEntity(globalPos.pos());
        var freq = nbt.getLong("freq");

        if (otherTile instanceof TileWirelessConnector otherConnector) {
            otherConnector.setFrequency(freq);
            setThisFreq.accept(freq);
            onSuccess.run();
            player.displayClientMessage(new net.minecraft.network.chat.TranslatableComponent("chat.wireless_connect", thisPos.getX(), thisPos.getY(), thisPos.getZ()), true);

            return InteractionResult.sidedSuccess(world.isClientSide);
        } else if (otherTile instanceof TileWirelessHub otherHub) {
            int otherPort = otherHub.allocatePort();
            if (hasNoPorts(otherHub, player)) return InteractionResult.FAIL;
            otherHub.setFrequency(freq, otherPort);
            setThisFreq.accept(freq);
            onSuccess.run();
            player.displayClientMessage(new TranslatableComponent("chat.wireless_connect", thisPos.getX(), thisPos.getY(), thisPos.getZ()), true);

            return InteractionResult.sidedSuccess(world.isClientSide);
        } else {
            if (otherWorldInstance.isLoaded(otherPos)) onBroken.run();

            player.displayClientMessage(WirelessFail.MISSING.getTranslation(), true);

            return InteractionResult.FAIL;
        }
    }
}
