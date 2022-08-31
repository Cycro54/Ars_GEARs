package invoker54.arsgears.config;

import com.hollingsworth.arsnouveau.api.RegistryHelper;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.init.GlyphInit;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID)
public class Config {

    public static void registerGlyphConfigs(){
        RegistryHelper.generateConfig(ArsGears.MOD_ID, GlyphInit.registeredSpells);
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) { }

    @SubscribeEvent
    public static void onReload(final ModConfig.Reloading configEvent) { }
}
