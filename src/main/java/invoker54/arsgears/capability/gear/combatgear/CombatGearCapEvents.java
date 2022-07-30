package invoker54.arsgears.capability.gear.combatgear;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.capability.gear.utilgear.UtilGearProvider;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID)
public class CombatGearCapEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void attachCombatGearCaps(AttachCapabilitiesEvent<ItemStack> event){
        if (event.getObject().getItem() instanceof CombatGearItem){
            //Add the combat gear cap
            //LOGGER.debug("am adding the combat cap to dis.");
            event.addCapability(CombatGearProvider.CAP_COMBAT_GEAR_LOC, new CombatGearProvider());
        }
    }
}
