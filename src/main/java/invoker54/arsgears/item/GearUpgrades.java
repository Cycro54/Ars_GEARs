package invoker54.arsgears.item;

import net.minecraft.entity.monster.VindicatorEntity;

public class GearUpgrades {
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
}
