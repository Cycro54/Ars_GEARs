package invoker54.arsgears.network.message;

import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncServerCombatGearMsg {
    
    public INBT nbtData;
    public int itemToUpdate;

    public SyncServerCombatGearMsg(INBT nbtData, int itemToUpdate){
        this.nbtData = nbtData;
        this.itemToUpdate = itemToUpdate;
    }
    
    public static void encode(SyncServerCombatGearMsg msg, PacketBuffer buffer){
        buffer.writeNbt((CompoundNBT) msg.nbtData);
        buffer.writeInt(msg.itemToUpdate);
    }
    
    public static SyncServerCombatGearMsg decode(PacketBuffer buffer){
        return new SyncServerCombatGearMsg(buffer.readNbt(), buffer.readInt());
    }

    public static void handle(SyncServerCombatGearMsg msg, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            PlayerEntity player = context.getSender();

            ItemStack gearStack = player.getMainHandItem();

            if (!(gearStack.getItem() instanceof CombatGearItem)) return;

            CombatGearCap cap = CombatGearCap.getCap(gearStack);

            //This writes the nbtData to the required item
            //Let's say the data is Sharpness enchant, this will write the Enchantment to the Sword CompountNBT in the gear capability
            CompoundNBT tag = cap.getTag(msg.itemToUpdate).merge((CompoundNBT) msg.nbtData);

            //If the player has the item equipped, make sure to sync the data
            if (cap.getSelectedItem() == msg.itemToUpdate){
                gearStack.getOrCreateTag().merge(cap.getTag(msg.itemToUpdate));
            }
        });
        context.setPacketHandled(true);
    }

}
