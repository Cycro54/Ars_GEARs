package invoker54.arsgears.item.combatgear;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.MathUtil;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.client.render.item.modSwordRenderer;
import invoker54.arsgears.item.GearUpgrades;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.List;

import static com.hollingsworth.arsnouveau.common.items.SpellBook.getMode;
import static com.hollingsworth.arsnouveau.common.items.SpellBook.getSpellColor;

public class ModSpellSword extends SwordItem implements IAnimatable {
    private static final Logger LOGGER = LogManager.getLogger();
    public ModSpellSword(IItemTier iItemTier) {
        super(iItemTier, 3, -2.4f, new Properties().setISTER(() -> modSwordRenderer::new));
    }

    @Override
    public void inventoryTick(ItemStack gearStack, World worldIn, Entity entity, int itemSlot, boolean isSelected) {
        PlayerEntity player = (PlayerEntity) entity;
        //Grabing spell code shtuff
        Spell spell = CombatGearItem.SpellM.getCurrentRecipe(gearStack);
        spell.recipe.add(0, MethodTouch.INSTANCE);
        SpellResolver resolver = new SpellResolver(new SpellContext(spell, player));
        CompoundNBT itemTag = gearStack.getOrCreateTag();

        //Get the cap
        CombatGearCap cap = CombatGearCap.getCap(gearStack);

        boolean flag1 = resolver.withSilent(true).canCast(player);
        //This is if the item is still on cooldown
        boolean flag2 = CombatGearItem.getCooldown(player, itemTag, SpellBook.getMode(itemTag), true) <= 0;
        //This is if the spell has no glyphs after the Touch glyph
        boolean flag3 = resolver.spell.recipe.size() != 1;

        //If the player can't afford the spell, AND the combat gear is activated, set its activation to false
        if ((!flag1 || !flag2 || !flag3) && cap.getActivated()){
            cap.setActivated(false);
        }

        //This is for the sweep
        cap.isSweep = false;
    }

    //When the player right clicks with the item (I can also use this to check if they click on an entity)
    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        //First grab the itemstack
        ItemStack gearStack = playerIn.getItemInHand(handIn);

        //Next, Grab its capability
        CombatGearCap cap = CombatGearCap.getCap(gearStack);

        //Finally grab the itemStack tag
        CompoundNBT itemTag = gearStack.getOrCreateTag();

        LOGGER.debug("IS MY CAP ACTIVATED? " + cap.getActivated());


        //get the spell stuff set up
        Spell spell = CombatGearItem.SpellM.getCurrentRecipe(gearStack);
        spell.recipe.add(0, MethodTouch.INSTANCE);
        //Get the spell resolver
        SpellResolver resolver = new SpellResolver((new SpellContext(spell, playerIn)).
                withColors(getSpellColor(itemTag, getMode(itemTag))));

        boolean canCast1 = resolver.canCast(playerIn);
        boolean canCast2 = spell.recipe.size() > 1;
        boolean canCast3 = CombatGearItem.getCooldown(playerIn, itemTag, SpellBook.getMode(itemTag), true) <= 0;

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
                //This damages the gear stack
                gearStack.setDamageValue(gearStack.getDamageValue() + 1);
                //This sets the cooldown for the current spell
                float cooldown = CombatGearItem.calcCooldown(resolver.spell, true) + playerIn.level.getGameTime();
                CombatGearItem.setCooldown(itemTag, SpellBook.getMode(itemTag), cooldown);
                return ActionResult.success(gearStack);
            }
            //endregion

            //next block ray trace
            RayTraceResult blockResult = playerIn.pick(5.0, 0.0F, isSensitive);
            if(blockResult.getType() == RayTraceResult.Type.BLOCK || (isSensitive && blockResult instanceof BlockRayTraceResult)){
                ItemUseContext context = new ItemUseContext(playerIn, handIn, (BlockRayTraceResult) blockResult);
                resolver.onCastOnBlock(context);
                cap.setActivated(false);
                //This damages the gear stack
                gearStack.setDamageValue(gearStack.getDamageValue() + 1);
                //This sets the cooldown for the current spell
                float cooldown = CombatGearItem.calcCooldown(resolver.spell, true) + playerIn.level.getGameTime();
                CombatGearItem.setCooldown(itemTag, SpellBook.getMode(itemTag), cooldown);
                return ActionResult.success(gearStack);
            }
            //endregion
        }

        //This will stop the player from activating the sword if they aren't high enough level
        if (((CombatGearItem)gearStack.getItem()).getTier().ordinal() <= 1) return ActionResult.fail(gearStack);

        //If they can't cast in the first place, don't allow them to activate the item
        if (!cap.getActivated() && (!canCast1 || !canCast2 || !canCast3)){
            if (!canCast2) PortUtil.sendMessageNoSpam(playerIn, new TranslationTextComponent("ars_nouveau.spell.validation.exists.non_empty_spell"));
            if (!canCast3) PortUtil.sendMessageNoSpam(playerIn, new TranslationTextComponent("ars_gears.chat.cast_cooldown"));
            return ActionResult.consume(gearStack);
        }

        //Set it to whatever it wasnt
        cap.setActivated(!cap.getActivated());
        return cap.getActivated() ? ActionResult.fail(gearStack) : ActionResult.consume(gearStack);
    }

    @Override
    public boolean hurtEnemy(ItemStack gearStack, LivingEntity target, LivingEntity playerIn) {
        CombatGearCap cap = CombatGearCap.getCap(gearStack);
        CompoundNBT itemTag = gearStack.getOrCreateTag();

        //This is the spell sweep upgrade
        boolean hasSpellSweep = GearUpgrades.getUpgrade(cap.getSelectedItem(), cap, GearUpgrades.swordSpellSweep) != 0;

        //Only if the combat gear is set to active will the spell be cast.
        if (cap.getActivated() || (hasSpellSweep && cap.isSweep)) {
            cap.setActivated(false);
            Spell spell = CombatGearItem.SpellM.getCurrentRecipe(gearStack);

            //This is the spell sweep upgrade
            if (cap.isSweep) spell.setCost(0);

            spell.recipe.add(0, MethodTouch.INSTANCE);
            //Get the spell resolver
            SpellResolver resolver = new SpellResolver((new SpellContext(spell, playerIn)).
                    withColors(getSpellColor(itemTag, getMode(itemTag))));

            EntityRayTraceResult entityRes = new EntityRayTraceResult(target);
            resolver.onCastOnEntity(gearStack, playerIn, entityRes.getEntity(), Hand.MAIN_HAND);
            //This sets the cooldown for the current spell
            float cooldown = CombatGearItem.calcCooldown(resolver.spell, true) + playerIn.level.getGameTime();
            CombatGearItem.setCooldown(itemTag, SpellBook.getMode(itemTag), cooldown);
        }
        cap.isSweep = true;
        return super.hurtEnemy(gearStack, target, playerIn);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(final ItemStack gearStack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
//        CombatGearCap cap = CombatGearCap.getCap(gearStack);
//        CompoundNBT upgrades = GearUpgrades.getUpgrades(CombatGearItem.swordINT, cap);
//
//        if (upgrades.contains(GearUpgrades.swordManaSteal))
//            tooltip.add(GearUpgrades.getFullName(GearUpgrades.swordManaSteal, upgrades));
//
//        if (upgrades.contains(GearUpgrades.swordSpellSweep))
//            tooltip.add(GearUpgrades.getFullName(GearUpgrades.swordSpellSweep, upgrades));
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