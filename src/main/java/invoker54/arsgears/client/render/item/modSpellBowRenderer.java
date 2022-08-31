package invoker54.arsgears.client.render.item;

import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.SpellCaster;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.client.renderer.item.FixedGeoItemRenderer;
import com.hollingsworth.arsnouveau.common.items.Wand;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import invoker54.arsgears.client.ModelData;
import invoker54.arsgears.client.Ticker;
import invoker54.arsgears.item.combatgear.ModSpellBow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;

import javax.annotation.Nullable;

import static java.lang.Math.PI;

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

            int maxTime = (currentItemStack == null) ? 7200 : currentItemStack.getItem().getUseDuration(currentItemStack);
            int timeHeld = maxTime - Minecraft.getInstance().player.getUseItemRemainingTicks();
            if (timeHeld > 0 && timeHeld != maxTime && timeHeld > (ModSpellBow.getChargeDuration(currentItemStack)/2f)) {
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
        float outerAngle = (float) Math.toRadians (Ticker.getDelta(true, true) * 10f);
        top.setRotationZ((float) Math.toRadians(-10.0));
        top.setRotationY(0.0F);
        top.setRotationX(0.0F);
        bottom.setRotationZ((float) Math.toRadians(10.0));
        bottom.setRotationY(0.0F);
        bottom.setRotationX((float) Math.toRadians(-180.0));
        float percentage = 0F;
        if (Minecraft.getInstance().player.getMainHandItem().equals(this.currentItemStack)) {
            int maxTime = currentItemStack.getItem().getUseDuration(currentItemStack);
            int timeHeld = (int) ((float) (maxTime - Minecraft.getInstance().player.getUseItemRemainingTicks()) + partialTicks);
            if (timeHeld != 0 && timeHeld != maxTime) {
                percentage = Math.min(timeHeld/ModSpellBow.getChargeDuration(currentItemStack), 1);
                //EaseOutQuad formula
                percentage = (float) Math.sin((percentage * PI) / 2);
                top.setRotationZ((float) (Math.toRadians(-10.0) - Math.toRadians((double) percentage * 17) * 2.0));
                bottom.setRotationZ((float) (Math.toRadians(-10.0) + Math.toRadians((double) percentage * 17) * 2.0));
                outerAngle = (float) Math.toRadians (Ticker.getDelta(true, true) * MathHelper.lerp(percentage, 14F, -1F));
                if (percentage == 1){
                    top.setRotationZ((float) (Math.toRadians(-10.0) - Math.toRadians((double) (percentage * 17) + 3) * 2.0));
                    bottom.setRotationZ((float) (Math.toRadians(-10.0) + Math.toRadians((double) (percentage * 17) + 3) * 2.0));
                    outerAngle = (float) Math.toRadians (Ticker.getDelta(true, true) * -30F);
                }
//                if (timeHeld >= ModSpellBow.getChargeDuration(currentItemStack)) {
//                    top.setRotationZ((float) (Math.toRadians(-10.0) - Math.toRadians(19.0) * 2.0));
//                    bottom.setRotationZ((float) (Math.toRadians(-10.0) + Math.toRadians(19.0) * 2.0));
//                    outerAngle = ((float) ClientInfo.ticksInGame + partialTicks) / 3.0F % 360.0F;
//                }
            }
        }
        gem.setRotationX(ModelData.setData(currentItemStack, "GrotX", outerAngle, true));
        gem.setRotationY(ModelData.setData(currentItemStack, "GrotY", outerAngle, true));
        float scale;
        //if the player isn't holding use down or the bow is fully charged
        if (percentage == 0 || (percentage == 1)) {
            scale = ModelData.setData(currentItemStack, "Scale", outerAngle, true);
            scale = (float) MathHelper.lerp((Math.sin(scale) + 1) / 2F, 0.75f, 1);
        }
        //If the player is holding use down
        else{
            scale = MathHelper.lerp(percentage, 1.2f, 0.6f);
            ModelData.setData(currentItemStack, "Scale", 1, false);
        }
        gem.setScaleX(scale);
        gem.setScaleY(scale);
        gem.setScaleZ(scale);

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
