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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static invoker54.arsgears.item.combatgear.CombatGearItem.swordINT;

public class CombatUpgradeScreen extends UpgradeScreen {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    protected void init() {
        LOGGER.debug("I AM OPENING THE COMBAT UPGRADE SCREEN");
        super.init();

        createSharpness();
        createManaSteal();
    }

    @Override
    public void tick() {
        //If the player ends up dropping the gear at some point while this screen is on, close the screen
        if (ArsUtil.getHeldItem(ClientUtil.mC.player, CombatGearItem.class).isEmpty()) ClientUtil.mC.setScreen(null);
    }

    //Sword
    public void createSharpness() {
        ResourceLocation image = new ResourceLocation(ArsGears.MOD_ID, "textures/gui/upgrade_screen/combat/sharpness.png");
        int totalLvls = 3;
        UpgradeButton prevButton;
        String catName = "Sharpness";

        //Sharpness 1
        prevButton = createEnchantUpgrade(CombatGearItem.class, swordINT, catName, Enchantments.SHARPNESS, totalLvls, 1, image, null);

        //Sharpness 2
        prevButton = createEnchantUpgrade(CombatGearItem.class, swordINT, catName, Enchantments.SHARPNESS, totalLvls, 2, image, prevButton);

        //Empty
        createEmptyUpgrade(catName);

        //Sharpness 4
        createEnchantUpgrade(CombatGearItem.class, swordINT, catName, Enchantments.SHARPNESS, totalLvls, 4, image, prevButton);
    }

    //Sword
    public void createManaSteal(){
        ResourceLocation image = new ResourceLocation(ArsGears.MOD_ID, "textures/gui/upgrade_screen/combat/looting.png");
        int totalLvls = 2;
        UpgradeButton prevButton;
        String catName = "Mana Steal";

        //Mana Steal 1
        prevButton = createCustomUpgrade(CombatGearItem.class, swordINT, catName, GearUpgrades.swordManaSteal, totalLvls, 1, image, null);

        //Empty
        createEmptyUpgrade(catName);

        //Mana Steal 2
        createCustomUpgrade(CombatGearItem.class, swordINT, catName, GearUpgrades.swordManaSteal, totalLvls, 2, image, prevButton);
    }

    //This was taken from the EnchantmentHelper class
    public static void setEnchantments(Map<Enchantment, Integer> enchants, CompoundNBT tag) {
        ListNBT listnbt = new ListNBT();

        for(Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (enchantment != null) {
                int i = entry.getValue();
                CompoundNBT compoundnbt = new CompoundNBT();
                compoundnbt.putString("id", String.valueOf((Object) Registry.ENCHANTMENT.getKey(enchantment)));
                compoundnbt.putShort("lvl", (short)i);
                listnbt.add(compoundnbt);
            }
        }

        tag.put("Enchantments", listnbt);
    }
}
