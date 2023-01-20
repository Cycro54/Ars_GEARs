package invoker54.arsgears.network.message;

import invoker54.arsgears.capability.gear.GearCap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class buyUpgradeMsg {
    public INBT nbtData;
    public int gearCycle;
    public int cost;

    public buyUpgradeMsg(INBT nbtData, int itemToUpdate, int cost){
        this.nbtData = nbtData;
        this.gearCycle = itemToUpdate;
        this.cost = cost;
    }
    
    public static void encode(buyUpgradeMsg msg, PacketBuffer buffer){
        buffer.writeNbt((CompoundNBT) msg.nbtData);
        buffer.writeInt(msg.gearCycle);
        buffer.writeInt(msg.cost);
    }
    
    public static buyUpgradeMsg decode(PacketBuffer buffer){
        return new buyUpgradeMsg(buffer.readNbt(), buffer.readInt(), buffer.readInt());
    }

    public static void handle(buyUpgradeMsg msg, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            PlayerEntity player = context.getSender();

            ItemStack gearStack = player.getOffhandItem();

            GearCap cap = GearCap.getCap(gearStack);

            if (cap == null) return;

            //This is for general compound saving (this is for GEAR upgrades)
            if (msg.gearCycle == -1) {
                gearStack.getOrCreateTag().merge((CompoundNBT) msg.nbtData);
            }
            //This is for specific compound saving (this is for enchants)
            else {
                cap.getTag(msg.gearCycle).merge((CompoundNBT) msg.nbtData);
            }

            //If the player is in creative, don't touch their xp
            if (player.isCreative()) return;

            //Now finally take their precious experience.
            player.giveExperiencePoints(-msg.cost);
        });
        context.setPacketHandled(true);
    }

}
