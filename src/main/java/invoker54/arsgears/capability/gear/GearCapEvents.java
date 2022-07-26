package invoker54.arsgears.capability.gear;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.item.combatgear.ModSpellMirror;
import invoker54.arsgears.item.combatgear.ModSpellBow;
import invoker54.arsgears.item.combatgear.ModSpellSword;
import invoker54.arsgears.item.utilgear.ModFishingRodItem;
import invoker54.arsgears.item.utilgear.ModHoeItem;
import invoker54.arsgears.item.utilgear.PaxelItem;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID)
public class GearCapEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void attachGearCaps(AttachCapabilitiesEvent<ItemStack> event){
        Item item = event.getObject().getItem();

        //For Combat gear stuff
        if (item instanceof CombatGearItem){
            event.addCapability(GearProvider.CAP_GEAR_LOC, new GearProvider(false, event.getObject()));
        }
        else if (item instanceof ModSpellSword){
            event.addCapability(GearProvider.CAP_GEAR_LOC, new GearProvider(false, event.getObject()));
        }
        else if (item instanceof ModSpellBow){
            event.addCapability(GearProvider.CAP_GEAR_LOC, new GearProvider(false, event.getObject()));
        }
        else if (item instanceof ModSpellMirror){
            event.addCapability(GearProvider.CAP_GEAR_LOC, new GearProvider(false, event.getObject()));
        }

        //For Util gear stuff
        else if (item instanceof UtilGearItem){
            event.addCapability(GearProvider.CAP_GEAR_LOC, new GearProvider(true, event.getObject()));
        }
        else if (item instanceof PaxelItem){
            event.addCapability(GearProvider.CAP_GEAR_LOC, new GearProvider(true, event.getObject()));
        }
        else if (item instanceof ModFishingRodItem){
            event.addCapability(GearProvider.CAP_GEAR_LOC, new GearProvider(true, event.getObject()));
        }
        else if (item instanceof ModHoeItem){
            event.addCapability(GearProvider.CAP_GEAR_LOC, new GearProvider(true, event.getObject()));
        }
    }
}
