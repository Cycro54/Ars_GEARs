package invoker54.arsgears.client.event;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.capability.UtilGearCap;
import invoker54.arsgears.init.ItemInit;
import net.minecraft.entity.player.PlayerEntity;
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
        event.enqueueWork(() -> {

            //This changes the selected item model
            ItemModelsProperties.register(ItemInit.WOOD_UTILITY_GEAR.getItem(),
                    new ResourceLocation(ArsGears.MOD_ID, "selected_item"),
                    (itemStack, clientWorld, livingEntity) -> {
                        if(livingEntity == null) return -1;
                        if(!(livingEntity instanceof PlayerEntity)) return -1;
                        boolean isMainhand = livingEntity.getMainHandItem() == itemStack;
                        boolean isOffHand = livingEntity.getOffhandItem() == itemStack;
                        if(!isMainhand && !isOffHand) return -1;

                        float change = (((PlayerEntity) livingEntity).fishing == null) ? 0 : 0.5f;

                        return UtilGearCap.getCap(itemStack).getSelectedItem() + change;
                    });
        });
    }
}
