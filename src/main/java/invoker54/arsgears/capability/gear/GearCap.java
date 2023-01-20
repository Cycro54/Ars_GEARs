package invoker54.arsgears.capability.gear;

import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.capability.player.PlayerDataCap;
import invoker54.arsgears.init.SoundsInit;
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
    protected ItemStack gearStack;

    public GearTier getTier(){
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

    public GearCap(){}

    public GearCap(ItemStack gearStack){
        this.gearStack = gearStack;
        if (this instanceof CombatGearCap) {
            //Starter Sword
            itemTags[0].putString("id", WOODEN_MOD_SWORD.getRegistryName().toString());
            //Starter Bow
            itemTags[1].putString("id", WOODEN_MOD_BOW.getRegistryName().toString());
            //Starter Mirror
            itemTags[2].putString("id", WOODEN_MOD_MIRROR.getRegistryName().toString());
        }
        else {
            //Starter Paxel
            itemTags[0].putString("id", WOOD_PAXEL.getRegistryName().toString());
            //Starter Fishing Rod
            itemTags[1].putString("id", WOOD_FISHING_ROD.getRegistryName().toString());
            //Starter Hoe
            itemTags[2].putString("id", WOOD_HOE.getRegistryName().toString());
        }
    }

    @Override
    public int getSelectedItem() {
        return selectedItem;
    }

    @Override
    public void cycleItem(ItemStack gearStack, PlayerEntity player) {
        int prevSelect = getSelectedItem();

        //Have to make the changes now if I wish to keep them
        selectedItem = (selectedItem == 2 ? 0 : ++selectedItem);

        //Play a sound (Utility will be higher pitch than Combat)
        float pitch = (0.7F + (((selectedItem + 1) / 3F) * 0.3F));
        player.level.playSound(null, player.blockPosition(),
                SoundsInit.GEAR_SWITCH, player.getSoundSource(), 0.8F, pitch);

        CompoundNBT mainNBT = gearStack.serializeNBT();
        CompoundNBT tagNBT = gearStack.getOrCreateTag();

        //Save important current tag shtuff (while also removing the saved stuff from the mainNBT)
//        LOGGER.debug("WHATS mainNBT id BEFORE edit? " + (mainNBT.getString("id")));
        saveTag(mainNBT, tagNBT, itemTags[prevSelect]);

        //Now load important current tag shtuff
        loadTag(mainNBT, tagNBT, itemTags[selectedItem]);
//        LOGGER.debug("WHATS mainNBT id AFTER edite? " + (mainNBT.getString("id")));

        //Place tagNBT back into the mainNBT (just in case it wasn't in there already)
        mainNBT.put("tag", tagNBT);

        //Make sure to set the tracked combat item or else it will change
        PlayerDataCap cap = PlayerDataCap.getCap(player);

        //make a new item with the modified mainNBT
        gearStack = ItemStack.of(mainNBT);

        if (CombatGearCap.getCap(gearStack) == null) {
            cap.upgradeUtilityGear(gearStack);
        }
        else {
            cap.upgradeCombatGear(gearStack);
        }
    }

    @Override
    public CompoundNBT getTag(int gearCycle){
        return (getSelectedItem() == gearCycle) ? this.gearStack.getOrCreateTag() : itemTags[gearCycle];
    }

    protected void saveTag(CompoundNBT mainNBT, CompoundNBT tagNBT, CompoundNBT capNBT){
        if(tagNBT.contains("Enchantments")) {
            //Save the stuff
            capNBT.put("Enchantments", tagNBT.get("Enchantments"));
            //Now remove the stuff
            tagNBT.remove("Enchantments");
        }

        //id is the actual item
        if (mainNBT.contains("id")){
            capNBT.putString("id", mainNBT.getString("id"));
            LOGGER.debug("WHATS THE OLD ID? " + mainNBT.getString("id"));
        }
    }
    protected void loadTag(CompoundNBT mainNBT, CompoundNBT tagNBT, CompoundNBT capNBT){
        if(capNBT.contains("Enchantments")) {
            //Read the shtuff
            tagNBT.put("Enchantments", capNBT.get("Enchantments"));
        }

        //id is the actual item
        if (capNBT.contains("id")){
            mainNBT.putString("id", capNBT.getString("id"));
            LOGGER.debug("WHATS THE NEW ID? " + capNBT.getString("id"));
        }
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
