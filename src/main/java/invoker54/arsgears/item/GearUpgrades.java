package invoker54.arsgears.item;

import invoker54.arsgears.capability.gear.GearCap;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;

public class GearUpgrades {
    public static final String gearUpgradeNBT = "ARS_GEAR_UPGRADE_NBT";

    //Combat gear upgrades
    //region Sword
    /** Steals mana from entity hit */
    public static final String swordManaSteal = "SWORD_MANA_STEAL";
    /** Applies spell on sweeping attack */
    public static final String swordSpellSweep = "SWORD_SPELL_SWEEP";
    //endregion

    //region Bow
    /** How fast you draw your bow */
    public static final String bowSpeed = "BOW_DRAW_SPEED";
    /** Makes spell arrows travel instantaneously */
    public static final String bowSpellArrow = "BOW_SPELL_ARROW";
    /** Makes the cooldown for all of your spells go down */
    public static final String bowCooldown = "BOW_COOLDOWN";
    /** Chance of keeping arrows */
    public static final String bowArrowKeep = "BOW_ARROW_KEEP";
    /** Spell splitting */
    public static final String bowSpellSplit = "BOW_SPELL_SPLIT";
    //endregion

    //region Mirror
    /** Make spells cost less on the mirror */
    public static final String mirrorManaDiscount = "MIRROR_MANA_DISCOUNT";
    /** Adds free glyphs onto the end of the mirror spell */
    public static final String mirrorFreeGlyph = "MIRROR_FREE_GLYPH";
    /** Ability to quickly cast a self spell on yourself WITHOUT switching to it */
    public static final String mirrorQuickCast = "MIRROR_QUICK_CAST";
    //endregion

    //Utility gear upgrades
    //region paxel
//    /** Increases the Paxels mining level */
//    public static final String paxelMiningPower = "PAXEL_MINING_POWER";
    /** Will automatically place mined blocks into a selected inventory */
    public static final String paxelAutoInv = "PAXEL_AUTO_INVENTORY";
    /** This will increase the mining radius of the paxel */
    public static final String paxelRadialMine = "PAXEL_RADIAL_MINE";
    //endregion

    //region hoe
    /** Increases the harvest radius */
    public static final String hoeRadius = "HOE_RADIUS";
    /** Increase the amount of drops per harvest */
    public static final String hoeDrops = "HOE_DROPS";
    //endregion

    //region fishing rod
    /** Decrease chance to lose bait */
    public static final String fishrodBaitKeep = "FISHING_ROD_BAIT_KEEP";
    /** Gain more XP per catch */
    public static final String fishrodXPGain = "FISHING_ROD_XP_GAIN";
    //endregion

    public static TranslationTextComponent getName(String upgrade){
        return new TranslationTextComponent("ars_gears.upgrades." + upgrade);
    }

    public static TranslationTextComponent getFullName(String upgrade, CompoundNBT nbt){
        return (TranslationTextComponent) getName(upgrade).append(" " + nbt.getInt(upgrade));
    }

    public static CompoundNBT getUpgrades(int gearCycle, GearCap cap) {
        //First grab the main compoundNBT Tag
        CompoundNBT cNBT = cap.getTag(gearCycle);
        //Now inside of it should be an upgrade compound, if there isn't create one.
        if (!cNBT.contains(GearUpgrades.gearUpgradeNBT)) {
            cNBT.put(GearUpgrades.gearUpgradeNBT, new CompoundNBT());
        }
        return cNBT.getCompound(GearUpgrades.gearUpgradeNBT);
    }

    public static int getUpgrade(int gearCycle, GearCap cap, String upgradeName){
        CompoundNBT upgrades = getUpgrades(gearCycle, cap);
        return upgrades.getInt(upgradeName);
    }
}
