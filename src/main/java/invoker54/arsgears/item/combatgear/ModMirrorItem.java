package invoker54.arsgears.item.combatgear;

import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.items.EnchantersMirror;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import static com.hollingsworth.arsnouveau.common.items.SpellBook.getMode;
import static com.hollingsworth.arsnouveau.common.items.SpellBook.getSpellColor;

public class ModMirrorItem extends EnchantersMirror {
    public ModMirrorItem(Properties builder) {
        super(builder);
    }

    @Override
    public void inventoryTick(ItemStack gearStack, World worldIn, Entity entity, int itemSlot, boolean isSelected) {
        PlayerEntity player = (PlayerEntity) entity;
        //Grabing spell code shtuff
        Spell spell = CombatGearItem.SpellM.getCurrentRecipe(gearStack);
        spell.recipe.add(0, MethodSelf.INSTANCE);
        SpellResolver resolver = new SpellResolver(new SpellContext(spell, player));

        //Get the cap
        CombatGearCap cap = CombatGearCap.getCap(gearStack);

        //Make sure the player can even cast the spell
        boolean flag = resolver.canCast(player);

        //If the player can afford the spell, AND the combat gear isn't activated, activate the combat gear
        if (flag && !cap.getActivated()){
            cap.setActivated(true);
        }
        else if (!flag && cap.getActivated()){
            cap.setActivated(false);
        }
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        //My new, terrible code. HAHAHAA!
        //First grab the itemstack
        ItemStack gearStack = playerIn.getItemInHand(handIn);
        CombatGearCap cap = CombatGearCap.getCap(gearStack);

        //Grabing spell code shtuff
        Spell spell = CombatGearItem.SpellM.getCurrentRecipe(gearStack);
        spell.recipe.add(0, MethodSelf.INSTANCE);
        //Get the spell resolver
        SpellResolver resolver = new SpellResolver((new SpellContext(spell, playerIn)).
                withColors(getSpellColor(gearStack.getOrCreateTag(), getMode(gearStack.getOrCreateTag()))));

        //For client side, and if the gearstack doesn't have a tag, AND if the gear isn't activated
        if (worldIn.isClientSide() || !gearStack.hasTag() || !cap.getActivated()){
            if (!cap.getActivated()) {
                PortUtil.sendMessageNoSpam(playerIn, new TranslationTextComponent("ars_nouveau.spell.no_mana"));
            }
            return new ActionResult<>(ActionResultType.CONSUME, gearStack);
        }

        //Now let's cast the spell on the player
        resolver.onCast(gearStack, playerIn, worldIn);
        return new ActionResult<>(ActionResultType.CONSUME, gearStack);



        //Original code
//        ItemStack stack = playerIn.getItemInHand(handIn);
//        ISpellCaster caster = getSpellCaster(stack);
//        caster.getSpell().setCost((int) (caster.getSpell().getCastingCost() - caster.getSpell().getCastingCost() * 0.25));
//        return caster.castSpell(worldIn, playerIn, handIn, new TranslationTextComponent("ars_nouveau.mirror.invalid"));
    }
}
