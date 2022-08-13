package invoker54.arsgears.client.event;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.init.ItemInit;
import invoker54.arsgears.item.utilgear.ModFishingRodItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class UtilGearPropertyEvent {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void propertyOverrideRegistry(FMLClientSetupEvent event) {
        //This changes the selected item model
        IItemPropertyGetter UtilityChanger = (itemStack, clientWorld, entity) -> {
            LOGGER.debug("ENTITY NULL? " + (entity == null));
            if (entity == null) {
                return 0.0F;
            } else {
                LOGGER.debug("IS WE CARRYING MODFISHROD? " + !(ArsUtil.getHeldItem(entity, ModFishingRodItem.class).isEmpty()));
                boolean flag = !(ArsUtil.getHeldItem(entity, ModFishingRodItem.class).isEmpty());

                return flag && entity instanceof PlayerEntity && ((PlayerEntity)entity).fishing != null ? 1.0F : 0.0F;
            }
        };

        ResourceLocation isFishing = new ResourceLocation("cast");

        event.enqueueWork(() -> {
            ItemModelsProperties.register(ItemInit.WOOD_FISHING_ROD, isFishing, UtilityChanger);
            ItemModelsProperties.register(ItemInit.STONE_FISHING_ROD, isFishing, UtilityChanger);
            ItemModelsProperties.register(ItemInit.IRON_FISHING_ROD, isFishing, UtilityChanger);
            ItemModelsProperties.register(ItemInit.DIAMOND_FISHING_ROD, isFishing, UtilityChanger);
            ItemModelsProperties.register(ItemInit.ARCANE_FISHING_ROD, isFishing, UtilityChanger);
        });
    }
}
