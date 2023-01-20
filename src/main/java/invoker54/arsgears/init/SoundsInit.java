package invoker54.arsgears.init;

import invoker54.arsgears.ArsGears;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID)
public class SoundsInit {

    private static final Logger LOGGER = LogManager.getLogger();

    public static ArrayList<SoundEvent> soundEvents = new ArrayList<>();

    public static final SoundEvent GEAR_EAT = addSound("gear_eat");
    public static final SoundEvent GEAR_SWITCH = addSound("gear_switch");
    public static final SoundEvent GEAR_ACTIVATE = addSound("gear_activate");
    public static final SoundEvent GEAR_CAST = addSound("gear_cast");
    public static SoundEvent addSound(String name){
        ResourceLocation soundSource = new ResourceLocation(ArsGears.MOD_ID, name);
        return new SoundEvent(soundSource);
    }

    @SubscribeEvent
    public static void registerSounds(final RegistryEvent.Register<SoundEvent> soundEventRegister){
        IForgeRegistry<SoundEvent> registry = soundEventRegister.getRegistry();
        for (SoundEvent sound: soundEvents){
            registry.register(sound);
        }
        LOGGER.error(GEAR_EAT.getRegistryName().getPath());
    }
}
