package invoker54.arsgears.network.message;

import invoker54.arsgears.client.gui.container.GearContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenGearContainerMsg {
    //This is how the Network Handler will handle the message
    public static void handle(OpenGearContainerMsg msg, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            //Open the Sell Container
            context.getSender().openMenu(new SimpleNamedContainerProvider((id, playerInv, player) -> {
                return new GearContainer(id, playerInv);
            }, new StringTextComponent("Gear Inventory")));
        });
        context.setPacketHandled(true);
    }
}
