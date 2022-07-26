package invoker54.arsgears.client.keybind;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.capability.utilgear.UtilGearCap;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import invoker54.arsgears.network.NetworkHandler;
import invoker54.arsgears.network.message.CycleUtilityGearMsg;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsGears.MOD_ID)
public class KeybindsInit {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ArrayList<CustomKeybind> gearBinds = new ArrayList<>();
    public static CustomKeybind cycleSelectedItem_utility;

    public static void registerKeys(FMLClientSetupEvent event){
        //Open/Close Shop
        cycleSelectedItem_utility = new CustomKeybind("cycle_gear", GLFW.GLFW_KEY_GRAVE_ACCENT, (action) ->{
            if(action != GLFW.GLFW_PRESS) return;
            LOGGER.debug("What's the active item: " + ClientUtil.mC.player.getMainHandItem().getItem().getClass());
            ItemStack item = ClientUtil.mC.player.getMainHandItem();
            if(item.getItem() instanceof UtilGearItem){
//                UtilGearCap cap = UtilGearCap.getCap(item);
//                cap.cycleItem();
                NetworkHandler.INSTANCE.sendToServer(new CycleUtilityGearMsg());
            }
        });
        gearBinds.add(cycleSelectedItem_utility);
    }
}
