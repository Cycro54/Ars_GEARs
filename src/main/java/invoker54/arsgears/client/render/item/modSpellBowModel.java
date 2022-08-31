package invoker54.arsgears.client.render.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import invoker54.arsgears.item.combatgear.ModSpellBow;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class modSpellBowModel extends AnimatedGeoModel<ModSpellBow> {
    @Override
    public ResourceLocation getModelLocation(ModSpellBow wand) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/spellbow.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(ModSpellBow wand) {
        return  new ResourceLocation(ArsNouveau.MODID, "textures/items/spellbow.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(ModSpellBow wand) {
        return new ResourceLocation(ArsNouveau.MODID , "animations/wand_animation.json");
    }
}
