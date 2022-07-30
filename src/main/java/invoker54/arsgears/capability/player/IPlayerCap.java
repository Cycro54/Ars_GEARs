package invoker54.arsgears.capability.player;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface IPlayerCap extends INBTSerializable<CompoundNBT> {

    ItemStack getUtilityGear(boolean getCopy);

    void setUtilityGear(ItemStack heldItem);

    void syncUtilityGearData();

    //Combat gear data
    ItemStack getCombatGear(boolean getCopy);
    
    void setCombatGear(ItemStack heldItem);

    void syncCombatGearData();
}
