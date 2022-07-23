package invoker54.arsgears.init;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.item.utilgear.PaxelItem;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import invoker54.arsgears.item.utilgear.UtilityGearTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemTier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemInit {

    public static final Item WOOD_PAXEL = new PaxelItem(ItemTier.WOOD, 9, 3f,
            new Item.Properties()).setRegistryName(ArsGears.MOD_ID, "wood_paxel");

    public static final Item WOOD_UTILITY_GEAR = new UtilGearItem(UtilityGearTier.WOOD,
            new Item.Properties().tab(ItemGroup.TAB_TOOLS)).setRegistryName(ArsGears.MOD_ID, "wood_utility_gear");

    //public static final EntityType<CustomBobber> CUSTOM_BOBBER_ENTITY_TYPE

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> itemRegistryEvent){
        IForgeRegistry<Item> registry = itemRegistryEvent.getRegistry();

        registry.register(WOOD_PAXEL);

        registry.register(WOOD_UTILITY_GEAR);
    }
}
