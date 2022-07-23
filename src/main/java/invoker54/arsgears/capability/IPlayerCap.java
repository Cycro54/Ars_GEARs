package invoker54.arsgears.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface IPlayerCap extends INBTSerializable<CompoundNBT> {

    //Utility gear data
    ItemStack getUtilityGear();

    //Combat gear data
    ItemStack getCombatGear();



}
