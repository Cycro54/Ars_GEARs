package invoker54.arsgears.capability.utilgear;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID)
public class UtilGearCapEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void attachUtilGearCaps(AttachCapabilitiesEvent<ItemStack> event){
        if (event.getObject().getItem() instanceof UtilGearItem){
            //LOGGER.debug("I HAVE FOUND AN ITEM THAT MATCHES THE DESCRIPTION");
            event.addCapability(UtilGearProvider.CAP_UTIL_GEAR_LOC, new UtilGearProvider());
        }
    }
}
