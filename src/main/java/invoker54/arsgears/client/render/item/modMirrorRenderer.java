package invoker54.arsgears.client.render.item;

import com.hollingsworth.arsnouveau.client.renderer.tile.GenericModel;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import invoker54.arsgears.item.combatgear.ModSpellMirror;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class modMirrorRenderer extends GeoItemRenderer<ModSpellMirror> {
    public static AnimatedGeoModel model = new GenericModel("enchanters_mirror", "items");

    public modMirrorRenderer() {
        super(model);
    }

//    public static GenericItemRenderer getISTER() {
//        return new GenericItemRenderer(model);
//    }

    @Override
    public void render(GeoModel model, ModSpellMirror animatable, float partialTicks, RenderType type, MatrixStack matrixStackIn, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
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
