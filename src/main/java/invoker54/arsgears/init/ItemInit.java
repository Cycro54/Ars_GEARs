package invoker54.arsgears.init;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.enchanting_apparatus.EnchantingApparatusRecipe;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.client.render.CombatGearRenderer;
import invoker54.arsgears.item.UpgradeRune;
import invoker54.arsgears.item.combatgear.CombatGearItem;
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
    public static final Item UTILITY_RUNE_1 = addItem(new UpgradeRune(STONE_UTILITY_GEAR, getDefault()), "utility_rune_1");
    public static final Item UTILITY_RUNE_2 = addItem(new UpgradeRune(IRON_UTILITY_GEAR, getDefault()),"utility_rune_2");
    public static final Item UTILITY_RUNE_3 = addItem(new UpgradeRune(DIAMOND_UTILITY_GEAR, getDefault()), "utility_rune_3");
    public static final Item UTILITY_RUNE_4 = addItem(new UpgradeRune(ARCANE_UTILITY_GEAR, getDefault()), "utility_rune_4");

   //endregion

    //region This is for the Combat Gear
    //public static final Item MOD_SWORD = new ModSwordItem().setRegistryName(ArsGears.MOD_ID, "wood_mod_sword");
    public static final Item WOOD_COMBAT_GEAR = addItem(new CombatGearItem(GearTier.WOOD, getDefault()), "wood_combat_gear");
    public static final Item STONE_COMBAT_GEAR = addItem(new CombatGearItem(GearTier.STONE, new Item.Properties()), "stone_combat_gear");
    public static final Item IRON_COMBAT_GEAR = addItem(new CombatGearItem(GearTier.IRON, new Item.Properties()), "iron_combat_gear");
    public static final Item DIAMOND_COMBAT_GEAR = addItem(new CombatGearItem(GearTier.DIAMOND, new Item.Properties()), "diamond_combat_gear");
    public static final Item ARCANE_COMBAT_GEAR = addItem(new CombatGearItem(GearTier.ARCANE, new Item.Properties()), "arcane_combat_gear");
    public static final Item COMBAT_RUNE_1 = addItem(new UpgradeRune(STONE_COMBAT_GEAR, getDefault()), "combat_rune_1");
    public static final Item COMBAT_RUNE_2 = addItem(new UpgradeRune(IRON_COMBAT_GEAR, getDefault()), "combat_rune_2");
    public static final Item COMBAT_RUNE_3 = addItem(new UpgradeRune(DIAMOND_COMBAT_GEAR, getDefault()), "combat_rune_3");
    public static final Item COMBAT_RUNE_4 = addItem(new UpgradeRune(ARCANE_COMBAT_GEAR, getDefault()), "combat_rune_4");
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
    }

}
