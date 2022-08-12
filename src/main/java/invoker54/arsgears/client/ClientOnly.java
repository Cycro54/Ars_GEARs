package invoker54.arsgears.client;

import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.client.gui.upgrade.CombatUpgradeScreen;
import invoker54.arsgears.client.gui.upgrade.UtilityUpgradeScreen;
import invoker54.arsgears.init.CapInit;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import net.minecraft.item.ItemStack;

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
