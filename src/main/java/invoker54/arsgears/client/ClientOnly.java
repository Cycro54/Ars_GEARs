package invoker54.arsgears.client;

import invoker54.arsgears.client.gui.upgrade.CombatUpgradeScreen;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import net.minecraft.item.ItemStack;

public class ClientOnly {

    public static void openUpgradeScreen(ItemStack gearStack){
        if(gearStack.getItem() instanceof CombatGearItem) {
            ClientUtil.mC.setScreen(new CombatUpgradeScreen());
        }
        else if (gearStack.getItem() instanceof UtilGearItem){
            //ClientUtil.mC.setScreen();
        }
    }
}
