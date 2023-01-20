package invoker54.arsgears.event;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.capability.player.PlayerDataCap;
import invoker54.arsgears.init.ItemInit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID)
public class UtilityGearEvents {
    private static final Logger LOGGER = LogManager.getLogger();
    @SubscribeEvent
    public static void checkUtilityGear(TickEvent.PlayerTickEvent event){
        //make sure it's server side
        if(event.side == LogicalSide.CLIENT) return;
        //Make sure it's the last phase (only needs to run once)
        if(event.phase == TickEvent.Phase.END) return;
        //I need the player
        PlayerEntity player = event.player;
        //The player capability
        PlayerDataCap cap = PlayerDataCap.getCap(player);
        //the tracked item in the capability
        ItemStack trackedGear = cap.getUtilityGear();
        //Check hands for utility gear, spell sword, bow, or mirror
        ItemStack focusedGear = player.getMainHandItem();
        GearCap itemCap = GearCap.getCap(focusedGear);
        if (itemCap == null || itemCap instanceof CombatGearCap) {
            focusedGear = player.getOffhandItem();
            itemCap = GearCap.getCap(focusedGear);
        }

        //If it STILL equals null, that means there is not a utility Gear equipped
        if (itemCap == null || itemCap instanceof CombatGearCap) return;

        if (focusedGear.isEmpty()) return;

        //If the trackedGear and focusedGear don't match, set focusedGear to be the new trackedGear
        if (trackedGear != focusedGear) {
            ArsUtil.replaceItemStack(player, focusedGear, cap.getUtilityGear());
        }
        //Finally, sync the data between the copy and the trackedGear
        cap.syncUtilityGearData();
    }

    @SubscribeEvent
    public static void onDrop(ItemTossEvent event){
        ItemStack oldStack = event.getEntityItem().getItem();
        GearCap cap = GearCap.getCap(oldStack);

        if (cap == null || cap instanceof CombatGearCap) return;

        ItemStack newStack = new ItemStack(ItemInit.WOOD_UTILITY_GEAR);

        newStack.deserializeNBT(oldStack.serializeNBT());

        event.getEntityItem().setItem(newStack);
    }
}
