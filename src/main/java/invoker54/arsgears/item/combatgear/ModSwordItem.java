package invoker54.arsgears.item.combatgear;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.MathUtil;
import com.hollingsworth.arsnouveau.client.renderer.item.SwordRenderer;
import com.hollingsworth.arsnouveau.common.items.EnchantersSword;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.ArrayList;

import static com.hollingsworth.arsnouveau.common.items.SpellBook.getMode;
import static com.hollingsworth.arsnouveau.common.items.SpellBook.getSpellColor;
import static com.hollingsworth.arsnouveau.setup.ItemsRegistry.defaultItemProperties;

public class ModSwordItem extends SwordItem implements IAnimatable {
    private static final Logger LOGGER = LogManager.getLogger();
    public ModSwordItem(IItemTier iItemTier) {
        super(iItemTier, 3, -2.4f, defaultItemProperties().stacksTo(1).setISTER(() -> SwordRenderer::new));
    }

    @Override
    public void inventoryTick(ItemStack gearStack, World worldIn, Entity entity, int itemSlot, boolean isSelected) {
        PlayerEntity player = (PlayerEntity) entity;
        //Grabing spell code shtuff
        Spell spell = CombatGearItem.SpellM.getCurrentRecipe(gearStack);
        spell.recipe.add(0, MethodTouch.INSTANCE);
        SpellResolver resolver = new SpellResolver(new SpellContext(spell, player));

        //Get the cap
        CombatGearCap cap = CombatGearCap.getCap(gearStack);

        boolean flag = resolver.withSilent(true).canCast(player);

        //This is if the spell has no glyphs after the Touch glyph
        if (resolver.spell.recipe.size() == 1) flag = false;

        //If the player can't afford the spell, AND the combat gear is activated, set its activation to false
        if (!flag && cap.getActivated()){
            cap.setActivated(false);
        }
    }

    //When the player right clicks with the item (I can also use this to check if they click on an entity)
    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        //First grab the itemstack
        ItemStack gearStack = playerIn.getItemInHand(handIn);

        //Next, Grab its capability
        CombatGearCap cap = CombatGearCap.getCap(gearStack);

        LOGGER.debug("IS MY CAP ACTIVATED? " + cap.getActivated());


        //get the spell stuff set up
        Spell spell = CombatGearItem.SpellM.getCurrentRecipe(gearStack);
        spell.recipe.add(0, MethodTouch.INSTANCE);
        //Get the spell resolver
        SpellResolver resolver = new SpellResolver((new SpellContext(spell, playerIn)).
                withColors(getSpellColor(gearStack.getOrCreateTag(), getMode(gearStack.getOrCreateTag()))));

        boolean canCast1 = resolver.canCast(playerIn);
        boolean canCast2 = spell.recipe.size() > 1;

        //Now I need to find out if it's activated
        if(cap.getActivated()) {
            //First I must grab the block or entity that I'm looking at
            //In order to do that I must get the isSensitive boolean (all of this is modified code from the SpellBook class in Ars Nouveau)
//            ManaCapability.getMana(playerIn).ifPresent((iMana) -> {
//                //What this does is check if the players spell tier is lower than this items tier,
//                //If it is, this will make the players spell tier go up to this items tier
//                //That would be unnecesary though, since the players spell tier will instead be based on combatGear's tier
//                if (iMana.getBookTier() < this.tier.ordinal()) {
//                    iMana.setBookTier(this.tier.ordinal());
//                }
//
//                //Glyph bonus is for mana, I don't want mana to be based on how many glyphs you have though.
//                if (iMana.getGlyphBonus() < getUnlockedSpells(stack.getTag()).size()) {
//                    iMana.setGlyphBonus(getUnlockedSpells(stack.getTag()).size());
//                }
//
//            });
            //Get the current spell
            //Get the spell
            boolean isSensitive = resolver.spell.getBuffsAtIndex(0, playerIn, AugmentSensitive.INSTANCE) > 0;

            //if the item doesn't have spell tags, or this is running on the client (CombatGearItem handles this already)
//            if(worldIn.isClientSide || !gearStack.hasTag()){
//                return new ActionResult<>(ActionResultType.CONSUME, gearStack);
//            }

            //region entity raytrace first
            EntityRayTraceResult entityRes = MathUtil.getLookedAtEntity(playerIn, 25);
            if(entityRes != null && entityRes.getEntity() instanceof LivingEntity){
                resolver.onCastOnEntity(gearStack, playerIn, entityRes.getEntity(), handIn);
                cap.setActivated(false);
                gearStack.setDamageValue(gearStack.getDamageValue() + 1);
                return ActionResult.success(gearStack);
            }
            //endregion

            //next block ray trace
            RayTraceResult blockResult = playerIn.pick(5.0, 0.0F, isSensitive);
            if(blockResult.getType() == RayTraceResult.Type.BLOCK || (isSensitive && blockResult instanceof BlockRayTraceResult)){
                ItemUseContext context = new ItemUseContext(playerIn, handIn, (BlockRayTraceResult) blockResult);
                resolver.onCastOnBlock(context);
                cap.setActivated(false);
                gearStack.setDamageValue(gearStack.getDamageValue() + 1);
                return ActionResult.success(gearStack);
            }
            //endregion
        }

        //This will stop the player from activating the sword if they aren't high enough level
        if (((CombatGearItem)gearStack.getItem()).getTier().ordinal() <= 1) return ActionResult.fail(gearStack);

        //If they can't cast in the first place, don't allow them to activate the item
        if (!cap.getActivated() && (!canCast1 || !canCast2)){
            if (!canCast2) PortUtil.sendMessageNoSpam(playerIn, new TranslationTextComponent("ars_nouveau.spell.validation.exists.non_empty_spell"));
            return ActionResult.consume(gearStack);
        }

        //Set it to whatever it wasnt
        cap.setActivated(!cap.getActivated());
        return cap.getActivated() ? ActionResult.fail(gearStack) : ActionResult.consume(gearStack);
    }

    @Override
    public boolean hurtEnemy(ItemStack gearStack, LivingEntity target, LivingEntity playerIn) {
        CombatGearCap cap = CombatGearCap.getCap(gearStack);

        //Only if the combat gear is set to active will the spell be cast.
        if (cap.getActivated()) {
            cap.setActivated(false);
            Spell spell = CombatGearItem.SpellM.getCurrentRecipe(gearStack);
            spell.recipe.add(0, MethodTouch.INSTANCE);
            //Get the spell resolver
            SpellResolver resolver = new SpellResolver((new SpellContext(spell, playerIn)).
                    withColors(getSpellColor(gearStack.getOrCreateTag(), getMode(gearStack.getOrCreateTag()))));

            EntityRayTraceResult entityRes = new EntityRayTraceResult(target);
            resolver.onCastOnEntity(gearStack, playerIn, entityRes.getEntity(), Hand.MAIN_HAND);
        }
        return super.hurtEnemy(gearStack, target, playerIn);
    }

    @Override
    public void registerControllers(AnimationData data) {

    }

    public AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}
