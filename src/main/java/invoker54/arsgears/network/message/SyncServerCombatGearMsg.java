package invoker54.arsgears.network.message;

import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.client.screen.GearContainer;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncServerCombatGearMsg {
    
    public INBT nbtData;
    
    public SyncServerCombatGearMsg(INBT nbtData){
        this.nbtData = nbtData;
    }
    
    public static void encode(SyncServerCombatGearMsg msg, PacketBuffer buffer){
        buffer.writeNbt((CompoundNBT) msg.nbtData);
    }
    
    public static SyncServerCombatGearMsg decode(PacketBuffer buffer){
        return new SyncServerCombatGearMsg(buffer.readNbt());
    }

    public static void handle(SyncServerCombatGearMsg msg, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            PlayerEntity player = context.getSender();

            ItemStack gearStack = ArsUtil.getHeldItem(player, CombatGearItem.class);
            gearStack.deserializeNBT((CompoundNBT) msg.nbtData);
        });
        context.setPacketHandled(true);
    }

}
