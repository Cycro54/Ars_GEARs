package invoker54.arsgears.network.message;

import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class CycleGearMsg {
    //This is how the Network Handler will handle the message
    private static final Logger LOGGER = LogManager.getLogger();

    public static void handle(CycleGearMsg msg, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            ItemStack item = context.getSender().getMainHandItem();

            GearCap cap = GearCap.getCap(item);

            if (cap instanceof CombatGearCap) {
                LOGGER.debug("Is this activated? " + ((CombatGearCap) cap).getActivated());
                ((CombatGearCap) cap).setActivated(cap.getSelectedItem() == 2, context.getSender());
                LOGGER.debug("Is this still activated? " + ((CombatGearCap) cap).getActivated());
            }

            cap.cycleItem(item, context.getSender());


//            if (item.getItem() instanceof UtilGearItem) GearCap.getCap(item).cycleItem(item);
//
//            else if(item.getItem() instanceof CombatGearItem) {
//                CombatGearCap cap = CombatGearCap.getCap(item);
//
//                cap.cycleItem(item);
//
//                //This is for if the item ends up being the mirror
//            }
        });
        context.setPacketHandled(true);
    }
}
