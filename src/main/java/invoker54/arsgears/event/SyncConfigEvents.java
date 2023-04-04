package invoker54.arsgears.event;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.config.ArsGearsConfig;
import invoker54.arsgears.network.NetworkHandler;
import invoker54.arsgears.network.message.SyncConfigMsg;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

import static invoker54.arsgears.config.ArsGearsConfig.*;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID)
public class SyncConfigEvents {
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
        //Make sure you have all the right values
        bakeCommonConfig();

        //Now send the player all of the config values
        NetworkHandler.sendToPlayer(event.getPlayer(), new SyncConfigMsg(
                useCombatItems,
                useUtilityItems,
                useSpellbook,
                disableSpellBookCooldown,
                disableGearCooldown,
                coolDownMultiplier,
                coolDownValueChange,
                upgradeValue
        ));
    }

    @SubscribeEvent
    public static void onUpdateConfig(TickEvent.ServerTickEvent event){
        if (event.type == TickEvent.Type.CLIENT) return;
        if (event.phase == TickEvent.Phase.START) return;
        if (ArsGearsConfig.isDirty()){
            //Then finally send the config data to all players
            NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(),
                    new SyncConfigMsg(
                            useCombatItems,
                            useUtilityItems,
                            useSpellbook,
                            disableSpellBookCooldown,
                            disableGearCooldown,
                            coolDownMultiplier,
                            coolDownValueChange,
                            upgradeValue
                    ));

            ArsGearsConfig.markDirty(false);
        }
    }
}