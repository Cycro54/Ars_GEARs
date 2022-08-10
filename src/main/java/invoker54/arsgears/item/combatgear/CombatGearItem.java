package invoker54.arsgears.item.combatgear;

import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.api.client.IDisplayMana;
import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.util.MathUtil;
import com.hollingsworth.arsnouveau.client.keybindings.ModKeyBindings;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.client.render.CombatGearRenderer;
import invoker54.arsgears.item.GearTier;
import invoker54.arsgears.item.GearUpgrades;
import invoker54.arsgears.network.NetworkHandler;
import invoker54.arsgears.network.message.OpenGearContainerMsg;
import net.minecraft.block.BlockState;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.hollingsworth.arsnouveau.common.items.SpellBook.*;

public class CombatGearItem extends ToolItem implements IScribeable, IDisplayMana, IAnimatable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String COMBAT_GEAR = "COMBAT_GEAR";

    public final ModSwordItem modSword;
    public final ModBowItem modBow;
    public final ModMirrorItem modMirror;
    public static int swordINT = 0;
    public static int bowInt = 1;
    public static int mirrorInt = 2;

    public CombatGearItem(IItemTier tier, Item.Properties builder) {
        super(0, -2.4f, tier, null, builder.setISTER(() -> CombatGearRenderer::new));

        modSword = new ModSwordItem(tier);
        modBow = new ModBowItem();
        modMirror = new ModMirrorItem(builder);
    }

    /**
     * This is for Attribute modification, but the only thing I really need to change is the attack damage. So I'll just do that in a damage event
     * I might need this later so I'll keep it for now
     * @param
     * @return
     */
//    @Override
//    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
//        final Multimap<Attribute, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);
//        CombatGearCap cap = CombatGearCap.getCap(stack);
//
//        double newDamage = 0;
//        double newSpeed = 0;
//
//        switch (cap.getSelectedItem()){
//            default:
//                newDamage = modSword.getAttributeModifiers(slot, stack).get(Attributes.ATTACK_DAMAGE).stream().findFirst().get().getAmount();
//                newSpeed = modSword.getAttributeModifiers(slot, stack).get(Attributes.ATTACK_SPEED).stream().findFirst().get().getAmount();
//                break;
//            case 1:
//                newDamage = modBow.getAttributeModifiers(slot, stack).get(Attributes.ATTACK_DAMAGE).stream().findFirst().get().getAmount();
//                newSpeed = modBow.getAttributeModifiers(slot, stack).get(Attributes.ATTACK_SPEED).stream().findFirst().get().getAmount();
//                break;
//            case 2:
//                newDamage = modMirror.getAttributeModifiers(slot, stack).get(Attributes.ATTACK_DAMAGE).stream().findFirst().get().getAmount();
//                newSpeed = modMirror.getAttributeModifiers(slot, stack).get(Attributes.ATTACK_SPEED).stream().findFirst().get().getAmount();
//                break;
//        }
//
//        if (slot == EquipmentSlotType.MAINHAND) {
//            replaceModifier(modifiers, Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE_UUID, newDamage);
//            replaceModifier(modifiers, Attributes.ATTACK_SPEED, BASE_ATTACK_SPEED_UUID, newSpeed);
//        }
//
//        return modifiers;
//    }
//
//    /**
//     * Replace a modifier in the {@link Multimap} with a copy that's had {@code newValue} applied to its value.
//     *
//     * @param modifierMultimap The MultiMap
//     * @param attribute        The attribute being modified
//     * @param id               The ID of the modifier
//     * @param newValue       The newValue to apply
//     */
//    private void replaceModifier(Multimap<Attribute, AttributeModifier> modifierMultimap, Attribute attribute, UUID id, double newValue) {
//        // Get the modifiers for the specified attribute
//        final Collection<AttributeModifier> modifiers = modifierMultimap.get(attribute);
//
//        // Find the modifier with the specified ID, if any
//        final Optional<AttributeModifier> modifierOptional = modifiers.stream().filter(attributeModifier -> attributeModifier.getId().equals(id)).findFirst();
//
//        if (modifierOptional.isPresent()) { // If it exists,
//            final AttributeModifier modifier = modifierOptional.get();
//            modifiers.remove(modifier); // Remove it
//            modifiers.add(new AttributeModifier(modifier.getId(), modifier.getName(), newValue, modifier.getOperation())); // Add the new modifier
//        }
//    }

    @Override
    public boolean isValidRepairItem(ItemStack p_82789_1_, ItemStack p_82789_2_) {
        return false;
    }
    @Override
    public boolean isRepairable(ItemStack p_isRepairable_1_) {
        return false;
    }
    @Override
    public boolean isEnchantable(ItemStack p_77616_1_) {
        return false;
    }


    //Until I can get the enchantment glint to work with custom renderers
    @Override
    public boolean isFoil(ItemStack gearStack) {
        CombatGearCap gearCap = CombatGearCap.getCap(gearStack);

        return gearCap.getActivated();
    }

    @Override
    public GearTier getTier() {
        return (GearTier) super.getTier();
    }

    @Override
    public void inventoryTick(ItemStack gearStack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        /**
         * This is old code from the Ars Nouveau SpellBookItem, no longer need dis though
         */
//        if(!worldIn.isClientSide && worldIn.getGameTime() % 5 == 0) {
//            CompoundNBT tag = stack.getOrCreateTag();
//            tag.putInt(SpellBook.BOOK_MODE_TAG, 0);
//            StringBuilder starting_spells = new StringBuilder();
//
//            if(stack.getItem() == ItemsRegistry.creativeSpellBook){
//                ArsNouveauAPI.getInstance().getSpell_map().values().forEach(s -> starting_spells.append(",").append(s.getTag().trim()));
//            }else{
//                ArsNouveauAPI.getInstance().getDefaultStartingSpells().forEach(s-> starting_spells.append(",").append(s.getTag().trim()));
//            }
//            tag.putString(SpellBook.UNLOCKED_SPELLS, starting_spells.toString());
//        }

        if (worldIn.isClientSide()) return;

        PlayerEntity player = (PlayerEntity) entityIn;

        if (ArsUtil.getHeldItem(player, CombatGearItem.class).isEmpty()) return;

        CombatGearCap cap = CombatGearCap.getCap(gearStack);

        //If it's set to the crafting mode, set it to the 1st spell slot instead.
        if (SpellBook.getMode(gearStack.getOrCreateTag()) == 0){SpellBook.setMode(gearStack.getOrCreateTag(),0);}

        //If the item tier isn't high enough and the item is somehow activated, deactivate it.
        if (getTier().ordinal() <= 1 && cap.getActivated()) cap.setActivated(false);

        switch (cap.getSelectedItem()){
            default:
                modSword.inventoryTick(gearStack, worldIn, entityIn, itemSlot, isSelected);
                break;
            case 1:
                modBow.inventoryTick(gearStack, worldIn, entityIn, itemSlot, isSelected);
                break;
            case 2:
                modMirror.inventoryTick(gearStack, worldIn, entityIn, itemSlot, isSelected);
                break;
        }
    }

    @Override
    public boolean onScribe(World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean hurtEnemy(ItemStack gearStack, LivingEntity target, LivingEntity playerIn) {
        CombatGearCap cap = CombatGearCap.getCap(gearStack);

        switch (cap.getSelectedItem()){
            default:
                return modSword.hurtEnemy(gearStack, target, playerIn);
            case 1:
                return modBow.hurtEnemy(gearStack, target, playerIn);
            case 2:
                return modMirror.hurtEnemy(gearStack, target, playerIn);
        }
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack gearStack = playerIn.getItemInHand(handIn);
        CombatGearCap cap = CombatGearCap.getCap(gearStack);

        //This is for opening the GEARs food menu if the player isn't looking at anything
        if (playerIn.isCrouching() && handIn == Hand.MAIN_HAND){
            //This is for checking if the player is looking at something
            EntityRayTraceResult entityRes = MathUtil.getLookedAtEntity(playerIn, 25);
            if(entityRes == null){
                if (worldIn.isClientSide()) NetworkHandler.INSTANCE.sendToServer(new OpenGearContainerMsg());
                return ActionResult.pass(playerIn.getItemInHand(handIn));
            }
        }

        if(!gearStack.hasTag())
            return new ActionResult<>(ActionResultType.SUCCESS, gearStack);

        //Check if the spell slot is empty
//        boolean flag = getRecipeFromTag(gearStack.getOrCreateTag(), getMode(gearStack.getOrCreateTag())).isEmpty();
//        if(getTier().ordinal() > 0 && flag) {
//            return new ActionResult<>(ActionResultType.CONSUME, gearStack);
//        }

        if(worldIn.isClientSide || !gearStack.hasTag()){
            return ActionResult.fail(gearStack);
        }
        // Crafting mode (you can no longer select crafting mode)
        // You will instead select a spell slot, and if that spell slot is empty the spell book will open
        /*if(getTier().ordinal() > 0 && getMode(gearStack.getOrCreateTag()) == 0 && playerIn instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) playerIn;
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(()->player), new PacketOpenSpellBook(gearStack.getTag(), getTier().ordinal(), getUnlockedSpellString(player.getItemInHand(handIn).getOrCreateTag())));
            return new ActionResult<>(ActionResultType.CONSUME, gearStack);
        } */

        //If not crafting mode, let's use the selected item instead.
        switch (cap.getSelectedItem()){
            default:
                return modSword.use(worldIn, playerIn, handIn);
            case 1:
                return modBow.use(worldIn, playerIn, handIn);
            case 2:
                return modMirror.use(worldIn, playerIn, handIn);
        }
    }

    @Override
    public void releaseUsing(ItemStack gearStack, World worldIn, LivingEntity playerIn, int useTime) {
        CombatGearCap cap = CombatGearCap.getCap(gearStack);

        LOGGER.debug("Choosing who will release");
        switch (cap.getSelectedItem()) {
            default:
                modSword.releaseUsing(gearStack, worldIn, playerIn, useTime);
                break;
            case 1:
                modBow.releaseUsing(gearStack, worldIn, playerIn, useTime);
                break;
            case 2:
                modMirror.releaseUsing(gearStack, worldIn, playerIn, useTime);
                break;
        }
    }

    @Override
    public int getUseDuration(ItemStack gearStack) {
        CombatGearCap cap = CombatGearCap.getCap(gearStack);

        switch (cap.getSelectedItem()){
            default:
                return modSword.getUseDuration(gearStack);
            case 1:
                return modBow.getUseDuration(gearStack);
            case 2:
                return modMirror.getUseDuration(gearStack);
        }
    }

    @Override
    public float getDestroySpeed(ItemStack gearStack, BlockState blockState) {
        CombatGearCap cap = CombatGearCap.getCap(gearStack);

        switch (cap.getSelectedItem()){
            default:
                return modSword.getDestroySpeed(gearStack, blockState);
            case 1:
                return modBow.getDestroySpeed(gearStack, blockState);
            case 2:
                return modMirror.getDestroySpeed(gearStack, blockState);
        }
    }

    @Override
    public UseAction getUseAnimation(ItemStack gearStack) {
        CombatGearCap cap = CombatGearCap.getCap(gearStack);

        switch (cap.getSelectedItem()){
            default:
                return modSword.getUseAnimation(gearStack);
            case 1:
                return modBow.getUseAnimation(gearStack);
            case 2:
                return modMirror.getUseAnimation(gearStack);
        }
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
    public void registerControllers(AnimationData data) {
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(final ItemStack gearStack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        if (world == null) return;

        CombatGearCap cap = CombatGearCap.getCap(gearStack);
        int tier = getTier().ordinal();

        if (tier + 1 < 3) return;

        if(gearStack.hasTag()) {
            tooltip.add(new StringTextComponent(SpellBook.getSpellName(gearStack.getTag())));

            tooltip.add(new TranslationTextComponent("ars_nouveau.spell_book.select", KeyBinding.createNameSupplier(ModKeyBindings.OPEN_SPELL_SELECTION.getKeyBinding().getName()).get().getString()));
            tooltip.add(new TranslationTextComponent("ars_nouveau.spell_book.craft", KeyBinding.createNameSupplier(ModKeyBindings.OPEN_BOOK.getKeyBinding().getName()).get().getString()));
        }
        tooltip.add(new TranslationTextComponent("tooltip.ars_nouveau.caster_level", tier - 1).setStyle(Style.EMPTY.withColor(TextFormatting.BLUE)));


        if (GearUpgrades.getUpgrades(cap.getSelectedItem(), cap).size() == 0) return;

        //Now for the special upgrades
        switch (cap.getSelectedItem()) {
            default:
                modSword.appendHoverText(gearStack, world, tooltip, flag);
                break;
            case 1:
                modBow.appendHoverText(gearStack, world, tooltip, flag);
                break;
            case 2:
                modMirror.appendHoverText(gearStack, world, tooltip, flag);
                break;
        }
    }

    public AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    //These are SpellBook methods that should've been static
    public static class SpellM {
        public static Spell getCurrentRecipe(ItemStack stack){
            return SpellBook.getRecipeFromTag(stack.getTag(), getMode(stack.getTag()));
        }
    }
}
