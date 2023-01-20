package invoker54.arsgears.init;

import com.hollingsworth.arsnouveau.ArsNouveau;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.item.GearTier;
import invoker54.arsgears.item.UpgradeRune;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.item.combatgear.ModSpellBow;
import invoker54.arsgears.item.combatgear.ModSpellMirror;
import invoker54.arsgears.item.combatgear.ModSpellSword;
import invoker54.arsgears.item.utilgear.ModFishingRodItem;
import invoker54.arsgears.item.utilgear.ModHoeItem;
import invoker54.arsgears.item.utilgear.PaxelItem;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
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

    //region This is for the Utility Gear
    //Fake paxels (for crafting)
    public static final Item WOOD_PAXEL_FAKE = addItem(new Item(getDefault(true)), "utility/wood_paxel_fake");
    public static final Item STONE_PAXEL_FAKE = addItem(new Item(getDefault(true)), "utility/stone_paxel_fake");
    public static final Item IRON_PAXEL_FAKE = addItem(new Item(getDefault(true)), "utility/iron_paxel_fake");
    public static final Item DIAMOND_PAXEL_FAKE = addItem(new Item(getDefault(true)), "utility/diamond_paxel_fake");

    //Bait types
    /** This will be moved to the food overhaul mod I am making */
//    public static final Item BAIT_PORK = addItem(new BaitItem(getDefault(true)), "bait_pork");
//    public static final Item BAIT_BEEF = addItem(new BaitItem(getDefault(true)), "bait_beef");
//    public static final Item BAIT_CHICKEN = addItem(new BaitItem(getDefault(true)), "bait_chicken");
//    public static final Item BAIT_MUTTON = addItem(new BaitItem(getDefault(true)), "bait_mutton");
//    public static final Item BAIT_RABBIT = addItem(new BaitItem(getDefault(true)), "bait_rabbit");
//    public static final Item BAIT_ROTTEN_FLESH = addItem(new BaitItem(getDefault(true)), "bait_rotten_flesh");
//    public static final Item STARBUNCLE_BAIT = addItem(new BaitItem(getDefault(true)), "starbuncle_bait");

    //Paxel
    public static final Item WOOD_PAXEL = addItem(new PaxelItem(GearTier.WOOD, 1, -2.8F, getDefault(false)), "utility/wood_paxel");
    public static final Item STONE_PAXEL = addItem(new PaxelItem(GearTier.STONE, 1, -2.8F, getDefault(false)), "utility/stone_paxel");
    public static final Item IRON_PAXEL = addItem(new PaxelItem(GearTier.IRON, 1, -2.8F, getDefault(false)), "utility/iron_paxel");
    public static final Item DIAMOND_PAXEL = addItem(new PaxelItem(GearTier.DIAMOND, 1, -2.8F, getDefault(false)), "utility/diamond_paxel");
    public static final Item ARCANE_PAXEL = addItem(new PaxelItem(GearTier.ARCANE, 1, -2.8F, getDefault(false)), "utility/arcane_paxel");
    //Fishing Rod
    public static final Item WOOD_FISHING_ROD = addItem(new ModFishingRodItem(getDefault(false).durability(GearTier.WOOD.getUses())), "utility/wood_fishing_rod");
    public static final Item STONE_FISHING_ROD = addItem(new ModFishingRodItem(getDefault(false).durability(GearTier.STONE.getUses())), "utility/stone_fishing_rod");
    public static final Item IRON_FISHING_ROD = addItem(new ModFishingRodItem(getDefault(false).durability(GearTier.IRON.getUses())), "utility/iron_fishing_rod");
    public static final Item DIAMOND_FISHING_ROD = addItem(new ModFishingRodItem(getDefault(false).durability(GearTier.DIAMOND.getUses())), "utility/diamond_fishing_rod");
    public static final Item ARCANE_FISHING_ROD = addItem(new ModFishingRodItem(getDefault(false).durability(GearTier.ARCANE.getUses())), "utility/arcane_fishing_rod");
    //Hoe
    public static final Item WOOD_HOE = addItem(new ModHoeItem(GearTier.WOOD, 0, -3.0F, getDefault(false)), "utility/wood_hoe");
    public static final Item STONE_HOE = addItem(new ModHoeItem(GearTier.STONE, -1, -2.0F, getDefault(false)), "utility/stone_hoe");
    public static final Item IRON_HOE = addItem(new ModHoeItem(GearTier.IRON, -2, -1.0F, getDefault(false)), "utility/iron_hoe");
    public static final Item DIAMOND_HOE = addItem(new ModHoeItem(GearTier.DIAMOND, -3, 0.0F, getDefault(false)), "utility/diamond_hoe");
    public static final Item ARCANE_HOE = addItem(new ModHoeItem(GearTier.ARCANE, -4, 0.0F, getDefault(false)), "utility/arcane_hoe");
    
    public static final Item WOOD_UTILITY_GEAR = addItem(new UtilGearItem(GearTier.WOOD, getDefault(true)), "utility/wood_utility_gear");
    public static final Item UTILITY_RUNE_1 = 
            addItem(new UpgradeRune(GearTier.STONE, STONE_PAXEL, STONE_FISHING_ROD, STONE_HOE, true, getDefault(true)), "utility/utility_rune_1");
    public static final Item UTILITY_RUNE_2 = 
            addItem(new UpgradeRune(GearTier.IRON, IRON_PAXEL, IRON_FISHING_ROD, IRON_HOE, true, getDefault(true)), "utility/utility_rune_2");
    public static final Item UTILITY_RUNE_3 = 
            addItem(new UpgradeRune(GearTier.DIAMOND, DIAMOND_PAXEL, DIAMOND_FISHING_ROD, DIAMOND_HOE, true, getDefault(true)), "utility/utility_rune_3");
    public static final Item UTILITY_RUNE_4 = 
            addItem(new UpgradeRune(GearTier.ARCANE, ARCANE_PAXEL, ARCANE_FISHING_ROD, ARCANE_HOE, true, getDefault(true)), "utility/utility_rune_4");
    //endregion

    //region This is for the Combat Gear (I will keep the combat gear item to use for changing into the other items
    public static final Item WOOD_COMBAT_GEAR = addItem(new CombatGearItem(GearTier.WOOD, getDefault(true)), "combat/wood_combat_gear");
    
    //SWORD
    public static final Item WOODEN_MOD_SWORD = addItem(new ModSpellSword(GearTier.WOOD), "combat/wood_combat_sword");
    public static final Item STONE_MOD_SWORD = addItem(new ModSpellSword(GearTier.STONE), "combat/stone_combat_sword");
    public static final Item IRON_MOD_SWORD = addItem(new ModSpellSword(GearTier.IRON), "combat/iron_combat_sword");
    public static final Item DIAMOND_MOD_SWORD = addItem(new ModSpellSword(GearTier.DIAMOND), "combat/diamond_combat_sword");
    public static final Item ARCANE_MOD_SWORD = addItem(new ModSpellSword(GearTier.ARCANE), "combat/arcane_combat_sword");
    
    //BOW
    public static final Item WOODEN_MOD_BOW = addItem(new ModSpellBow(GearTier.WOOD), "combat/wood_combat_bow");
    public static final Item STONE_MOD_BOW = addItem(new ModSpellBow(GearTier.STONE), "combat/stone_combat_bow");
    public static final Item IRON_MOD_BOW = addItem(new ModSpellBow(GearTier.IRON), "combat/iron_combat_bow");
    public static final Item DIAMOND_MOD_BOW = addItem(new ModSpellBow(GearTier.DIAMOND), "combat/diamond_combat_bow");
    public static final Item ARCANE_MOD_BOW = addItem(new ModSpellBow(GearTier.ARCANE), "combat/arcane_combat_bow");
    
    //MIRROR
    public static final Item WOODEN_MOD_MIRROR = addItem(new ModSpellMirror(GearTier.WOOD), "combat/wood_combat_mirror");
    public static final Item STONE_MOD_MIRROR = addItem(new ModSpellMirror(GearTier.STONE), "combat/stone_combat_mirror");
    public static final Item IRON_MOD_MIRROR = addItem(new ModSpellMirror(GearTier.IRON), "combat/iron_combat_mirror");
    public static final Item DIAMOND_MOD_MIRROR = addItem(new ModSpellMirror(GearTier.DIAMOND), "combat/diamond_combat_mirror");
    public static final Item ARCANE_MOD_MIRROR = addItem(new ModSpellMirror(GearTier.ARCANE), "combat/arcane_combat_mirror");
    
    //Runes
    public static final Item COMBAT_RUNE_1 = 
            addItem(new UpgradeRune(GearTier.STONE, STONE_MOD_SWORD, STONE_MOD_BOW, STONE_MOD_MIRROR,false, getDefault(true)), "combat/combat_rune_1");
    public static final Item COMBAT_RUNE_2 = 
            addItem(new UpgradeRune(GearTier.IRON, IRON_MOD_SWORD, IRON_MOD_BOW, IRON_MOD_MIRROR,false, getDefault(true)), "combat/combat_rune_2");
    public static final Item COMBAT_RUNE_3 = 
            addItem(new UpgradeRune(GearTier.DIAMOND, DIAMOND_MOD_SWORD, DIAMOND_MOD_BOW, DIAMOND_MOD_MIRROR,false, getDefault(true)), "combat/combat_rune_3");
    public static final Item COMBAT_RUNE_4 = 
            addItem(new UpgradeRune(GearTier.ARCANE, ARCANE_MOD_SWORD, ARCANE_MOD_BOW, ARCANE_MOD_MIRROR,false, getDefault(true)), "combat/combat_rune_4");
    //endregion

    //region Regular items
    public static final Item STICK_CROSS = addItem(new Item(new Item.Properties().tab(ItemGroup.TAB_MATERIALS)), "stick_cross");
    //endregion

    public static Item.Properties getDefault(boolean itemGroup) {
        if (!itemGroup) return new Item.Properties();
        
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
