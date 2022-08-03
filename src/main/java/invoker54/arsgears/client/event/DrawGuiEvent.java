package invoker54.arsgears.client.event;

import invoker54.arsgears.ArsGears;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsGears.MOD_ID)
public class DrawGuiEvent {

    private static final modGuiSpellHUD modSpellHUD = new modGuiSpellHUD();

    @SubscribeEvent
    public static void renderSpellHUD(final RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        modSpellHUD.drawHUD(event.getMatrixStack());
    }
}