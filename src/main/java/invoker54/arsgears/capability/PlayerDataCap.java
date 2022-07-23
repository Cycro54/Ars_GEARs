package invoker54.arsgears.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class PlayerDataCap implements IPlayerCap{
    @Override
    public CompoundNBT serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {

    }

    @Override
    public ItemStack getUtilityGear() {
        return null;
    }

    @Override
    public ItemStack getCombatGear() {
        return null;
    }
}
