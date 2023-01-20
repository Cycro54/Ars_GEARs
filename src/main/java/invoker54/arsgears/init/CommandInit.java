package invoker54.arsgears.init;

import com.mojang.brigadier.CommandDispatcher;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.commands.SetGearCommand;
import net.minecraft.command.CommandSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsGears.MOD_ID)
public class CommandInit {

    @SubscribeEvent
    public static void onRegisterCommandEvent(RegisterCommandsEvent event){
        CommandDispatcher<CommandSource> commandDispatcher = event.getDispatcher();

        SetGearCommand.register(commandDispatcher);
    }
}
