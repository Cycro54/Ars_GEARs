package invoker54.arsgears.capability.player;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface IPlayerCap extends INBTSerializable<CompoundNBT> {

    ItemStack getUtilityGear();

    void setUtilityGear(ItemStack heldItem);

    void syncUtilityGearData();

    //Combat gear data
    ItemStack getCombatGear();
}
