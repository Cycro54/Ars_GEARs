package invoker54.arsgears.network.message;

import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ActivateGearMsg {

    //This is how the Network Handler will handle the message
    public static void handle(ActivateGearMsg msg, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if (context.getSender() == null) return;

            ItemStack item = ArsUtil.getHeldGearCap(context.getSender(), false, false);

            if(item != null) {
                PlayerEntity player = context.getSender();
                CombatGearCap cap = CombatGearCap.getCap(item);
                cap.setActivated(!cap.getActivated(), player);
            }
        });
        context.setPacketHandled(true);
    }
}
