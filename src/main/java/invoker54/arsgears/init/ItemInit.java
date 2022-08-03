package invoker54.arsgears.init;

import com.hollingsworth.arsnouveau.ArsNouveau;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.client.render.CombatGearRenderer;
import invoker54.arsgears.item.UpgradeRune;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import invoker54.arsgears.item.GearTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ToolItem;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemInit {
    private static final Logger LOGGER = LogManager.getLogger();

    //region This is for the Utility Gear
    public static final Item WOOD_UTILITY_GEAR = new UtilGearItem(GearTier.WOOD,
            getDefault()).setRegistryName(ArsGears.MOD_ID, "wood_utility_gear");
    public static final Item STONE_UTILITY_GEAR = new UtilGearItem(GearTier.STONE,
            new Item.Properties()).setRegistryName(ArsGears.MOD_ID, "stone_utility_gear");
    public static final Item IRON_UTILITY_GEAR = new UtilGearItem(GearTier.IRON,
            new Item.Properties()).setRegistryName(ArsGears.MOD_ID, "iron_utility_gear");
    public static final Item ARCANE_UTILITY_GEAR = new UtilGearItem(GearTier.ARCANE,
            new Item.Properties()).setRegistryName(ArsGears.MOD_ID, "arcane_utility_gear");
    public static final Item UTILITY_RUNE_1 = new UpgradeRune((ToolItem) STONE_UTILITY_GEAR, getDefault()).setRegistryName(ArsGears.MOD_ID, "utility_rune_1");
    public static final Item UTILITY_RUNE_2 = new UpgradeRune((ToolItem) IRON_UTILITY_GEAR, getDefault()).setRegistryName(ArsGears.MOD_ID, "utility_rune_2");
    public static final Item UTILITY_RUNE_3 = new UpgradeRune((ToolItem) ARCANE_UTILITY_GEAR, getDefault()).setRegistryName(ArsGears.MOD_ID, "utility_rune_3");

   //endregion

    //region This is for the Combat Gear
    //public static final Item MOD_SWORD = new ModSwordItem().setRegistryName(ArsGears.MOD_ID, "wood_mod_sword");
    public static final Item WOOD_COMBAT_GEAR = new CombatGearItem(GearTier.WOOD,
            getDefault().setISTER(() -> CombatGearRenderer::new)).setRegistryName(ArsGears.MOD_ID, "wood_combat_gear");
    public static final Item STONE_COMBAT_GEAR = new CombatGearItem(GearTier.STONE,
            new Item.Properties().setISTER(() -> CombatGearRenderer::new)).setRegistryName(ArsGears.MOD_ID, "stone_combat_gear");
    public static final Item IRON_COMBAT_GEAR = new CombatGearItem(GearTier.IRON,
            new Item.Properties().setISTER(() -> CombatGearRenderer::new)).setRegistryName(ArsGears.MOD_ID, "iron_combat_gear");
    public static final Item ARCANE_COMBAT_GEAR = new CombatGearItem(GearTier.ARCANE,
            new Item.Properties().setISTER(() -> CombatGearRenderer::new)).setRegistryName(ArsGears.MOD_ID, "arcane_combat_gear");
    public static final Item COMBAT_RUNE_1 = new UpgradeRune((ToolItem) STONE_COMBAT_GEAR, getDefault()).setRegistryName(ArsGears.MOD_ID, "combat_rune_1");
    public static final Item COMBAT_RUNE_2 = new UpgradeRune((ToolItem) IRON_COMBAT_GEAR, getDefault()).setRegistryName(ArsGears.MOD_ID, "combat_rune_2");
    public static final Item COMBAT_RUNE_3 = new UpgradeRune((ToolItem) ARCANE_COMBAT_GEAR, getDefault()).setRegistryName(ArsGears.MOD_ID, "combat_rune_3");
    //endregion

    //region Regular items
    public static final Item STICK_CROSS = new Item(new Item.Properties().tab(ItemGroup.TAB_MATERIALS)).setRegistryName(ArsGears.MOD_ID, "stick_cross");
    //endregion

    public static Item.Properties getDefault() {
        return new Item.Properties().tab(ArsNouveau.itemGroup);
    }

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> itemRegistryEvent){
        IForgeRegistry<Item> registry = itemRegistryEvent.getRegistry();
        //region This is for the Utility Gear
        registry.register(WOOD_UTILITY_GEAR);
        registry.register(UTILITY_RUNE_1);
        registry.register(IRON_UTILITY_GEAR);
        registry.register(UTILITY_RUNE_2);
        registry.register(STONE_UTILITY_GEAR);
        registry.register(UTILITY_RUNE_3);
        registry.register(ARCANE_UTILITY_GEAR);
        //endregion

        //region This is for the Combat Gear
        registry.register(WOOD_COMBAT_GEAR);
        registry.register(COMBAT_RUNE_1);
        registry.register(STONE_COMBAT_GEAR);
        registry.register(COMBAT_RUNE_2);
        registry.register(IRON_COMBAT_GEAR);
        registry.register(COMBAT_RUNE_3);
        registry.register(ARCANE_COMBAT_GEAR);
        //endregion

        //region this is for regular items
        registry.register(STICK_CROSS);
        //endregion

        LOGGER.debug("What is the stone combat item? " + (STONE_COMBAT_GEAR.getRegistryName()));
        LOGGER.debug("What is the stone combat item? " + (IRON_COMBAT_GEAR.getRegistryName()));
        LOGGER.debug("What is the stone combat item? " + (ARCANE_COMBAT_GEAR.getRegistryName()));
    }
}
