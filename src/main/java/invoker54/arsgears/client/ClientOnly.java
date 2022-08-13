package invoker54.arsgears.client;

import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.client.gui.upgrade.CombatUpgradeScreen;
import invoker54.arsgears.client.gui.upgrade.UtilityUpgradeScreen;

public class ClientOnly {

    public static void openUpgradeScreen(GearCap cap){
        if(cap instanceof CombatGearCap) {
            ClientUtil.mC.setScreen(new CombatUpgradeScreen());
        }
        else{
            ClientUtil.mC.setScreen(new UtilityUpgradeScreen());
        }
    }
}
