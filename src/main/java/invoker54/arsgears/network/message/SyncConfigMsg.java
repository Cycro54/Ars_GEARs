package invoker54.arsgears.network.message;

import invoker54.arsgears.config.ArsGearsConfig;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncConfigMsg {

    public Boolean useCombatItems;
    public Boolean useUtilityItems;
    public Boolean useSpellbook;
    public Boolean disableCooldown;
    public Double coolDownMultiplier;
    public Double coolDownValueChange;
    public Integer upgradeValue;

    public SyncConfigMsg(boolean useCombatItems, boolean useUtilityItems, boolean useSpellbook, boolean disableCooldown,
                         double coolDownMultiplier, double coolDownValueChange, int upgradeValue){
        this.useCombatItems = useCombatItems;
        this.useUtilityItems = useUtilityItems;
        this.useSpellbook = useSpellbook;
        this.disableCooldown = disableCooldown;
        this.coolDownMultiplier = coolDownMultiplier;
        this.coolDownValueChange = coolDownValueChange;
        this.upgradeValue = upgradeValue;
    }

    public static void encode(SyncConfigMsg msg, PacketBuffer buffer){
        buffer.writeBoolean(msg.useCombatItems);
        buffer.writeBoolean(msg.useUtilityItems);
        buffer.writeBoolean(msg.useSpellbook);
        buffer.writeBoolean(msg.disableCooldown);
        buffer.writeDouble(msg.coolDownMultiplier);
        buffer.writeDouble(msg.coolDownValueChange);
        buffer.writeInt(msg.upgradeValue);
    }

    public static SyncConfigMsg decode(PacketBuffer buffer) {
        return new SyncConfigMsg(
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readDouble(),
                buffer.readDouble(),
                buffer.readInt()
        );
    }

    //This is how the Network Handler will handle the message
    public static void handle(SyncConfigMsg msg, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            //Make sure only the server sends this
            if (context.getSender() != null) return;

            //Now start to sync the config data
            ArsGearsConfig.useCombatItems = msg.useCombatItems;
            ArsGearsConfig.useUtilityItems = msg.useUtilityItems;
            ArsGearsConfig.useSpellbook = msg.useSpellbook;
            ArsGearsConfig.disableCooldown = msg.disableCooldown;
            ArsGearsConfig.coolDownMultiplier = msg.coolDownMultiplier;
            ArsGearsConfig.coolDownValueChange = msg.coolDownValueChange;
            ArsGearsConfig.upgradeValue = msg.upgradeValue;
        });
        context.setPacketHandled(true);
    }
}
