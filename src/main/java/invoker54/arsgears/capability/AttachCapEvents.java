package invoker54.arsgears.capability;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID)
public class AttachCapEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void attachUtilGearCaps(AttachCapabilitiesEvent<ItemStack> event){

        LOGGER.debug("Looking at item of class: " + event.getObject().getItem().getClass());
        LOGGER.debug("Looking at item of uuid: " + event.getObject().hashCode());
        if (event.getObject().getItem() instanceof UtilGearItem){
            LOGGER.debug("I HAVE FOUND AN ITEM THAT MATCHES THE DESCRIPTION");
            event.addCapability(UtilGearProvider.CAP_UTIL_GEAR_LOC, new UtilGearProvider());
        }
    }

    public static void registerCaps(){
        CapabilityManager.INSTANCE.register(UtilGearCap.class, new UtilGearCap.UtilGearNBTStorage(),UtilGearCap::new);
    }
}
