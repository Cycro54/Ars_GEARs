package invoker54.arsgears.capability;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class UtilGearCap implements IUtilGearCap{
    private static final Logger LOGGER = LogManager.getLogger();
    public String SELECTED_ITEM = "SELECTED_ITEM";
    public String TIER = "TIER";

    private int selectedItem = 0;
    private int tier = 0;

    public static UtilGearCap getCap(ItemStack item){
        return item.getCapability(UtilGearProvider.CAP_UTILITY_GEAR).orElseThrow(NullPointerException::new);
    }

    @Override
    public int getSelectedItem() {
        return selectedItem;
    }

    @Override
    public int getTier() {
        return tier;
    }

    @Override
    public void cycleItem() {
       selectedItem = (selectedItem == 2 ? 0 : ++selectedItem);
        LOGGER.debug("I am cycling selected item to: " + selectedItem);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT cNBT = new CompoundNBT();
        cNBT.putInt(SELECTED_ITEM, selectedItem);
        cNBT.putInt(TIER, tier);
        return cNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        selectedItem = nbt.getInt(SELECTED_ITEM);
        tier = nbt.getInt(TIER);
    }

    public static class UtilGearNBTStorage implements Capability.IStorage<UtilGearCap>{

        @Nullable
        @Override
        public INBT writeNBT(Capability<UtilGearCap> capability, UtilGearCap instance, Direction side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<UtilGearCap> capability, UtilGearCap instance, Direction side, INBT nbt) {
            CompoundNBT mainNbt = (CompoundNBT) nbt;

            instance.deserializeNBT(mainNbt);
        }


    }

}
