package invoker54.arsgears.network.message;

import invoker54.arsgears.capability.player.PlayerDataCap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncServerPlayerCapMsg {
    private INBT nbtData;

    public SyncServerPlayerCapMsg(INBT nbtData){
        this.nbtData = nbtData;
    }

    public static void encode(SyncServerPlayerCapMsg msg, PacketBuffer buffer){
        buffer.writeNbt((CompoundNBT) msg.nbtData);
    }

    public static SyncServerPlayerCapMsg decode(PacketBuffer buffer){
        return new SyncServerPlayerCapMsg(buffer.readNbt());
    }

    //This is how the Network Handler will handle the message
    public static void handle(SyncServerPlayerCapMsg msg, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            if (context.getSender() == null) return;

            PlayerDataCap cap = PlayerDataCap.getCap(context.getSender());
            if (cap == null) return;

            //Give player data cap to player
            cap.deserializeNBT((CompoundNBT) msg.nbtData);
        });
        context.setPacketHandled(true);
    }
}
