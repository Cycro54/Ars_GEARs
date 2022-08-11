package invoker54.arsgears.client.event;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.init.ItemInit;
import invoker54.arsgears.event.item.utilgear.UtilGearItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD, modid = ArsGears.MOD_ID)
public class UtilGearPropertyEvent {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void propertyOverrideRegistry(FMLClientSetupEvent event) {
        //This changes the selected item model
        IItemPropertyGetter UtilityChanger = (itemStack, clientWorld, entity) -> {
            if(entity == null) return -1;
            if(!(entity instanceof PlayerEntity)) return -1;

            if (ArsUtil.getHeldItem(entity, UtilGearItem.class).isEmpty()) return -1;

            float change = (((PlayerEntity) entity).fishing == null) ? 0 : 0.5f;

            return GearCap.getCap(itemStack).getSelectedItem() + change;
        };
        ResourceLocation selected_item = new ResourceLocation(ArsGears.MOD_ID, "selected_item");

        event.enqueueWork(() -> {
            ItemModelsProperties.register(ItemInit.WOOD_UTILITY_GEAR.getItem(), selected_item, UtilityChanger);
            ItemModelsProperties.register(ItemInit.STONE_UTILITY_GEAR.getItem(), selected_item, UtilityChanger);
            ItemModelsProperties.register(ItemInit.IRON_UTILITY_GEAR.getItem(), selected_item, UtilityChanger);
            ItemModelsProperties.register(ItemInit.DIAMOND_UTILITY_GEAR.getItem(), selected_item, UtilityChanger);
            ItemModelsProperties.register(ItemInit.ARCANE_UTILITY_GEAR.getItem(), selected_item, UtilityChanger);
        });
    }
}
