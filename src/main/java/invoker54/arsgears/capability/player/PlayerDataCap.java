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
    private final String COMBAT_GEAR_DATA = "COMBAT_GEAR_DATA";

    private ItemStack utility_gear_copy;
    private ItemStack utility_gear_tracked;

    private ItemStack combat_gear_copy;
    private ItemStack combat_gear_tracked;
    public PlayerDataCap(){
        utility_gear_copy = new ItemStack(ItemInit.WOOD_PAXEL);
        utility_gear_tracked = ItemStack.EMPTY;
        
        combat_gear_copy = new ItemStack(ItemInit.WOODEN_MOD_SWORD);
        combat_gear_tracked = ItemStack.EMPTY;

        //Just extra stuff for the combat gear so the selected spell isn't 0
        combat_gear_copy.getOrCreateTag().putInt("mode", 1);
    }

    public static PlayerDataCap getCap(LivingEntity player){
        return player.getCapability(PlayerDataProvider.CAP_PLAYER_DATA).orElseThrow(NullPointerException::new);
    }

    @Override
    public ItemStack getUtilityGear() {
        if (utility_gear_tracked.getItem() != utility_gear_copy.getItem()){
            utility_gear_tracked = utility_gear_copy.copy();
        }

        return utility_gear_tracked;
    }

    //This will set an item stack to be the currently held utility gear
    @Override
    public void upgradeUtilityGear(ItemStack upgrade) {
        utility_gear_copy = upgrade.copy();

        LOGGER.info("I am setting the held utility gear");
        //Set the item stack as the held_utility_gear (this doesn't change the itemstack in the players inv)
        utility_gear_tracked = ItemStack.EMPTY;
    }

    @Override
    public void syncUtilityGearData() {
        //If they are already synced, don't sync it.
        if (ItemStack.matches(utility_gear_tracked, utility_gear_copy)) return;
        //if (held_utility_gear.equals(utility_gear_copy, false)) return null;

        if (utility_gear_tracked.isEmpty()) return;

        //Now copy the held utility gear data over to the utility gear copy
        CompoundNBT cNBT = utility_gear_tracked.serializeNBT();
        utility_gear_copy = ItemStack.of(cNBT);
        LOGGER.info("I AM SYNCING THE UTILITY GEAR DATA RIGHT NOW");
    }

    @Override
    public ItemStack getCombatGear() {
        if (combat_gear_tracked.getItem() != combat_gear_copy.getItem()){
            combat_gear_tracked = combat_gear_copy.copy();
        }

        return combat_gear_tracked;
    }

    @Override
    public void upgradeCombatGear(ItemStack upgrade) {
        combat_gear_copy = upgrade.copy();

        LOGGER.info("I am setting the held combat gear");
        //Set the item stack as the held_combat_gear (this doesn't change the itemstack in the players inv)
        combat_gear_tracked = ItemStack.EMPTY;
    }

    @Override
    public void syncCombatGearData() {
        //If they are already synced, don't sync it.
        if (ItemStack.matches(combat_gear_tracked, combat_gear_copy)) return;
        //if (held_combat_gear.equals(combat_gear_copy, false)) return null;

        if (combat_gear_tracked.isEmpty()) return;

        //Now copy the held combat gear data over to the combat gear copy
        CompoundNBT cNBT = combat_gear_tracked.serializeNBT();
        combat_gear_copy = ItemStack.of(cNBT);
        LOGGER.info("I AM SYNCING THE COMBAT GEAR DATA RIGHT NOW");
    }

    @Override
    public CompoundNBT serializeNBT() {
        LOGGER.debug("I AM SAVING");
        CompoundNBT cNBT = new CompoundNBT();
        cNBT.put(UTILITY_GEAR_DATA, utility_gear_copy.serializeNBT());
        cNBT.put(COMBAT_GEAR_DATA, combat_gear_copy.serializeNBT());
        return cNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        utility_gear_copy = ItemStack.of(nbt.getCompound(UTILITY_GEAR_DATA));
        combat_gear_copy = ItemStack.of(nbt.getCompound(COMBAT_GEAR_DATA));
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
