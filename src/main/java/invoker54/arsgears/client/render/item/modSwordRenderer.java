package invoker54.arsgears.client.render.item;

import com.hollingsworth.arsnouveau.client.renderer.item.FixedGeoItemRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import invoker54.arsgears.event.item.combatgear.ModSpellSword;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;

import javax.annotation.Nullable;

public class modSwordRenderer  extends FixedGeoItemRenderer<ModSpellSword> {
    public modSwordRenderer() {
        super(new modSwordModel());
    }

    @Override
    public RenderType getRenderType(Object animatable, float partialTicks, MatrixStack stack, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }

    @Override
    public void render(GeoModel model, Object animatable, float partialTicks, RenderType type, MatrixStack matrixStackIn, @org.jetbrains.annotations.Nullable IRenderTypeBuffer renderTypeBuffer, @org.jetbrains.annotations.Nullable IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        //Added vertexBuilder so that I could add the enchantment glint effect (AND IT WORKS, CAN YOU BELIEVE IT?!!? HAHAHAAA)
        vertexBuilder = ItemRenderer.getFoilBuffer(renderTypeBuffer, type, true, currentItemStack.hasFoil());

        renderEarly(animatable, matrixStackIn, partialTicks, renderTypeBuffer, vertexBuilder, packedLightIn,
                packedOverlayIn, red, green, blue, alpha);

        renderLate(animatable, matrixStackIn, partialTicks, renderTypeBuffer, vertexBuilder, packedLightIn,
                packedOverlayIn, red, green, blue, alpha);
        // Render all top level bones
        for (GeoBone group : model.topLevelBones) {
            renderRecursively(group, matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue,
                    alpha);
        }
    }
}
