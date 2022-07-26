package invoker54.arsgears.init;

import invoker54.arsgears.capability.player.PlayerDataCap;
import invoker54.arsgears.capability.utilgear.UtilGearCap;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapInit {
    public static void registerCaps(){
        CapabilityManager.INSTANCE.register(UtilGearCap.class, new UtilGearCap.UtilGearNBTStorage(),UtilGearCap::new);
        CapabilityManager.INSTANCE.register(PlayerDataCap.class, new PlayerDataCap.PlayerDataNBTStorage(),PlayerDataCap::new);
    }
}
