package invoker54.arsgears.capability.player;

import invoker54.arsgears.ArsGears;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID)
public class PlayerDataCapEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void attachCap(AttachCapabilitiesEvent<Entity> event){
        if (event.getObject() instanceof PlayerEntity){
            //LOGGER.debug("I HAVE FOUND A player!");
            event.addCapability(PlayerDataProvider.CAP_PLAYER_DATA_LOC, new PlayerDataProvider());
        }
    }

    @SubscribeEvent
    public static void onPlayerCopy(PlayerEvent.Clone event){
        if (!event.isWasDeath()) return;

        //Grab ye old cap
        PlayerDataCap origCap = PlayerDataCap.getCap(event.getOriginal());
        //Grab the new cap
        PlayerDataCap newCap = PlayerDataCap.getCap(event.getPlayer());

        if (origCap == null || newCap == null) throw new NullPointerException();

        //Now place the old data on the new cap
        newCap.deserializeNBT(origCap.serializeNBT());
    }
}
