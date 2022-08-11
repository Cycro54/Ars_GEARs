package invoker54.arsgears.client.event;

import invoker54.arsgears.ArsGears;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = ArsGears.MOD_ID)
public class CombatGearPropertyEvent {
    private static final Logger LOGGER = LogManager.getLogger();

//    @SubscribeEvent
//    public static void propertyOverrideRegistry(FMLClientSetupEvent event) {
//        IItemPropertyGetter combatChanger = (itemStack, clientWorld, entity) -> {
//            if(entity == null) return -1;
//
//            if(!(entity instanceof PlayerEntity)) return -1;
//
//            if (ArsUtil.getHeldItem(entity, CombatGearItem.class).isEmpty()) return -1;
//
//            return CombatGearCap.getCap(itemStack).getSelectedItem();
//        };
//        ResourceLocation selected_item = new ResourceLocation(ArsGears.MOD_ID, "selected_item");
//
//        event.enqueueWork(() -> {
//            ItemModelsProperties.register(ItemInit.WOOD_COMBAT_GEAR.getItem(), selected_item, combatChanger);
//            ItemModelsProperties.register(ItemInit.STONE_COMBAT_GEAR.getItem(), selected_item, combatChanger);
//            ItemModelsProperties.register(ItemInit.IRON_COMBAT_GEAR.getItem(), selected_item, combatChanger);
//            ItemModelsProperties.register(ItemInit.DIAMOND_COMBAT_GEAR.getItem(), selected_item, combatChanger);
//            ItemModelsProperties.register(ItemInit.ARCANE_COMBAT_GEAR.getItem(), selected_item, combatChanger);
//        });
//    }
}
