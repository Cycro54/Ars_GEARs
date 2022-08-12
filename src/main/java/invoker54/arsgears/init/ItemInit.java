package invoker54.arsgears.init;

import com.hollingsworth.arsnouveau.ArsNouveau;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.item.UpgradeRune;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.item.combatgear.ModSpellMirror;
import invoker54.arsgears.item.combatgear.ModSpellBow;
import invoker54.arsgears.item.combatgear.ModSpellSword;
import invoker54.arsgears.item.utilgear.PaxelItem;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import invoker54.arsgears.item.GearTier;
import net.minecraft.item.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemInit {
    private static final Logger LOGGER = LogManager.getLogger();

    public static ArrayList<Item> items = new ArrayList<>();

    public static Item addItem(Item item, String name){
        item.setRegistryName(ArsGears.MOD_ID, name);
        items.add(item);
        return item;
    }

    //region This iss for the Utility Gear
    public static final Item WOOD_PAXEL = addItem(new PaxelItem(ItemTier.WOOD, 0,-5, getDefault()), "wood_paxel");
    public static final Item STONE_PAXEL = addItem(new PaxelItem(ItemTier.STONE, 0,-5, getDefault()), "stone_paxel");
    public static final Item IRON_PAXEL = addItem(new PaxelItem(ItemTier.IRON, 0,-5, getDefault()), "iron_paxel");
    public static final Item DIAMOND_PAXEL = addItem(new PaxelItem(ItemTier.DIAMOND, 0,-5, getDefault()), "diamond_paxel");
    public static final Item WOOD_UTILITY_GEAR = addItem(new UtilGearItem(GearTier.WOOD, getDefault()), "wood_utility_gear");
    public static final Item STONE_UTILITY_GEAR = addItem(new UtilGearItem(GearTier.STONE, new Item.Properties()), "stone_utility_gear");
    public static final Item IRON_UTILITY_GEAR = addItem(new UtilGearItem(GearTier.IRON, new Item.Properties()), "iron_utility_gear");
    public static final Item DIAMOND_UTILITY_GEAR = addItem(new UtilGearItem(GearTier.DIAMOND, new Item.Properties()), "diamond_utility_gear");
    public static final Item ARCANE_UTILITY_GEAR = addItem(new UtilGearItem(GearTier.ARCANE, new Item.Properties()), "arcane_utility_gear");
//    public static final Item UTILITY_RUNE_1 = addItem(new UpgradeRune(STONE_UTILITY_GEAR, getDefault()), "utility_rune_1");
//    public static final Item UTILITY_RUNE_2 = addItem(new UpgradeRune(IRON_UTILITY_GEAR, getDefault()),"utility_rune_2");
//    public static final Item UTILITY_RUNE_3 = addItem(new UpgradeRune(DIAMOND_UTILITY_GEAR, getDefault()), "utility_rune_3");
//    public static final Item UTILITY_RUNE_4 = addItem(new UpgradeRune(ARCANE_UTILITY_GEAR, getDefault()), "utility_rune_4");

   //endregion

    //region This is for the Combat Gear (I will keep the combat gear item to use for changing into the other items
    public static final Item WOOD_COMBAT_GEAR = addItem(new CombatGearItem(getDefault()), "combat/wood_combat_gear");
    
    //Wood
    public static final Item WOODEN_MOD_SWORD = addItem(new ModSpellSword(GearTier.WOOD), "combat/wood_combat_sword");
    public static final Item WOODEN_MOD_BOW = addItem(new ModSpellBow(GearTier.WOOD), "combat/wood_combat_bow");
    public static final Item WOODEN_MOD_MIRROR = addItem(new ModSpellMirror(GearTier.WOOD), "combat/wood_combat_mirror");
    
    //Stone
    public static final Item STONE_MOD_SWORD = addItem(new ModSpellSword(GearTier.STONE), "combat/stone_combat_sword");
    public static final Item STONE_MOD_BOW = addItem(new ModSpellBow(GearTier.STONE), "combat/stone_combat_bow");
    public static final Item STONE_MOD_MIRROR = addItem(new ModSpellMirror(GearTier.STONE), "combat/stone_combat_mirror");
    
    //Iron
    public static final Item IRON_MOD_SWORD = addItem(new ModSpellSword(GearTier.IRON), "combat/iron_combat_sword");
    public static final Item IRON_MOD_BOW = addItem(new ModSpellBow(GearTier.IRON), "combat/iron_combat_bow");
    public static final Item IRON_MOD_MIRROR = addItem(new ModSpellMirror(GearTier.IRON), "combat/iron_combat_mirror");
    
    //Diamond
    public static final Item DIAMOND_MOD_SWORD = addItem(new ModSpellSword(GearTier.DIAMOND), "combat/diamond_combat_sword");
    public static final Item DIAMOND_MOD_BOW = addItem(new ModSpellBow(GearTier.DIAMOND), "combat/diamond_combat_bow");
    public static final Item DIAMOND_MOD_MIRROR = addItem(new ModSpellMirror(GearTier.DIAMOND), "combat/diamond_combat_mirror");
    
    //Arcane
    public static final Item ARCANE_MOD_SWORD = addItem(new ModSpellSword(GearTier.ARCANE), "combat/arcane_combat_sword");
    public static final Item ARCANE_MOD_BOW = addItem(new ModSpellBow(GearTier.ARCANE), "combat/arcane_combat_bow");
    public static final Item ARCANE_MOD_MIRROR = addItem(new ModSpellMirror(GearTier.ARCANE), "combat/arcane_combat_mirror");
    
    //Runes
    public static final Item COMBAT_RUNE_1 = 
            addItem(new UpgradeRune(GearTier.STONE, STONE_MOD_SWORD, STONE_MOD_BOW, STONE_MOD_MIRROR,false, getDefault()), "combat/combat_rune_1");
    public static final Item COMBAT_RUNE_2 = 
            addItem(new UpgradeRune(GearTier.IRON, IRON_MOD_SWORD, IRON_MOD_BOW, IRON_MOD_MIRROR,false, getDefault()), "combat/combat_rune_2");
    public static final Item COMBAT_RUNE_3 = 
            addItem(new UpgradeRune(GearTier.DIAMOND, DIAMOND_MOD_SWORD, DIAMOND_MOD_BOW, DIAMOND_MOD_MIRROR,false, getDefault()), "combat/combat_rune_3");
    public static final Item COMBAT_RUNE_4 = 
            addItem(new UpgradeRune(GearTier.ARCANE, ARCANE_MOD_SWORD, ARCANE_MOD_BOW, ARCANE_MOD_MIRROR,false, getDefault()), "combat/combat_rune_4");
    //endregion

    //region Regular items
    public static final Item STICK_CROSS = addItem(new Item(new Item.Properties().tab(ItemGroup.TAB_MATERIALS)), "stick_cross");
    //endregion

    public static Item.Properties getDefault() {
        return new Item.Properties().tab(ArsNouveau.itemGroup);
    }

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> itemRegistryEvent){
        IForgeRegistry<Item> registry = itemRegistryEvent.getRegistry();
        for (Item item: items){
            registry.register(item);
        }

        LOGGER.error(WOODEN_MOD_SWORD.getRegistryName().getPath());
    }
}
