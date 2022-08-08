package invoker54.arsgears.client.render.item;

import com.hollingsworth.arsnouveau.client.renderer.item.SwordModel;
import com.hollingsworth.arsnouveau.common.items.EnchantersSword;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class modSwordModel extends AnimatedGeoModel<CombatGearItem> {

    public ResourceLocation getModelLocation(CombatGearItem wand) {
        return new ResourceLocation("ars_nouveau", "geo/sword.geo.json");
    }

    public ResourceLocation getTextureLocation(CombatGearItem wand) {
        return new ResourceLocation("ars_nouveau", "textures/items/enchanters_sword.png");
    }

    public ResourceLocation getAnimationFileLocation(CombatGearItem wand) {
        return new ResourceLocation("ars_nouveau", "animations/sword.json");
    }
}
