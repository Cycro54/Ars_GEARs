package invoker54.arsgears.network.message;

import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.item.utilgear.UtilGearItem;
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

            ItemStack gearStack = player.getMainHandItem();

            GearCap cap = GearCap.getCap(gearStack);

            if (cap == null) return;

            //This writes the nbtData to the required item
            //Let's say the data is Sharpness enchant, this will write the Enchantment to the Sword CompoundNBT in the gear capability
            cap.getTag(msg.gearCycle).merge((CompoundNBT) msg.nbtData);

            //If the player has the item equipped, make sure to sync the data
            if (cap.getSelectedItem() == msg.gearCycle){
                gearStack.getOrCreateTag().merge(cap.getTag(msg.gearCycle));
            }

            //Now finally take their precious experience.
            player.giveExperiencePoints(-msg.cost);
        });
        context.setPacketHandled(true);
    }

}
