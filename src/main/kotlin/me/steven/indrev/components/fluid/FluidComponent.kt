package me.steven.indrev.components.fluid

import alexiil.mc.lib.attributes.fluid.FluidTransferable
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume
import me.steven.indrev.api.sideconfigs.ConfigurationType
import me.steven.indrev.api.sideconfigs.SideConfiguration
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.collection.DefaultedList

open class FluidComponent(val blockEntity: BlockEntity?, val limit: FluidAmount, tankCount: Int = 1) : SimpleFixedFluidInv(tankCount, limit) {

    init {
        addListener({ _, _, _, _ ->
            blockEntity?.markDirty()
            if (blockEntity?.world?.isClient == false)
                    (blockEntity as? BlockEntityClientSerializable)?.sync()
        }, {})
    }


    val tanks: DefaultedList<FluidVolume>
        get() = tanks

    val transferConfig: SideConfiguration = SideConfiguration(ConfigurationType.FLUID)

    operator fun set(tank: Int, volume: FluidVolume) {
        this.tanks[tank] = volume
    }

    operator fun get(tank: Int): FluidVolume = tanks[tank]

    open fun getInteractInventory(tank: Int): FluidTransferable = super.getTank(tank)

    override fun toTag(tag: CompoundTag): CompoundTag {
        val tanksTag = CompoundTag()
        tanks.forEachIndexed { index, tank ->
            val tankTag = CompoundTag()
            tankTag.put("fluids", tank.toTag())
            tanksTag.put(index.toString(), tankTag)
        }
        tag.put("tanks", tanksTag)
        transferConfig.toTag(tag)
        return tag
    }

    override fun fromTag(tag: CompoundTag?) {
        super.fromTag(tag)
        val tanksTag = tag?.getCompound("tanks")
        tanksTag?.keys?.forEach { key ->
            val index = key.toInt()
            val tankTag = tanksTag.getCompound(key)
            val volume = FluidVolume.fromTag(tankTag.getCompound("fluids"))
            tanks[index] = volume
        }

        transferConfig.fromTag(tag)
    }
}