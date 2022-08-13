package invoker54.arsgears.init;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.client.gui.container.GearContainer;
import invoker54.arsgears.client.gui.container.GearContainerScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ContainerInit {
    public static ContainerType<GearContainer> gearContainerType;

    @SubscribeEvent
    public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event){
        gearContainerType = IForgeContainerType.create(GearContainer::createContainer);
        gearContainerType.setRegistryName(ArsGears.MOD_ID, "gear_container");

        event.getRegistry().register(gearContainerType);
    }

    public static void initialize(){
        ScreenManager.register(gearContainerType, GearContainerScreen::new);
    }
}
