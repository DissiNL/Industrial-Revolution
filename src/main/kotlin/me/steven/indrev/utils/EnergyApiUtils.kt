package me.steven.indrev.utils

import dev.technici4n.fasttransferlib.api.ContainerItemContext
import dev.technici4n.fasttransferlib.api.energy.EnergyApi
import dev.technici4n.fasttransferlib.api.energy.EnergyIo
import dev.technici4n.fasttransferlib.api.item.ItemKey
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import java.util.function.LongFunction

private val ENERGY_IO_CACHE = Long2ObjectOpenHashMap<BlockApiCache<EnergyIo, Direction>>()

fun energyOf(world: ServerWorld, blockPos: BlockPos, direction: Direction): EnergyIo? {
    return ENERGY_IO_CACHE.computeIfAbsent(blockPos.asLong(), LongFunction { BlockApiCache.create(EnergyApi.SIDED, world, blockPos) })[direction]
}

fun energyOf(itemStack: ItemStack?): EnergyIo? {
    return EnergyApi.ITEM.get(ItemKey.of(itemStack), ContainerItemContext.ofStack(itemStack))
}