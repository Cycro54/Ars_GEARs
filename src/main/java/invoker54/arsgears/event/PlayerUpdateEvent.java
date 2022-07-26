package invoker54.arsgears.event;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.capability.player.PlayerDataCap;
import invoker54.arsgears.init.ItemInit;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.CallbackI;
import sun.awt.geom.AreaOp;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID)
public class PlayerUpdateEvent {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void checkUtilityGear(TickEvent.PlayerTickEvent event){
        //make sure it's server side
        if(event.side == LogicalSide.CLIENT) return;
        //Make sure it's the last phase (only needs to run once)
        //if(event.phase == TickEvent.Phase.END) return;

        //I need the player
        PlayerEntity player = event.player;
        //The player capability
        PlayerDataCap cap = PlayerDataCap.getCap(player);
        //the tracked item in the capability
        ItemStack trackedGear = cap.getUtilityGear();
        //The item to be inspected
        ItemStack focusedGear = ItemStack.EMPTY;

        //Check main hand for Utility Gear
        if (player.getMainHandItem().getItem() instanceof UtilGearItem){
            focusedGear = player.getMainHandItem();
        }
        //Check off hand for Utility Gear
        else if (player.getOffhandItem().getItem() instanceof UtilGearItem) {
            focusedGear = player.getOffhandItem();
        }

        //make sure we have a focused gear
        if (focusedGear.isEmpty()) return;

        //If the trackedGear and focusedGear don't match, set focusedGear to be the new trackedGear
        if (!ItemStack.matches(trackedGear, focusedGear)) {
            LOGGER.info("THEY WERENT THE SAME");
            cap.setUtilityGear(focusedGear);
        }
        //Now destroy all of the other gears in the inventory
        for (ItemStack itemStack : player.inventory.items){
            if (itemStack.getItem() instanceof UtilGearItem){
                if (itemStack != focusedGear) itemStack.shrink(1);
            }
        }
        //Don't forget about offhand
        if (player.getOffhandItem() != focusedGear) player.getOffhandItem().shrink(1);

        //Finally, sync the data between the copy and the trackedGear
        cap.syncUtilityGearData();
    }

//    private static CompoundNBT syncUtilityGear(ItemStack heldItem, CompoundNBT itemData, PlayerDataCap cap){
//        if (!(heldItem.getItem() instanceof UtilGearItem)) return null;
//
//        //If itemdata isn't null, then that means held utility gear in player data cap is set already
//        if (itemData != null){
//            heldItem.deserializeNBT(itemData);
//            return itemData;
//        }
//
//        //Make sure we didn't already set this itemstack as the held utility gear
//        if (!cap.checkUtilityGear(heldItem)) cap.setUtilityGear(heldItem);
//
//
//        //Sync the data between the utility gear copy and the held utility gear
//        itemData = cap.syncUtilityGearData();
//
//        //Make sure to return the data so the next utility gear can use it
//        return itemData;
//    }
}
