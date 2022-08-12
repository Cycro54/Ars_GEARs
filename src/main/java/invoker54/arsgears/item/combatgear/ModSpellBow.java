package invoker54.arsgears.item.combatgear;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.entity.EntitySpellArrow;
import com.hollingsworth.arsnouveau.common.items.SpellArrow;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.client.render.item.modSpellBowRenderer;
import invoker54.arsgears.item.GearUpgrades;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.hollingsworth.arsnouveau.common.items.SpellBook.getMode;
import static com.hollingsworth.arsnouveau.common.items.SpellBook.getSpellColor;

public class ModSpellBow extends BowItem implements IAnimatable, ICasterTool {
    private static final Logger LOGGER = LogManager.getLogger();
    public AnimationFactory factory = new AnimationFactory(this);

    public ModSpellBow(IItemTier tier) {
        super(new Item.Properties().durability(tier.getUses()).setISTER(() -> {
            return modSpellBowRenderer::new;
        }));
    }

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
                CompoundNBT itemTag = gearStack.getOrCreateTag();
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
                CombatGearCap cap = CombatGearCap.getCap(gearStack);
                if (cap.getActivated()) {
                    //Deactivate
                    cap.setActivated(false);
                    //This sets the cooldown for the current spell
                    float cooldown = CombatGearItem.calcCooldown(spellResolver.spell, true) + playerIn.level.getGameTime();
                    CombatGearItem.setCooldown(itemTag, SpellBook.getMode(itemTag), cooldown);

                    //arrowItem is an arrow, spell isnt null, and the player can cast a spell
                    if (arrowitem == Items.ARROW && !spell.isEmpty() && spellResolver.withSilent(true).canCast(playerentity)) {
                        abstractarrowentity = buildSpellArrow(worldIn, playerentity, spellResolver, cap.getActivated());
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
                        EntitySpellArrow spellArrow = buildSpellArrow(worldIn, playerentity, spellResolver, cap.getActivated());
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
    public EntitySpellArrow buildSpellArrow(World worldIn, PlayerEntity playerentity, SpellResolver spellResolver, boolean isSpellArrow) {
        EntitySpellArrow spellArrow = new EntitySpellArrow(worldIn, playerentity);
        spellArrow.spellResolver = spellResolver.withSilent(true);
        ParticleColor.IntWrapper color = spellResolver.spellContext.colors;
        spellArrow.setColors(color.r, color.g, color.b);

        //Arrows for spells will deal no damage
        if (isSpellArrow){
            spellArrow.setBaseDamage(0);
        }

        return spellArrow;
    }

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

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(final ItemStack gearStack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        CombatGearCap cap = CombatGearCap.getCap(gearStack);
        CompoundNBT upgrades = GearUpgrades.getUpgrades(CombatGearItem.bowInt, cap);

        if (upgrades.contains(GearUpgrades.bowSpeed))
            tooltip.add(GearUpgrades.getFullName(GearUpgrades.bowSpeed, upgrades));

        if (upgrades.contains(GearUpgrades.bowSpellArrow))
            tooltip.add(GearUpgrades.getFullName(GearUpgrades.bowSpellArrow, upgrades));

        if (upgrades.contains(GearUpgrades.bowArrowKeep))
            tooltip.add(GearUpgrades.getFullName(GearUpgrades.bowArrowKeep, upgrades));

        if (upgrades.contains(GearUpgrades.bowSpellSplit))
            tooltip.add(GearUpgrades.getFullName(GearUpgrades.bowSpellSplit, upgrades));
    }

    public boolean canPlayerCastSpell(ItemStack bow, PlayerEntity playerentity) {
        ISpellCaster caster = this.getSpellCaster(bow);
        return (new SpellResolver(new SpellContext(caster.getSpell(), playerentity))).withSilent(true).canCast(playerentity);
    }

    public void addArrow(AbstractArrowEntity abstractarrowentity, ItemStack bowStack, ItemStack arrowStack, boolean isArrowInfinite, PlayerEntity playerentity) {
        int power = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, bowStack);
        if (power > 0) {
            abstractarrowentity.setBaseDamage(abstractarrowentity.getBaseDamage() + (double)power * 0.5 + 0.5);
        }

        int punch = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, bowStack);
        if (punch > 0) {
            abstractarrowentity.setKnockback(punch);
        }

        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, bowStack) > 0) {
            abstractarrowentity.setSecondsOnFire(100);
        }

        if (isArrowInfinite || playerentity.abilities.instabuild && (arrowStack.getItem() == Items.SPECTRAL_ARROW || arrowStack.getItem() == Items.TIPPED_ARROW)) {
            abstractarrowentity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
        }

        playerentity.level.addFreshEntity(abstractarrowentity);
    }

    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY.or((i) -> {
            return i.getItem() instanceof SpellArrow;
        });
    }

    public void registerControllers(AnimationData data) {
    }

    public AbstractArrowEntity customArrow(AbstractArrowEntity arrow) {
        return super.customArrow(arrow);
    }

    public AnimationFactory getFactory() {
        return this.factory;
    }
    public boolean isScribedSpellValid(ISpellCaster caster, PlayerEntity player, Hand hand, ItemStack stack, Spell spell) {
        return spell.recipe.stream().noneMatch((s) -> {
            return s instanceof AbstractCastMethod;
        });
    }

    public void sendInvalidMessage(PlayerEntity player) {
        PortUtil.sendMessageNoSpam(player, new TranslationTextComponent("ars_nouveau.bow.invalid"));
    }

    public boolean setSpell(ISpellCaster caster, PlayerEntity player, Hand hand, ItemStack stack, Spell spell) {
        ArrayList<AbstractSpellPart> recipe = new ArrayList();
        recipe.add(MethodProjectile.INSTANCE);
        recipe.addAll(spell.recipe);
        spell.recipe = recipe;
        return false; //super.setSpell(caster, player, hand, stack, spell);
    }

    public int getEnchantmentValue() {
        return super.getEnchantmentValue();
    }

    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return true;
    }
}
