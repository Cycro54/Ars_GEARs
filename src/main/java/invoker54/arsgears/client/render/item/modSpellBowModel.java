package invoker54.arsgears.client.render.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.SpellBow;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class modSpellBowModel extends AnimatedGeoModel<CombatGearItem> {
    @Override
    public ResourceLocation getModelLocation(CombatGearItem wand) {
        return new ResourceLocation(ArsNouveau.MODID , "geo/spellbow.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(CombatGearItem wand) {
        return  new ResourceLocation(ArsNouveau.MODID, "textures/items/spellbow.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(CombatGearItem wand) {
        return new ResourceLocation(ArsNouveau.MODID , "animations/wand_animation.json");
    }
}
