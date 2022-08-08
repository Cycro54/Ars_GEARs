package invoker54.arsgears.client.render;

import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.client.render.item.modMirrorRenderer;
import invoker54.arsgears.client.render.item.modSpellBowRenderer;
import invoker54.arsgears.client.render.item.modSwordRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
I don't think I need this, item property override works just fine.
 */
public class CombatGearRenderer extends ItemStackTileEntityRenderer {
    private static final Logger LOGGER = LogManager.getLogger();
    modSwordRenderer swordRenderer = new modSwordRenderer();
    modSpellBowRenderer modSpellBowRenderer = new modSpellBowRenderer();
    modMirrorRenderer mirrorRenderer = new modMirrorRenderer();

    @Override
    public void renderByItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack stack, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

        //Grab the cap, so we can get the combat gear's mode
        CombatGearCap cap = CombatGearCap.getCap(itemStack);

        switch (cap.getSelectedItem()){
            //Sword
            default:
                swordRenderer.
                        renderByItem(itemStack, transformType, stack, bufferIn, combinedLightIn, combinedOverlayIn);
                break;
                case 1:
                    modSpellBowRenderer.
                        renderByItem(itemStack, transformType, stack, bufferIn, combinedLightIn, combinedOverlayIn);
                break;
                case 2:
                    mirrorRenderer.
                            renderByItem(itemStack, transformType, stack, bufferIn, combinedLightIn, combinedOverlayIn);
                break;
        }

    }
}
