package invoker54.arsgears.capability.gear;

import invoker54.arsgears.capability.gear.utilgear.UtilGearProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class GearCap implements IGearCap {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String SELECTED_ITEM = "SELECTED_ITEM";

    private int selectedItem = 0;

    public static GearCap getCap(ItemStack item){
        return item.getCapability(UtilGearProvider.CAP_UTILITY_GEAR).orElseThrow(NullPointerException::new);
    }

    @Override
    public int getSelectedItem() {
        return selectedItem;
    }

    @Override
    public void cycleItem() {
       selectedItem = (selectedItem == 2 ? 0 : ++selectedItem);
       //LOGGER.debug("I am cycling selected item to: " + selectedItem);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT cNBT = new CompoundNBT();
        cNBT.putInt(SELECTED_ITEM, selectedItem);
        return cNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        selectedItem = nbt.getInt(SELECTED_ITEM);
    }

    public static class GearNBTStorage implements Capability.IStorage<GearCap>{

        @Nullable
        @Override
        public INBT writeNBT(Capability<GearCap> capability, GearCap instance, Direction side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<GearCap> capability, GearCap instance, Direction side, INBT nbt) {
            CompoundNBT mainNbt = (CompoundNBT) nbt;

            instance.deserializeNBT(mainNbt);
        }


    }

}
