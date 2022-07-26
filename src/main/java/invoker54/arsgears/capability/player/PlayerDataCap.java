package invoker54.arsgears.capability.player;

import invoker54.arsgears.init.ItemInit;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class PlayerDataCap implements IPlayerCap {
    private static final Logger LOGGER = LogManager.getLogger();

    private final String UTILITY_GEAR_DATA = "UTILITY_GEAR_DATA";

    private final ItemStack utility_gear_copy;
    private ItemStack tracked_utility_gear;

    private ItemStack combat_gear;
    public PlayerDataCap(){
        utility_gear_copy = new ItemStack(ItemInit.WOOD_UTILITY_GEAR);
        tracked_utility_gear = ItemStack.EMPTY;
    }

    public static PlayerDataCap getCap(LivingEntity player){
        return player.getCapability(PlayerDataProvider.CAP_PLAYER_DATA).orElseThrow(NullPointerException::new);
    }

    @Override
    public ItemStack getUtilityGear() {
        return tracked_utility_gear;
    }

    //This will set an item stack to be the currently held utility gear
    @Override
    public void setUtilityGear(ItemStack heldItem) {
        LOGGER.info("I am setting the held utility gear");
        //Set the item stack as the held_utility_gear
        tracked_utility_gear = heldItem;

        //Next make sure that itemstack is up to date with the utility_gear_copy
        heldItem.deserializeNBT(utility_gear_copy.serializeNBT());
    }

    @Override
    public void syncUtilityGearData() {
        //If they are already synced, don't sync it.
        if (ItemStack.matches(tracked_utility_gear, utility_gear_copy)) return;
        //if (held_utility_gear.equals(utility_gear_copy, false)) return null;

        //Now copy the held utility gear data over to the utility gear copy
        CompoundNBT cNBT = tracked_utility_gear.serializeNBT();
        utility_gear_copy.deserializeNBT(cNBT);

        LOGGER.info("I AM SYNCING THE UTILITY GEAR DATA RIGHT NOW");
    }

    @Override
    public ItemStack getCombatGear() {
        return null;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT cNBT = new CompoundNBT();
        cNBT.put(UTILITY_GEAR_DATA, utility_gear_copy.serializeNBT());
        return cNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        utility_gear_copy.deserializeNBT(nbt.getCompound(UTILITY_GEAR_DATA));
    }

    public static class PlayerDataNBTStorage implements Capability.IStorage<PlayerDataCap>{

        @Nullable
        @Override
        public INBT writeNBT(Capability<PlayerDataCap> capability, PlayerDataCap instance, Direction side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<PlayerDataCap> capability, PlayerDataCap instance, Direction side, INBT nbt) {
            CompoundNBT mainNbt = (CompoundNBT) nbt;

            instance.deserializeNBT(mainNbt);
        }


    }
}
