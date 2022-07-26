package invoker54.arsgears.network.message;

import invoker54.arsgears.capability.player.PlayerDataCap;
import invoker54.arsgears.capability.utilgear.UtilGearCap;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CycleUtilityGearMsg {
    //This is how the Network Handler will handle the message
    public static void handle(CycleUtilityGearMsg msg, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            System.out.println("Syncing cap data for a client...");
            ItemStack item = context.getSender().getMainHandItem();
            if(item.getItem() instanceof UtilGearItem){
                UtilGearCap cap = UtilGearCap.getCap(item);
                cap.cycleItem();
            }

        });
        context.setPacketHandled(true);
    }
}
