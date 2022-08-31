package invoker54.arsgears.network;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.network.message.*;
import invoker54.arsgears.network.message.edited.PacketSetBookMode;
import invoker54.arsgears.network.message.edited.PacketUpdateBookGUI;
import invoker54.arsgears.network.message.edited.PacketUpdateSpellColors;
import invoker54.arsgears.network.message.edited.PacketUpdateSpellbook;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class NetworkHandler {
    //Increment the first number if you add new stuff to NetworkHandler class
    //Increment the middle number each time you make a new Message
    //Increment the last number each time you fix a bug
    private static final String PROTOCOL_VERSION = "1.8.0";

    private static int ID = 0;
    public static int nextID(){return ID++;}

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(

            //Name of the channel
            new ResourceLocation(ArsGears.MOD_ID, "network"),
            //Supplier<String> that returns protocol version
            () -> PROTOCOL_VERSION,
            //Checks incoming network protocol version for client (so it's pretty much PROTOCOL_VERSION == INCOMING_PROTOCOL_VERSION)
            PROTOCOL_VERSION::equals,
            //Checks incoming network protocol version for server (If they don't equal, it won't work.)
            PROTOCOL_VERSION::equals
    );

    public static void init(){
        // This is how you avoid sending anything to the server when you don't need to.
        // (change encode with an empty lambda, and just make decode create a new instance of the target message class)
        // INSTANCE.registerMessage(0, SpawnDiamondMsg.class, (message, buf) -> {}, it -> new SpawnDiamondMsg(), SpawnDiamondMsg::handle);
        // INSTANCE.registerMessage(0, SyncClientCapMsg.class, SyncClientCapMsg::Encode, SyncClientCapMsg::Decode, SyncClientCapMsg::handle);
        INSTANCE.registerMessage(nextID(), CycleGearMsg.class, (message, buf) -> {}, it -> new CycleGearMsg(), CycleGearMsg::handle);
        INSTANCE.registerMessage(nextID(), SyncServerPlayerCapMsg.class, SyncServerPlayerCapMsg::encode, SyncServerPlayerCapMsg::decode, SyncServerPlayerCapMsg::handle);
        INSTANCE.registerMessage(nextID(), OpenGearContainerMsg.class, (message, buf) -> {}, it -> new OpenGearContainerMsg(), OpenGearContainerMsg::handle);
        INSTANCE.registerMessage(nextID(), FeedGearMsg.class, FeedGearMsg::encode, FeedGearMsg::decode, FeedGearMsg::handle);
        INSTANCE.registerMessage(nextID(), ActivateGearMsg.class, (message, buf) -> {}, it -> new ActivateGearMsg(), ActivateGearMsg::handle);
        INSTANCE.registerMessage(nextID(), buyUpgradeMsg.class, buyUpgradeMsg::encode, buyUpgradeMsg::decode, buyUpgradeMsg::handle);
        INSTANCE.registerMessage(nextID(), QuickCastMsg.class, (message, buf) -> {}, it -> new QuickCastMsg(), QuickCastMsg::handle);

        //These are messages from Ars nouveau edited
        INSTANCE.registerMessage(nextID(), PacketUpdateSpellbook.class, PacketUpdateSpellbook::toBytes, PacketUpdateSpellbook::new, PacketUpdateSpellbook::handle);
        INSTANCE.registerMessage(nextID(), PacketSetBookMode.class, PacketSetBookMode::toBytes, PacketSetBookMode::new, PacketSetBookMode::handle);
        INSTANCE.registerMessage(nextID(), PacketUpdateSpellColors.class, PacketUpdateSpellColors::toBytes, PacketUpdateSpellColors::new, PacketUpdateSpellColors::handle);
        INSTANCE.registerMessage(nextID(), PacketUpdateBookGUI.class, PacketUpdateBookGUI::toBytes, PacketUpdateBookGUI::new, PacketUpdateBookGUI::handle);
    }


    //Custom method used to send data to players
    public static void sendToPlayer(PlayerEntity player, Object message) {
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), message);
    }
}
