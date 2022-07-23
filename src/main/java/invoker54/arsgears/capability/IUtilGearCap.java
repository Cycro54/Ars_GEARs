package invoker54.arsgears.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface IUtilGearCap extends INBTSerializable<CompoundNBT> {
    int getSelectedItem();

    int getTier();

    void cycleItem();

}
