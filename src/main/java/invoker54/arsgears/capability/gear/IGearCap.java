package invoker54.arsgears.capability.gear;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Map;

public interface IGearCap extends INBTSerializable<CompoundNBT> {
    int getSelectedItem();

    void cycleItem(ItemStack gearStack);

    CompoundNBT getTag(int itemToUpdate);

    CompoundNBT getUpgrades(int gearCycle);
}
