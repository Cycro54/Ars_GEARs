package invoker54.arsgears.capability.gear;

import invoker54.arsgears.capability.gear.utilgear.GearProvider;
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
    private final String ITEM_TAG = "ITEM_TAG";

    private int selectedItem = 0;
    protected CompoundNBT[] itemTags = new CompoundNBT[]{new CompoundNBT(), new CompoundNBT(), new CompoundNBT()};

    public static GearCap getCap(ItemStack item){
        return item.getCapability(GearProvider.CAP_GEAR).orElseThrow(NullPointerException::new);
    }

    @Override
    public int getSelectedItem() {
        return selectedItem;
    }

    @Override
    public void cycleItem(ItemStack gearStack) {
       //Save important current tag shtuff
        saveTag(gearStack.getOrCreateTag());
        //Cycle the item
       selectedItem = (selectedItem == 2 ? 0 : ++selectedItem);
       //Now make sure to read the shtuff
       readTag(gearStack.getOrCreateTag());
    }

    @Override
    public CompoundNBT getTag(int gearCycle){
        return itemTags[gearCycle];
    }

    protected CompoundNBT saveTag(CompoundNBT stackTag){
        CompoundNBT capTag = itemTags[selectedItem];

        if(stackTag.contains("Enchantments")) {
            //Save the stuff
            capTag.put("Enchantments", stackTag.get("Enchantments"));
            //Now remove the stuff
            stackTag.remove("Enchantments");
        }

        return capTag;
    }
    protected CompoundNBT readTag(CompoundNBT stackTag){
        CompoundNBT capTag = itemTags[selectedItem];

        if(capTag.contains("Enchantments")) {
            //Read the shtuff
            stackTag.put("Enchantments", capTag.get("Enchantments"));
        }

        return capTag;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT cNBT = new CompoundNBT();
        cNBT.putInt(SELECTED_ITEM, selectedItem);

        //This is for the item tags
        cNBT.put(ITEM_TAG + (0), itemTags[0]);
        cNBT.put(ITEM_TAG + (1), itemTags[1]);
        cNBT.put(ITEM_TAG + (2), itemTags[2]);
        return cNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        selectedItem = nbt.getInt(SELECTED_ITEM);

        //This is for the item tags
        itemTags[0].merge(nbt.getCompound(ITEM_TAG + (0)));
        itemTags[1].merge(nbt.getCompound(ITEM_TAG + (1)));
        itemTags[2].merge(nbt.getCompound(ITEM_TAG + (2)));
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
