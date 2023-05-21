package invoker54.arsgears.network.message;

import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.capability.player.PlayerDataCap;
import invoker54.arsgears.item.FakeSpellBook;
import net.minecraft.entity.player.ServerPlayerEntity;
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
            if (context.getSender() == null) return;
            ServerPlayerEntity player = context.getSender();
            ItemStack item = player.getMainHandItem();

            GearCap cap = GearCap.getCap(item);


            //If the player is sneaking, change between the fake spellbook and the Combat GEAR item
            if (player.isDiscrete()) {
                if (item.getItem() instanceof FakeSpellBook) {
                    cap = GearCap.getCap(PlayerDataCap.getCap(player).getCombatGear());

                    ((CombatGearCap)cap).changeToGEAR(player, item);
                }
                else if (cap instanceof CombatGearCap){
                    ((CombatGearCap)cap).changeToBook(player, item);
                }
                return;
            }

            if (cap instanceof CombatGearCap) {
                // LOGGER.debug("Is this activated? " + ((CombatGearCap) cap).getActivated());
                ((CombatGearCap) cap).setActivated(cap.getSelectedItem() == 2, context.getSender());
                // LOGGER.debug("Is this still activated? " + ((CombatGearCap) cap).getActivated());
            }

            cap.cycleItem(item, context.getSender());


        });
        context.setPacketHandled(true);
    }
}
