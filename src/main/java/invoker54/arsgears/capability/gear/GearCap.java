package invoker54.arsgears.capability.gear;

import invoker54.arsgears.capability.gear.utilgear.GearProvider;
import invoker54.arsgears.capability.player.PlayerDataCap;
import invoker54.arsgears.item.GearTier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

import static invoker54.arsgears.init.ItemInit.*;

public class GearCap implements IGearCap {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String SELECTED_ITEM = "SELECTED_ITEM";
    private final String ITEM_TAG = "ITEM_TAG";
    private final String TIER = "TIER";

    private GearTier gearTier = GearTier.WOOD;

    public GearTier GetTier(){
        return gearTier;
    }
    public void setTier(GearTier gearTier){
        this.gearTier = gearTier;
    }

    private int selectedItem = 0;
    protected CompoundNBT[] itemTags = new CompoundNBT[]{new CompoundNBT(), new CompoundNBT(), new CompoundNBT()};

    public static GearCap getCap(ItemStack item){
        return item.getCapability(GearProvider.CAP_GEAR).orElseGet(() -> null);
    }

    public GearCap(){
        //Starter Sword
        itemTags[0].putString("id", WOODEN_MOD_SWORD.getRegistryName().toString());
        //Starter Bow
        itemTags[1].putString("id", WOODEN_MOD_BOW.getRegistryName().toString());
        //Starter Mirror
        itemTags[2].putString("id", WOODEN_MOD_MIRROR.getRegistryName().toString());
    }

    @Override
    public int getSelectedItem() {
        return selectedItem;
    }

    @Override
    public void cycleItem(ItemStack gearStack, PlayerEntity player) {
       //Save important current tag shtuff
        saveTag(gearStack.getOrCreateTag());
        //Cycle the selected item
       selectedItem = (selectedItem == 2 ? 0 : ++selectedItem);
       //Change the current itemstack
        ItemStack oldStack = gearStack;
//        switch (selectedItem) {
//            default:
//                gearStack = new ItemStack(WOODEN_MOD_SWORD);
//                ArsUtil.replaceItemStack(player, oldStack, gearStack);
//                break;
//            case 1:
//                gearStack = new ItemStack(WOODEN_MOD_BOW);
//                ArsUtil.replaceItemStack(player, oldStack, gearStack);
//                break;
//            case 2:
//                gearStack = new ItemStack(WOODEN_MOD_MIRROR);
//                ArsUtil.replaceItemStack(player, oldStack, gearStack);
//                break;
//        }
        //grab the data from the old itemStack
        //Make a new itemstack with tag provided by Oldstack and the capability
//        LOGGER.warn("WHATS GEARSTACKS ID BEFORE? " + (gearStack.serializeNBT().getString("id")));
        gearStack = ItemStack.of(readTag(oldStack.serializeNBT()));
//
//        LOGGER.warn("WHATS CAPABILITY ID? " + (readTag(new CompoundNBT()).getString("id")));
//        LOGGER.warn("WHATS OLDSTACKS ID? " + (oldStack.serializeNBT().getString("id")));
//        LOGGER.warn("WHATS GEARSTACKS ID? " + (gearStack.serializeNBT().getString("id")));

        //Make sure to set the tracked combat item or else it will change
        PlayerDataCap cap = PlayerDataCap.getCap(player);
        cap.upgradeCombatGear(gearStack);
    }

    @Override
    public CompoundNBT getTag(int gearCycle){
        return itemTags[gearCycle];
    }

    protected CompoundNBT saveTag(CompoundNBT stackTag){
        //Only certain data is stored in this not all of it
        CompoundNBT capTag = getTag(selectedItem);

        if(stackTag.contains("Enchantments")) {
            //Save the stuff
            capTag.put("Enchantments", stackTag.get("Enchantments"));
            //Now remove the stuff
            stackTag.remove("Enchantments");
        }

        //id is the actual item
        if (stackTag.contains("id")){
            capTag.putString("id", stackTag.getString("id"));
        }

        return capTag;
    }
    protected CompoundNBT readTag(CompoundNBT stackTag){
        CompoundNBT capTag = itemTags[selectedItem];

        if(capTag.contains("Enchantments")) {
            //Read the shtuff
            stackTag.put("Enchantments", capTag.get("Enchantments"));
        }

        //id is the actual item
        if (capTag.contains("id")){
            stackTag.putString("id", capTag.getString("id"));
        }

        return stackTag;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT cNBT = new CompoundNBT();

        //Saves the currently selected item
        cNBT.putInt(SELECTED_ITEM, selectedItem);

        //Saves the gearTier
        cNBT.putInt(TIER, gearTier.ordinal());

        //This is for the item tags
        cNBT.put(ITEM_TAG + (0), itemTags[0]);
        cNBT.put(ITEM_TAG + (1), itemTags[1]);
        cNBT.put(ITEM_TAG + (2), itemTags[2]);
        return cNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        //Sets the currently selected gear
        selectedItem = nbt.getInt(SELECTED_ITEM);

        //Sets the gear tier
        gearTier = GearTier.values()[nbt.getInt(TIER)];

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
