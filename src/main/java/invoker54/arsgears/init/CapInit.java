package invoker54.arsgears.init;

import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.capability.player.PlayerDataCap;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapInit {
    public static void registerCaps(){
        CapabilityManager.INSTANCE.register(GearCap.class, new GearCap.GearNBTStorage(), GearCap::new);
        CapabilityManager.INSTANCE.register(CombatGearCap.class, new CombatGearCap.CombatGearNBTStorage(), CombatGearCap::new);
        CapabilityManager.INSTANCE.register(PlayerDataCap.class, new PlayerDataCap.PlayerDataNBTStorage(),PlayerDataCap::new);
    }
}
