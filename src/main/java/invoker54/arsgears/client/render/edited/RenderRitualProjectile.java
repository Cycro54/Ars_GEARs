package invoker54.arsgears.client.render.edited;

import com.hollingsworth.arsnouveau.client.renderer.entity.RenderBlank;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;


public class RenderRitualProjectile extends RenderBlank {
    public RenderRitualProjectile(EntityRendererManager renderManager, ResourceLocation entityTexture) {
        super(renderManager, entityTexture);
    }

    @Override
    public void render(Entity entity, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entity, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }
}
