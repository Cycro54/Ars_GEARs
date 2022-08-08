package invoker54.arsgears.event;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.capability.player.PlayerDataCap;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.ItemEvent;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID)
public class UtilityGearEvents {
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
        //Check hands for utilty gear
        ItemStack focusedGear = ArsUtil.getHeldItem(player, UtilGearItem.class);

        //make sure we have a focused gear
        if (focusedGear.isEmpty()) return;

        //If the trackedGear and focusedGear don't match, set focusedGear to be the new trackedGear
        if (trackedGear == focusedGear) {
            //LOGGER.info("THEY WERENT THE SAME");
            ArsUtil.replaceItemStack(player, focusedGear, cap.getUtilityGear());
        }

        //Finally, sync the data between the copy and the trackedGear
        cap.syncUtilityGearData();
    }
}
