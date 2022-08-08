package invoker54.arsgears.client.render.item;

import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.client.renderer.item.FixedGeoItemRenderer;
import com.hollingsworth.arsnouveau.common.items.Wand;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;

import javax.annotation.Nullable;

public class modSpellBowRenderer extends FixedGeoItemRenderer<Wand> {
    private static final Logger LOGGER = LogManager.getLogger();

    public modSpellBowRenderer() {
        super(new modSpellBowModel());
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack stack, IRenderTypeBuffer bufferIn, int combinedLightIn, int p_239207_6_) {
        if (transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) {
            PlayerEntity player = Minecraft.getInstance().player;
            Vector3d playerPos = player.position().add(0.0, (double)player.getEyeHeight(), 0.0);
            Vector3d look = player.getLookAngle();
            Vector3d right = (new Vector3d(-look.z, 0.0, look.x)).normalize();
            Vector3d down = right.cross(look);
            right = right.scale(0.2 - (double)player.attackAnim);
            Vector3d forward = look.scale(0.44999998807907104);
            down = down.scale(-0.1 - (double)player.attackAnim);
            Vector3d laserPos = playerPos.add(right);
            laserPos = laserPos.add(forward);
            laserPos = laserPos.add(down);
            ISpellCaster tool = SpellCaster.deserialize(itemStack);
            int timeHeld = 72000 - Minecraft.getInstance().player.getUseItemRemainingTicks();
            if (timeHeld > 0 && timeHeld != 72000) {
                float scaleAge = (float) ParticleUtil.inRange(0.05, 0.1);
                if (player.level.random.nextInt(6) == 0) {
                    for(int i = 0; i < 1; ++i) {
                        Vector3d particlePos = new Vector3d(laserPos.x, laserPos.y, laserPos.z);
                        particlePos = particlePos.add(ParticleUtil.pointInSphere().scale(0.30000001192092896));
                        player.level.addParticle(ParticleLineData.createData(tool.getColor().toParticleColor(), scaleAge, 5 + player.level.random.nextInt(20)), particlePos.x(), particlePos.y(), particlePos.z(), laserPos.x(), laserPos.y(), laserPos.z());
                    }
                }
            }
        }

        super.renderByItem(itemStack, transformType, stack, bufferIn, combinedLightIn, p_239207_6_);
    }

    @Override
    public void render(GeoModel model, Object animatable, float partialTicks, RenderType type, MatrixStack matrixStackIn, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        //Added vertexBuilder so that I could add the enchantment glint effect (AND IT WORKS, CAN YOU BELIEVE IT?!!? HAHAHAAA)
        vertexBuilder = ItemRenderer.getFoilBuffer(renderTypeBuffer, type, true, currentItemStack.hasFoil());
        IBone top = (IBone) model.getBone("bow_top").get();
        IBone gem = (IBone) model.getBone("gem").get();
        IBone bottom = (IBone) model.getBone("bow_top2").get();
        float outerAngle = ((float) ClientInfo.ticksInGame + partialTicks) / 10.0F % 360.0F;
        top.setRotationZ((float) Math.toRadians(-10.0));
        top.setRotationY(0.0F);
        top.setRotationX(0.0F);
        bottom.setRotationZ((float) Math.toRadians(10.0));
        bottom.setRotationY(0.0F);
        bottom.setRotationX((float) Math.toRadians(-180.0));
        if (Minecraft.getInstance().player.getMainHandItem().equals(this.currentItemStack)) {
            int timeHeld = (int) ((float) (72000 - Minecraft.getInstance().player.getUseItemRemainingTicks()) + partialTicks);
            if (timeHeld != 0 && timeHeld != 72000) {
                top.setRotationZ((float) (Math.toRadians(-10.0) - Math.toRadians((double) timeHeld) * 2.0));
                bottom.setRotationZ((float) (Math.toRadians(-10.0) + Math.toRadians((double) timeHeld) * 2.0));
                outerAngle = ((float) ClientInfo.ticksInGame + partialTicks) / 5.0F % 360.0F;
                if (timeHeld >= 19) {
                    top.setRotationZ((float) (Math.toRadians(-10.0) - Math.toRadians(19.0) * 2.0));
                    bottom.setRotationZ((float) (Math.toRadians(-10.0) + Math.toRadians(19.0) * 2.0));
                    outerAngle = ((float) ClientInfo.ticksInGame + partialTicks) / 3.0F % 360.0F;
                }
            }
        }

        gem.setRotationX(outerAngle);
        gem.setRotationY(outerAngle);

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

    public RenderType getRenderType(Object animatable, float partialTicks, MatrixStack stack, @Nullable IRenderTypeBuffer renderTypeBuffer, @Nullable IVertexBuilder vertexBuilder, int packedLightIn, ResourceLocation textureLocation) {
        return RenderType.entityTranslucent(textureLocation);
    }
}
