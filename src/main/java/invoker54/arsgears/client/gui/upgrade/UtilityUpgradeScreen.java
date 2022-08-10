package invoker54.arsgears.client.gui.upgrade;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.item.GearUpgrades;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static invoker54.arsgears.item.combatgear.CombatGearItem.*;
import static invoker54.arsgears.item.utilgear.UtilGearItem.*;

public class UtilityUpgradeScreen extends UpgradeScreen {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    protected void init() {
        super.init();
        paxelUpgrades();
        createEmptyCategory();
        fishingUpgrades();
        createEmptyCategory();
        hoeUpgrades();
    }

    @Override
    public void tick() {
        //If the player ends up dropping the gear at some point while this screen is on, close the screen
        if (ArsUtil.getHeldItem(ClientUtil.mC.player, UtilGearItem.class).isEmpty()) ClientUtil.mC.setScreen(null);
    }

    protected ResourceLocation getImage(String location){
        return new ResourceLocation(ArsGears.MOD_ID, "textures/gui/upgrade_screen/utility/" + location);
    }

    private void paxelUpgrades(){
        //region Enchants
        //Efficiency
        createEnchantUpgrade(paxelINT, Enchantments.BLOCK_EFFICIENCY, new int[]{1,2,3,5}, getImage("paxel_efficiency.png"));
        //Silk Touch
        createEnchantUpgrade(paxelINT, Enchantments.SILK_TOUCH, new int[]{0,0,1,0}, getImage("paxel_silk_touch.png"));
        //Fortune
        createEnchantUpgrade(paxelINT, Enchantments.BLOCK_FORTUNE, new int[]{0,1,2,3}, getImage("paxel_fortune.png"));
        //endregion

        //region Custom Upgrades
//        //Mining Power
//        createCustomUpgrade(paxelINT, GearUpgrades.paxelMiningPower, new int[]{1,2,3,4}, getImage("paxel_mining_power.png"));
        //Auto Place Inventory
        createCustomUpgrade(paxelINT, GearUpgrades.paxelAutoInv, new int[]{0,1,0,0}, getImage("paxel_auto_inventory.png"));
        //Radius
        createCustomUpgrade(paxelINT, GearUpgrades.paxelRadialMine, new int[]{0,0,1,0}, getImage("paxel_radial_mine.png"));
        //endregion
    }

    private void fishingUpgrades(){
        //region Enchants
        //Lure
        createEnchantUpgrade(fishingInt, Enchantments.FISHING_SPEED, new int[]{1,2,3,0}, getImage("fishing_lure.png"));
        //endregion

        //region Custom Upgrades
        //Bait Keep
        createCustomUpgrade(fishingInt, GearUpgrades.fishrodBaitKeep, new int[]{1,0,2,0}, getImage("fishing_bait_keep.png"));
        //XP Gain
        createCustomUpgrade(fishingInt, GearUpgrades.fishrodXPGain, new int[]{1,2,0,3}, getImage("fishing_xp_gain.png"));
        //endregion
    }

    private void hoeUpgrades(){
        //region Custom Upgrades
        //Harvest Radius
        createCustomUpgrade(hoeInt, GearUpgrades.hoeRadius, new int[]{0,1,0,2}, getImage("hoe_radius.png"));
        //Harvest Multiplier
        createCustomUpgrade(hoeInt, GearUpgrades.hoeDrops, new int[]{1,0,2,0}, getImage("hoe_harvest_multiply.png"));
        //endregion
    }
}
