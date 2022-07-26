package invoker54.arsgears.client.event;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.client.keybind.CustomKeybind;
import invoker54.arsgears.client.keybind.KeybindsInit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsGears.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class InputEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onKeyPress(InputEvent.KeyInputEvent event){
        onInput(event.getAction(), event.getKey());
    }

    @SubscribeEvent
    public static void onMousePress(InputEvent.MouseInputEvent event){
        onInput(event.getAction(), event.getButton());
    }

    private static void onInput(int action, int key){
        //LOGGER.debug("Is there a world?? " + (ClientUtil.mC.level == null));
        if (ClientUtil.mC.level == null) return;

        for (CustomKeybind cKeyBind : KeybindsInit.gearBinds){
            if (cKeyBind.keyBind.getKey().getValue() == key){
                cKeyBind.pressed(action);
                break;
            }
        }
    }
}
