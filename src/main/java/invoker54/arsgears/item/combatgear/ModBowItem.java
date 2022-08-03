package invoker54.arsgears.item.combatgear;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.ISpellCaster;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.entity.EntitySpellArrow;
import com.hollingsworth.arsnouveau.common.items.SpellArrow;
import com.hollingsworth.arsnouveau.common.items.SpellBow;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.hollingsworth.arsnouveau.common.items.SpellBook.getMode;
import static com.hollingsworth.arsnouveau.common.items.SpellBook.getSpellColor;

public class ModBowItem extends SpellBow {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemStack = playerIn.getItemInHand(handIn);

        playerIn.startUsingItem(handIn);
        return ActionResult.consume(itemStack);
        //return super.use(worldIn, playerIn, handIn);
    }

    @Override
    public void releaseUsing(ItemStack gearStack, World worldIn, LivingEntity playerIn, int timeLeft) {
        //region This is stuff from the BowItem class
        if (!(playerIn instanceof PlayerEntity))
            return;
        PlayerEntity playerentity = (PlayerEntity)playerIn;
        boolean isInfinity = playerentity.abilities.instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, gearStack) > 0;
        ItemStack arrowStack = findAmmo(playerentity, gearStack);

        int useTime = this.getUseDuration(gearStack) - timeLeft;
        useTime = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(gearStack, worldIn, playerentity, useTime, !arrowStack.isEmpty() || isInfinity);
        if (useTime < 0) return;
        boolean canFire = false;
        if (!arrowStack.isEmpty() || isInfinity) {
            if (arrowStack.isEmpty()) {
                arrowStack = new ItemStack(Items.ARROW);
            }
            canFire = true;
            LOGGER.debug("Can I shoot? " + canFire);
        }
        //endregion

        //This is the added code from Ars Nouveau
        ISpellCaster caster = getSpellCaster(gearStack);
        //This makes it so if you have no arrows, you can still shoot a fake arrow, I don't want that.
//        boolean isSpellArrow = false;
//        if(arrowStack.isEmpty() && caster.getSpell() != null && new SpellResolver(new SpellContext(caster.getSpell(), playerentity)).canCast(playerentity)){
//            canFire = true;
//            isSpellArrow = true;
//        }

        if(!canFire)
            return;

        float f = getPowerForTime(useTime);
        if ((double)f >= 0.1D) {
            boolean isArrowInfinite = playerentity.abilities.instabuild || (arrowStack.getItem() instanceof ArrowItem && ((ArrowItem)arrowStack.getItem()).isInfinite(arrowStack, gearStack, playerentity));
            if (!worldIn.isClientSide) {
                //Grab the spell (I added this)
                Spell spell = CombatGearItem.SpellM.getCurrentRecipe(gearStack);
                //Add the needed projectile cast method (if there is a spell)
                if(!spell.isEmpty()) spell.recipe.add(0, MethodProjectile.INSTANCE);
                //LOGGER.warn("Is de spell empty? " + spell.isEmpty());
                //Make a spell resolver
                //SpellResolver spellResolver = new SpellResolver(new SpellContext(spell, playerentity));
                SpellResolver spellResolver = new SpellResolver((new SpellContext(spell, playerentity)).
                        withColors(getSpellColor(gearStack.getOrCreateTag(), getMode(gearStack.getOrCreateTag()))));

                //LOGGER.warn("CAN PLAYER CAST THIS SPELL ANYWHO? " + spellResolver.canCast(playerentity));

                //Grab ArrowItem instance
                ArrowItem arrowitem = (ArrowItem) (arrowStack.getItem() instanceof ArrowItem ? arrowStack.getItem() : Items.ARROW);
                //Convert it into an Abstract Arrow Entity
                AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(worldIn, arrowStack, playerentity);
                //Then finally return a new type of arrow with customArrow
                abstractarrowentity = customArrow(abstractarrowentity);

                List<AbstractArrowEntity> arrows = new ArrayList<>();
                boolean didCastSpell = false;
                //Only make the arrow cast a spell IF the cap.getActivated returns true
                if (CombatGearCap.getCap(gearStack).getActivated()) {
                    CombatGearCap.getCap(gearStack).setActivated(false);
                    //arrowItem is an arrow, spell isnt null, and the player can cast a spell
                    if (arrowitem == Items.ARROW && !spell.isEmpty() && spellResolver.withSilent(true).canCast(playerentity)) {
                        abstractarrowentity = buildSpellArrow(worldIn, playerentity, spellResolver);
                        spellResolver.expendMana(playerentity);
                        didCastSpell = true;
                    }
                    // I don't think I'll be using spell arrows (Spell arrows are fake arrows)
                    else if(arrowitem instanceof SpellArrow) {
                        if (spell.isEmpty() || !(spellResolver.canCast(playerentity))) {
                            return;
                        } else if (spellResolver.canCast(playerentity)) {
                            spellResolver.expendMana(playerentity);
                            didCastSpell = true;
                        }
                    }
                }
                arrows.add(abstractarrowentity);
                //So if the player did manage to cast the spell, check if there is the splitAugment, if so, cast more arrows.
                //I'll keep the augment split for now, but later I might change the way it works
                if(!spell.isEmpty() && spell.isValid() && didCastSpell){
                    int numSplits = spell.getBuffsAtIndex(0, playerentity, AugmentSplit.class);
                    if(abstractarrowentity != null){
                        //Changed this to use the new method
                        numSplits = ((EntitySpellArrow) abstractarrowentity).spellResolver.spell.getBuffsAtIndex(0, playerentity, AugmentSplit.INSTANCE);
                    }

                    // (abstractarrowentity instanceof EntitySpellArrow ? ((EntitySpellArrow) abstractarrowentity).spellResolver.spell.getBuffsAtIndex(0, AugmentSplit));

                    for(int i =1; i < numSplits + 1; i++){
                        Direction offset = playerentity.getDirection().getClockWise();
                        if(i%2==0) offset = offset.getOpposite();
                        // Alternate sides
                        BlockPos projPos = playerentity.blockPosition().relative(offset, i);
                        projPos = projPos.offset(0, 1.5, 0);
                        EntitySpellArrow spellArrow = buildSpellArrow(worldIn, playerentity, spellResolver);
                        spellArrow.setPos(projPos.getX(), spellArrow.blockPosition().getY(), projPos.getZ());
                        arrows.add(spellArrow);
                    }
                }

                /* Finally, spawn all of those arrows */
                for(AbstractArrowEntity arr : arrows){
                    arr.shootFromRotation(playerentity, playerentity.xRot, playerentity.yRot, 0.0F, f * 3.0F, 1.0F);
                    if (f >= 1.0F) {
                        arr.setCritArrow(true);
                    }
                    addArrow(arr, gearStack, arrowStack, isArrowInfinite, playerentity);
                }
            }

            worldIn.playSound(null, playerentity.getX(), playerentity.getY(), playerentity.getZ(), SoundEvents.ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

            //Another thing from the bowItem class, shrinks the arrow stack by 1
            if (!isArrowInfinite && !playerentity.abilities.instabuild) {
                arrowStack.shrink(1);
            }
        }
    }
    //Method from SpellBow class
    public EntitySpellArrow buildSpellArrow(World worldIn, PlayerEntity playerentity, SpellResolver spellResolver) {
        EntitySpellArrow spellArrow = new EntitySpellArrow(worldIn, playerentity);
        spellArrow.spellResolver = spellResolver.withSilent(true);
        ParticleColor.IntWrapper color = spellResolver.spellContext.colors;
        spellArrow.setColors(color.r, color.g, color.b);

        return spellArrow;
    }

    @Override
    public ItemStack findAmmo(PlayerEntity playerEntity, ItemStack shootable) {
            Predicate<ItemStack> predicate = (ItemsRegistry.SPELL_BOW).getSupportedHeldProjectiles()
                    .and(i -> !(i.getItem() instanceof SpellArrow) || (i.getItem() instanceof SpellArrow && canPlayerCastSpell(shootable, playerEntity)));
            ItemStack itemstack = ShootableItem.getHeldProjectile(playerEntity, predicate);
            if (!itemstack.isEmpty()) {
                return itemstack;
            } else {
                predicate = (ItemsRegistry.SPELL_BOW).getAllSupportedProjectiles().and(i -> !(i.getItem() instanceof SpellArrow) || (i.getItem() instanceof SpellArrow && canPlayerCastSpell(shootable, playerEntity)));

                for(int i = 0; i < playerEntity.inventory.getContainerSize(); ++i) {
                    ItemStack itemstack1 = playerEntity.inventory.getItem(i);
                    if (predicate.test(itemstack1)) {
                        return itemstack1;
                    }
                }

                return playerEntity.abilities.instabuild ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
            }
    }
}