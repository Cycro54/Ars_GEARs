package invoker54.arsgears.network.message;

import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class CycleGearMsg {
    //This is how the Network Handler will handle the message
    public static void handle(CycleGearMsg msg, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            ItemStack item = context.getSender().getMainHandItem();

            if (item.getItem() instanceof UtilGearItem) GearCap.getCap(item).cycleItem();

            else if(item.getItem() instanceof CombatGearItem) {
                CombatGearCap cap = CombatGearCap.getCap(item);
                cap.cycleItem();
                if (cap.getSelectedItem() == 2) cap.setActivated(true);
                else cap.setActivated(false);
            }
        });
        context.setPacketHandled(true);
    }
}
