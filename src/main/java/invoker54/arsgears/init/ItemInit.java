package invoker54.arsgears.init;

import com.hollingsworth.arsnouveau.ArsNouveau;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.client.render.CombatGearRenderer;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.item.combatgear.ModBowItem;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import invoker54.arsgears.item.GearTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemInit {

    //region This is for the Utility Gear
//    public static final Item WOOD_PAXEL = new PaxelItem(ItemTier.WOOD, 9, 3f,
//            new Item.Properties()).setRegistryName(ArsGears.MOD_ID, "wood_paxel");

    public static final Item WOOD_UTILITY_GEAR = new UtilGearItem(GearTier.WOOD,
            new Item.Properties().tab(ItemGroup.TAB_TOOLS)).setRegistryName(ArsGears.MOD_ID, "wood_utility_gear");

   //endregion

    //region This is for the Combat Gear
    //public static final Item MOD_SWORD = new ModSwordItem().setRegistryName(ArsGears.MOD_ID, "wood_mod_sword");
    public static final Item WOOD_COMBAT_GEAR = new CombatGearItem(GearTier.WOOD,
            new Item.Properties().tab(ItemGroup.TAB_COMBAT).setISTER(() -> CombatGearRenderer::new)).setRegistryName(ArsGears.MOD_ID, "wood_combat_gear");
    public static final Item WOOD_MOD_BOW = new ModBowItem().setRegistryName(ArsNouveau.MODID, "spell_bow");
//    public static final Item WOOD_MOD_MIRROR = new ModMirrorItem((new Item.Properties())).setRegistryName(ArsGears.MOD_ID, "wood_mod_mirror");
    //endregion

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> itemRegistryEvent){
        IForgeRegistry<Item> registry = itemRegistryEvent.getRegistry();

        //region This is for the Utility Gear
        registry.register(WOOD_UTILITY_GEAR);
        //endregion

        //region This is for the Combat Gear
        registry.register(WOOD_COMBAT_GEAR);
        registry.register(WOOD_MOD_BOW);
//        registry.register(WOOD_MOD_MIRROR);
        //endregion
    }
}
