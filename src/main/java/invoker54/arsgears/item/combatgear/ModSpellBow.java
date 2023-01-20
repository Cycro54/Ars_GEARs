package invoker54.arsgears.item.combatgear;

import com.hollingsworth.arsnouveau.api.item.ICasterTool;
import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.entity.EntitySpellArrow;
import com.hollingsworth.arsnouveau.common.items.SpellArrow;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.capability.player.PlayerDataCap;
import invoker54.arsgears.client.render.item.modSpellBowRenderer;
import invoker54.arsgears.init.SoundsInit;
import invoker54.arsgears.item.GearUpgrades;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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
import static invoker54.arsgears.item.combatgear.CombatGearItem.COMBAT_GEAR;

public class ModSpellBow extends BowItem implements IAnimatable, ICasterTool, IScribeable {
    private static final Logger LOGGER = LogManager.getLogger();
    public AnimationFactory factory = new AnimationFactory(this);

    public ModSpellBow(IItemTier tier) {
        super(new Item.Properties().durability(tier.getUses()).setISTER(() -> {
            return modSpellBowRenderer::new;
        }));
    }

    @Override
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        if (!player.level.isClientSide) return;
        LOGGER.debug("ON USE TIME IS: " + count);
        LOGGER.debug("TIME NEEDED IS: " + getChargeDuration(stack));
        if ((stack.getUseDuration() - count) == ((int)getChargeDuration(stack))){
            player.playSound(SoundEvents.STONE_BUTTON_CLICK_ON, 1.0F, 1.0F);
        }
        super.onUsingTick(stack, player, count);
    }

    @Override
    public boolean isFoil(ItemStack gearStack) {
        CombatGearCap gearCap = CombatGearCap.getCap(gearStack);

        return gearCap.getActivated();
    }
    @Override
    public boolean isValidRepairItem(ItemStack p_82789_1_, ItemStack p_82789_2_) {
        return false;
    }
    @Override
    public boolean isRepairable(ItemStack p_isRepairable_1_) {
        return false;
    }
    @Override
    public boolean isEnchantable(ItemStack gearStack) {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack gearStack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!CombatGearItem.checkInvTick(gearStack, worldIn, entityIn, itemSlot, isSelected)) return;

        super.inventoryTick(gearStack, worldIn, entityIn, itemSlot, isSelected);
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack gearStack = playerIn.getItemInHand(handIn);

        if(worldIn.isClientSide){
            return ActionResult.fail(gearStack);
        }

        playerIn.startUsingItem(handIn);
        return ActionResult.consume(gearStack);
        //return super.use(worldIn, playerIn, handIn);
    }

    public static float getChargeDuration(ItemStack gearStack){
        //The base duration is 2 seconds (which is 40 ticks)
        float duration = 40;

        int upgradeLvl = GearUpgrades.getUpgrade(gearStack, GearUpgrades.bowSpeed);
        if (upgradeLvl == 0) return duration;

        float newTime;

        switch (upgradeLvl){
            //This is 1
            default:
                newTime = 1.5f;
                break;
            case 2:
                newTime = 1.1f;
                break;
            case 3:
                newTime = 0.6f;
                break;
        }

        return newTime * 20f;
    }

    public static float getPowerForTime(int timeUsed, ItemStack gearStack) {
        float f = (float)timeUsed / getChargeDuration(gearStack);
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
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

        if (worldIn.isClientSide()) return;
        boolean fireSpell = false;
        CombatGearCap cap = CombatGearCap.getCap(gearStack);
        if (cap.getActivated()) {
            cap.setActivated(false, playerentity);
            fireSpell = true;
        }

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

        float f = getPowerForTime(useTime, gearStack);
        if ((double)f >= 0.1D) {
            boolean isArrowInfinite = playerentity.abilities.instabuild || (arrowStack.getItem() instanceof ArrowItem && ((ArrowItem) arrowStack.getItem()).isInfinite(arrowStack, gearStack, playerentity));
            if (!worldIn.isClientSide) {
                //Grab the spell (I added this)
                Spell spell = CombatGearItem.SpellM.getCurrentRecipe(gearStack);
                //Add the needed projectile cast method (if there is a spell)
                CompoundNBT itemTag = gearStack.getOrCreateTag();
                SpellResolver spellResolver = new SpellResolver((new SpellContext(spell, playerentity)).
                        withColors(getSpellColor(gearStack.getOrCreateTag(), getMode(gearStack.getOrCreateTag()))));

                //Grab ArrowItem instance
                ArrowItem arrowitem = (ArrowItem) (arrowStack.getItem() instanceof ArrowItem ? arrowStack.getItem() : Items.ARROW);
                //Convert it into an Abstract Arrow Entity
                AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(worldIn, arrowStack, playerentity);
                //Then finally return a new type of arrow with customArrow
                abstractarrowentity = customArrow(abstractarrowentity);

                List<AbstractArrowEntity> arrows = new ArrayList<>();
                boolean didCastSpell = false;

                //Now for the upgrades
                int spellArrowLvl = GearUpgrades.getUpgrade(gearStack, GearUpgrades.bowSpellArrow);

                if (fireSpell) {
                    //This sets the cooldown for the current spell
                    float cooldown = CombatGearItem.calcCooldown(cap.getSelectedItem(), spellResolver.spell, true) + playerIn.level.getGameTime();
                    CombatGearItem.setCooldown(itemTag, SpellBook.getMode(itemTag), cooldown);

                    //arrowItem is an arrow, spell isnt null, and the player can cast a spell
                    if (arrowitem == Items.ARROW && !spell.isEmpty() && spellResolver.withSilent(true).canCast(playerentity)) {
                        abstractarrowentity = buildSpellArrow(worldIn, playerentity, spellResolver, fireSpell);
                        spellResolver.expendMana(playerentity);
                        didCastSpell = true;
                    }
                    // I don't think I'll be using spell arrows (Spell arrows are fake arrows)
                    else if (arrowitem instanceof SpellArrow) {
                        if (spell.isEmpty() || !(spellResolver.canCast(playerentity))) {
                            return;
                        } else if (spellResolver.canCast(playerentity)) {
                            spellResolver.expendMana(playerentity);
                            didCastSpell = true;
                        }
                    }

                    //Now play the cast sound
                    playerIn.level.playSound(null, playerIn.blockPosition(), SoundsInit.GEAR_CAST, playerIn.getSoundSource(), 1.3F, 0.8F + playerIn.getRandom().nextFloat() * 0.4F);
                }
                arrows.add(abstractarrowentity);
                //So if the player did manage to cast the spell, check if there is the splitAugment, if so, cast more arrows.
                //I'll keep the augment split for now, but later I might change the way it works
                if (!spell.isEmpty() && spell.isValid() && didCastSpell) {
                    int numSplits = spell.getBuffsAtIndex(0, playerentity, AugmentSplit.class);
                    if (abstractarrowentity != null) {
                        //Changed this to use the new method
                        numSplits = ((EntitySpellArrow) abstractarrowentity).spellResolver.spell.getBuffsAtIndex(0, playerentity, AugmentSplit.INSTANCE);
                    }

                    // (abstractarrowentity instanceof EntitySpellArrow ? ((EntitySpellArrow) abstractarrowentity).spellResolver.spell.getBuffsAtIndex(0, AugmentSplit));

                    for (int i = 1; i < numSplits + 1; i++) {
                        Direction offset = playerentity.getDirection().getClockWise();
                        if (i % 2 == 0) offset = offset.getOpposite();
                        // Alternate sides
                        BlockPos projPos = playerentity.blockPosition().relative(offset, i);
                        projPos = projPos.offset(0, 1.5, 0);
                        EntitySpellArrow spellArrow = buildSpellArrow(worldIn, playerentity, spellResolver, fireSpell);
                        spellArrow.setPos(projPos.getX(), spellArrow.blockPosition().getY(), projPos.getZ());
                        arrows.add(spellArrow);
                    }
                }

                /* Finally, spawn all of those arrows */
                for (AbstractArrowEntity arr : arrows) {
                    //If the player has the spell arrow upgrade, and the bow is fully charged, make the arrow FAST
                    float velocity = (f * 3.0F);
                    float random = 1.0F;
                    LOGGER.debug("F EQUALS " + f);
                    LOGGER.debug("DOES PLAYER HAVE THE SPELL ARROW UPGRADE? " + (spellArrowLvl == 1));
                    LOGGER.debug("firespell is " + (fireSpell));
                    LOGGER.debug("IS THE PLAYER CROUCHING? " + (playerentity.isCrouching()));
                    if (f == 1.0F && spellArrowLvl == 1 && fireSpell && !playerentity.isCrouching()) {
                        arr.setNoGravity(true);
                        random = 0;
                        velocity *= 1.5f;
                    }
                    //f * 3.0F
                    arr.shootFromRotation(playerentity, playerentity.xRot, playerentity.yRot, 0, velocity, random);

                    if (f >= 1.0F) {
                        arr.setCritArrow(true);
                    }
                    addArrow(arr, gearStack, arrowStack, isArrowInfinite, playerentity);
                }

                //This will reduce the items durability by 1
                gearStack.hurtAndBreak(1, playerentity, (player) -> {
                    player.broadcastBreakEvent(playerentity.getUsedItemHand());
                });
            }

            worldIn.playSound(null, playerentity.getX(), playerentity.getY(), playerentity.getZ(), SoundEvents.ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (random.nextFloat() * 0.4F + 1.2F) + f * 0.5F);

            //Another thing from the bowItem class, shrinks the arrow stack by 1
            if (!isArrowInfinite && !playerentity.abilities.instabuild) {
                int arrowKeepLvl = GearUpgrades.getUpgrade(gearStack, GearUpgrades.bowArrowKeep);
                float chanceToKeep;
                switch (arrowKeepLvl) {
                    default:
                        chanceToKeep = 0;
                        break;
                    case 1:
                        chanceToKeep = 0.3F;
                        break;
                    case 2:
                        chanceToKeep = 0.5F;
                        break;
                }
                //Chance to keep falls as you grow in tier (every 2 tiers)
                chanceToKeep -= ((int) (cap.getTier().ordinal() / 2F) * 0.1f);
//                LOGGER.debug("WHATS MY CHANCE TO KEEP? " + chanceToKeep);
                float chanceToLose = (float) Math.random();
//                LOGGER.debug("WHATS MY CHANCE TO LOSE? " + chanceToLose);
//                LOGGER.debug("WILL I KEEP IT? " + (chanceToLose < chanceToKeep));
                arrowStack.shrink((chanceToLose < chanceToKeep) ? 0 : 1);
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
            spellArrow.setBaseDamage(0.0);
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
        if (!CombatGearItem.checkHoverText(gearStack, world, tooltip)) return;

        CombatGearCap cap = CombatGearCap.getCap(gearStack);
        CompoundNBT upgrades = GearUpgrades.getUpgrades(gearStack);

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

//        if (isArrowInfinite || playerentity.abilities.instabuild && (arrowStack.getItem() == Items.SPECTRAL_ARROW || arrowStack.getItem() == Items.TIPPED_ARROW)) {
//            abstractarrowentity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
//        }
        abstractarrowentity.pickup = AbstractArrowEntity.PickupStatus.CREATIVE_ONLY;
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

    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Nullable
    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        CompoundNBT cNBT = stack.getOrCreateTag();

        CompoundNBT capNBT = CombatGearCap.getCap(stack).serializeNBT();

        cNBT.put(COMBAT_GEAR, capNBT);
        return cNBT;
    }

    @Override
    public void readShareTag(ItemStack stack, @org.jetbrains.annotations.Nullable CompoundNBT nbt) {
        CombatGearCap.getCap(stack).deserializeNBT(nbt.getCompound(COMBAT_GEAR));
        super.readShareTag(stack, nbt);
    }

    @Override
    public UseAction getUseAnimation(ItemStack p_77661_1_) {
        return super.getUseAnimation(p_77661_1_);
    }

    @Mod.EventBusSubscriber(modid = ArsGears.MOD_ID)
    public static class BowUpgrades{

        //This is for bow speed
        @SubscribeEvent
        public static void onDraw(FOVUpdateEvent event) {
            PlayerEntity player = event.getEntity();
            ItemStack gearStack = player.getUseItem();

            if (!(gearStack.getItem() instanceof ModSpellBow)) return;

            float f = 1.0F;
            if (player.abilities.flying) {
                f *= 1.1F;
            }

            f = (float) ((double) f * ((player.getAttributeValue(Attributes.MOVEMENT_SPEED) / (double) player.abilities.getWalkingSpeed() + 1.0D) / 2.0D));
            if (player.abilities.getWalkingSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
                f = 1.0F;
            }

            int i = player.getTicksUsingItem();
            float f1 = (float) i / getChargeDuration(gearStack);
            if (f1 > 1.0F) {
                f1 = 1.0F;
            } else {
                f1 = f1 * f1;
            }

            f *= 1.0F - f1 * 0.15F;

            event.setNewfov(f);
        }

        //This is for cooldown reduction
        @SubscribeEvent
        public static void onArrowHit(LivingDamageEvent event){
//            LOGGER.warn("THIS IS FOR COOLDOWN REDUCTION");
//            LOGGER.debug("Was the event cancelled? " + (event.isCanceled()));
            if (event.isCanceled()) return;

            //Who damaged it.
//            LOGGER.debug("What's entity? " + event.getEntity());
//            LOGGER.debug("What's living entity? " + event.getEntityLiving());
//            LOGGER.debug("What's damage source entity? " + event.getSource().getEntity());
//            LOGGER.debug("What's damage source direct entity? " + event.getSource().getDirectEntity());
            Entity livingEntity = event.getSource().getEntity();
            if (!(livingEntity instanceof PlayerEntity)) return;
//            LOGGER.debug("USING SPELL ARROW? " + (event.getSource().getDirectEntity() instanceof ArrowEntity));
//            LOGGER.debug("WHATS THE ENTITY? " + (event.getSource().getDirectEntity().getClass()));
            if (!(event.getSource().getDirectEntity() instanceof ArrowEntity)) return;
            PlayerEntity player = (PlayerEntity) livingEntity;

            //Gear Capability
            ItemStack gearStack = PlayerDataCap.getCap(player).getCombatGear();
            CombatGearCap gearCap = CombatGearCap.getCap(gearStack);

            //Cooldown reduction
            int CDReduceLvl = GearUpgrades.getUpgrade(gearStack, GearUpgrades.bowCooldown);
//            LOGGER.debug("HAS COOLDOWN REDUCTION? " + (CDReduceLvl != 0));
            if (CDReduceLvl == 0) return;

            float currentTime = player.level.getGameTime();

            //For each gear cycle
            for (int a = 0; a < 3; a++){
//                LOGGER.debug("Whats the gear cycle? GEAR " + (a));
                CompoundNBT itemTag = gearCap.getTag(a);
                //For each recipe
                for (int b = 1; b < 4; b++){
//                    LOGGER.debug("Whats the spell? Spell " + (b));
                    float cooldown = CombatGearItem.getCooldown(player, itemTag, b, false);
//                    LOGGER.debug("Cooldown " + (cooldown));
//                    LOGGER.debug("Time " + (currentTime));
                    if (cooldown <= currentTime){
                        continue;
                    }
//                    LOGGER.debug("Difference " + ((cooldown - currentTime))/2F);
                    //Remove 3 seconds (so 60 ticks)
                    cooldown -= 60;
                    //Set the new cooldown
                    CombatGearItem.setCooldown(itemTag, b, cooldown);
                }
            }
        }

        //This is for Arrow spell split
//        @SubscribeEvent
//        public static void onArrowSpell(SpellResolveEvent.Post event){
//            LOGGER.warn("THIS IS FOR ARROW SPLIT");
//            if (event.isCanceled()) return;
//
//
//
////            //Who was damaged.
////            LivingEntity hitEntity = event.getEntityLiving();
////
////            //Who damaged it
////            if (!(event.getSource().getEntity() instanceof PlayerEntity)) return;
////            PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
////
////            //What damaged it
////            if (!(event.getSource().getDirectEntity() instanceof EntitySpellArrow)) return;
////            EntitySpellArrow spellArrow = (EntitySpellArrow) event.getSource().getDirectEntity();
////
////            //The spell cast
////            SpellResolver resolver = spellArrow.spellResolver;
////            if (resolver.spell.isEmpty()) return;
////            resolver.spell.setCost(0);
////
////            //Gear Capability
////            ItemStack gearStack = PlayerDataCap.getCap(player).getCombatGear();
////            CombatGearCap gearCap = CombatGearCap.getCap(gearStack);
////
////            int splitUpgrade = GearUpgrades.getUpgrade(bowInt, gearCap, GearUpgrades.bowSpellSplit);
////            LOGGER.debug("is split upgrade 0? " + (splitUpgrade == 0));
////            for (LivingEntity nextEntity : player.level.getEntitiesOfClass(LivingEntity.class, hitEntity.getBoundingBox().inflate(10.0D, 1.0D, 10.0D))) {
////                LOGGER.debug("split upgrade " + (splitUpgrade));
////                if (splitUpgrade == 0) break;
////                LOGGER.debug("player? " + (nextEntity == player));
////                if (nextEntity == player) continue;
////                LOGGER.debug("The entity hit? " + (nextEntity == hitEntity));
////                if (nextEntity == hitEntity) continue;
////                if (player.isAlliedTo(nextEntity)) continue;
////                if (nextEntity instanceof ArmorStandEntity && ((ArmorStandEntity) nextEntity).isMarker()) continue;
////
////
////                resolver.onCast(gearStack, nextEntity, player.level);
////
////                splitUpgrade--;
////            }
//
//        }
    }
}
