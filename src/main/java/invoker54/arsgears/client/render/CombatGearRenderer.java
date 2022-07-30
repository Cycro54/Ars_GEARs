package invoker54.arsgears.client.render;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.renderer.item.*;
import com.hollingsworth.arsnouveau.common.items.SpellBow;
import com.hollingsworth.arsnouveau.common.items.Wand;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.client.render.item.modSpellBowRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.client.extensions.IForgeBakedModel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
I don't think I need this, item property override works just fine.
 */
public class CombatGearRenderer extends ItemStackTileEntityRenderer {
    private static final Logger LOGGER = LogManager.getLogger();
    ItemStack swordStack;
    modSpellBowRenderer modSpellBow = new modSpellBowRenderer();
    ItemStack mirrorStack;

    public CombatGearRenderer() {
    }

    @Override
    public void renderByItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType, MatrixStack stack, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        if (swordStack == null){
            swordStack = new ItemStack(ItemsRegistry.ENCHANTERS_SWORD);

            mirrorStack = new ItemStack(ItemsRegistry.ENCHANTERS_MIRROR);
        }
        //Grab the cap, so we can get the combat gear's mode
        CombatGearCap cap = CombatGearCap.getCap(itemStack);

        switch (cap.getSelectedItem()){
            //Sword
            default:
                ItemsRegistry.ENCHANTERS_SWORD.getItemStackTileEntityRenderer().
                        renderByItem(swordStack, transformType, stack, bufferIn, combinedLightIn, combinedOverlayIn);
                break;
                case 1:
                    modSpellBow.
                        renderByItem(itemStack, transformType, stack, bufferIn, combinedLightIn, combinedOverlayIn);
                break;
                case 2:
                ItemsRegistry.ENCHANTERS_MIRROR.getItemStackTileEntityRenderer().
                        renderByItem(mirrorStack, transformType, stack, bufferIn, combinedLightIn, combinedOverlayIn);
                break;
        }

    }
}
