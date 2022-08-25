package invoker54.arsgears.init;

import com.hollingsworth.arsnouveau.ArsNouveau;
import invoker54.arsgears.client.render.CustomFishRenderer;
import invoker54.arsgears.client.render.edited.RenderRitualProjectile;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RenderInit {

    public static void initializeRenderers(){
        RenderingRegistry.registerEntityRenderingHandler(EntityInit.FISHING_BOBBER, CustomFishRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityInit.MODDED_ORBIT, renderManager -> new RenderRitualProjectile(renderManager, new ResourceLocation(ArsNouveau.MODID, "textures/entity/spell_proj.png")));
    }

}
