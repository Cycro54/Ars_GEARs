package invoker54.arsgears.client.render.item;

import invoker54.arsgears.item.combatgear.ModSpellSword;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class modSwordModel extends AnimatedGeoModel<ModSpellSword> {

    public ResourceLocation getModelLocation(ModSpellSword wand) {
        return new ResourceLocation("ars_nouveau", "geo/sword.geo.json");
    }

    public ResourceLocation getTextureLocation(ModSpellSword wand) {
        return new ResourceLocation("ars_nouveau", "textures/items/enchanters_sword.png");
    }

    public ResourceLocation getAnimationFileLocation(ModSpellSword wand) {
        return new ResourceLocation("ars_nouveau", "animations/sword.json");
    }
}
