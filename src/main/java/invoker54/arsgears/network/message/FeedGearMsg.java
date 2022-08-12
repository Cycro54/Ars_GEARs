package invoker54.arsgears.network.message;

import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.client.gui.container.GearContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class FeedGearMsg {
    private int newDamageValue;
    private int newCount;

    public FeedGearMsg(int newDamageValue, int newCount){
        this.newDamageValue = newDamageValue;
        this.newCount = newCount;
    }

    public static void encode(FeedGearMsg msg, PacketBuffer buffer){
        buffer.writeInt(msg.newDamageValue);
        buffer.writeInt(msg.newCount);
    }

    public static FeedGearMsg decode(PacketBuffer buffer){
        return new FeedGearMsg(buffer.readInt(), buffer.readInt());
    }

    //This is how the Network Handler will handle the message
    public static void handle(FeedGearMsg msg, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            PlayerEntity player = context.getSender();

            //Now grab the gear
            ItemStack item = player.getMainHandItem();
            GearCap cap = GearCap.getCap(item);

            if (cap == null) return;

            //Mess with the container
            ((GearContainer)player.containerMenu).tempInv.getItem(0).setCount(msg.newCount);

            //Finally set the damage value
            item.setDamageValue(msg.newDamageValue);
        });
        context.setPacketHandled(true);
    }
}
