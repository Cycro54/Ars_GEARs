package invoker54.arsgears.client.gui.upgrade;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.client.gui.button.UpgradeButton;
import invoker54.arsgears.item.GearUpgrades;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.apache.http.conn.BasicEofSensorWatcher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static invoker54.arsgears.item.combatgear.CombatGearItem.*;

public class CombatUpgradeScreen extends UpgradeScreen {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    protected void init() {
        super.init();
        swordUpgrades();
        createEmptyCategory();
        bowUpgrades();
        createEmptyCategory();
        mirrorUpgrades();
    }

    @Override
    public void tick() {
        //If the player ends up dropping the gear at some point while this screen is on, close the screen
        if (ArsUtil.getHeldItem(ClientUtil.mC.player, CombatGearItem.class).isEmpty()) ClientUtil.mC.setScreen(null);
    }

    protected ResourceLocation getImage(String location){
        return new ResourceLocation(ArsGears.MOD_ID, "textures/gui/upgrade_screen/combat/" + location);
    }

    private void swordUpgrades(){
        //region Enchants
        //Sharpness
        createEnchantUpgrade(swordINT, "Sharpness", Enchantments.SHARPNESS, new int[]{1,2,0,4}, getImage("sharpness.png"));
        //Sweeping Edge
        createEnchantUpgrade(swordINT, "Sweeping Edge", Enchantments.SWEEPING_EDGE, new int[]{1,2,3,0}, getImage("sweep_edge.png"));
        //Looting
        createEnchantUpgrade(swordINT, "Looting", Enchantments.MOB_LOOTING, new int[]{0,1,3,0}, getImage("looting.png"));
        //endregion

        //region Custom Upgrades
        //Mana steal
        createCustomUpgrade(swordINT, "Mana Steal", GearUpgrades.swordManaSteal, new int[]{1,0,2,0}, getImage("mana_steal.png"));
        //Spell Sweep
        createCustomUpgrade(swordINT, "Spell Sweep", GearUpgrades.swordSpellSweep, new int[]{0,0,1,0}, getImage("spell_sweep.png"));
        //endregion
    }

    private void bowUpgrades(){
        //region Enchants
        //Power
        createEnchantUpgrade(bowInt, "Power", Enchantments.POWER_ARROWS, new int[]{1,2,3,0}, getImage("power.png"));
        //endregion

        //region Custom Upgrades
        //Bow Speed
        createCustomUpgrade(bowInt, "Bow Speed", GearUpgrades.bowSpeed, new int[]{1,2,3,0}, getImage("bow_speed.png"));
        //Spell Arrow
        createCustomUpgrade(bowInt, "Spell Arrow", GearUpgrades.bowSpellArrow, new int[]{0,1,0,0}, getImage("spell_arrow.png"));
        //Spell Cooldown
        createCustomUpgrade(bowInt, "Spell Cooldown", GearUpgrades.bowCooldown, new int[]{0,1,0,0}, getImage("spell_cooldown.png"));
        //Arrow Recycle
        createCustomUpgrade(bowInt, "Arrow Keep", GearUpgrades.bowArrowKeep, new int[]{1,0,0,0}, getImage("arrow_keep.png"));
        //Spell Split
        createCustomUpgrade(bowInt, "Spell Split", GearUpgrades.bowSpellSplit, new int[]{0,0,1,2}, getImage("spell_split.png"));
        //endregion
    }
    private void mirrorUpgrades(){
        //region Custom Upgrades
        //Mana Discount
        createCustomUpgrade(mirrorInt, "Mana Discount", GearUpgrades.mirrorManaDiscount, new int[]{0,1,2,0}, getImage("mana_discount.png"));
        //Extra Glyph
        createCustomUpgrade(mirrorInt, "Free Glyph", GearUpgrades.mirrorFreeGlyph, new int[]{0,1,2,3}, getImage("free_glyph.png"));
        //endregion
    }
}
