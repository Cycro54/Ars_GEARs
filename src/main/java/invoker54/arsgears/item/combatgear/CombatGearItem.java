package invoker54.arsgears.item.combatgear;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.client.IDisplayMana;
import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.keybindings.ModKeyBindings;
import com.hollingsworth.arsnouveau.client.renderer.item.SpellBowRenderer;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOpenSpellBook;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.client.gui.CombatUpgradeScreen;
import invoker54.arsgears.client.gui.ModGuiSpellBook;
import invoker54.arsgears.client.render.CombatGearRenderer;
import invoker54.arsgears.item.GearTier;
import net.minecraft.block.BlockState;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;

import java.util.List;

import static com.hollingsworth.arsnouveau.common.items.SpellBook.*;

public class CombatGearItem extends ToolItem implements IScribeable, IDisplayMana, IAnimatable {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String COMBAT_GEAR = "COMBAT_GEAR";

    private final ModSwordItem modSword;
    private final ModBowItem modBow;
    private final ModMirrorItem modMirror;

    public CombatGearItem(IItemTier tier, Item.Properties builder) {
        super(0, 1, tier, null, builder.setISTER(() -> CombatGearRenderer::new));

        modSword = new ModSwordItem(tier);
        modBow = new ModBowItem();
        modMirror = new ModMirrorItem(builder);
    }

    @Override
    public boolean isFoil(ItemStack p_77636_1_) {
        return false;
    }

    @Override
    public GearTier getTier() {
        return (GearTier) super.getTier();
    }

    @Override
    public void inventoryTick(ItemStack gearStack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
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

        if (SpellBook.getMode(gearStack.getOrCreateTag()) == 0){SpellBook.setMode(gearStack.getOrCreateTag(),0);}

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
        ItemStack stack = playerIn.getItemInHand(handIn);
        CombatGearCap cap = CombatGearCap.getCap(stack);

        if(!stack.hasTag())
            return new ActionResult<>(ActionResultType.SUCCESS, stack);

        //Check if the spell slot is empty
//        boolean flag = getRecipeFromTag(stack.getOrCreateTag(), getMode(stack.getOrCreateTag())).isEmpty();
//        if(getTier().ordinal() > 0 && flag) {
//            return new ActionResult<>(ActionResultType.CONSUME, stack);
//        }

        if(worldIn.isClientSide || !stack.hasTag()){
            return new ActionResult<>(ActionResultType.CONSUME, stack);
        }
        // Crafting mode (you can no longer select crafting mode)
        // You will instead select a spell slot, and if that spell slot is empty the spell book will open
        /*if(getTier().ordinal() > 0 && getMode(stack.getOrCreateTag()) == 0 && playerIn instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) playerIn;
            Networking.INSTANCE.send(PacketDistributor.PLAYER.with(()->player), new PacketOpenSpellBook(stack.getTag(), getTier().ordinal(), getUnlockedSpellString(player.getItemInHand(handIn).getOrCreateTag())));
            return new ActionResult<>(ActionResultType.CONSUME, stack);
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
        LOGGER.info("Share tag shtuff");
        LOGGER.info("This is the new tag broz: " + CombatGearCap.getCap(stack).getActivated());
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
        //CombatGearCap cap = CombatGearCap.getCap(gearStack);
        int tier = getTier().ordinal();

        super.appendHoverText(gearStack, world, tooltip, flag);
        if(gearStack.hasTag()) {
            tooltip.add(new StringTextComponent(SpellBook.getSpellName(gearStack.getTag())));

            tooltip.add(new TranslationTextComponent("ars_nouveau.spell_book.select", KeyBinding.createNameSupplier(ModKeyBindings.OPEN_SPELL_SELECTION.getKeyBinding().getName()).get().getString()));
            tooltip.add(new TranslationTextComponent("ars_nouveau.spell_book.craft", KeyBinding.createNameSupplier(ModKeyBindings.OPEN_BOOK.getKeyBinding().getName()).get().getString()));
        }
        tooltip.add(new TranslationTextComponent("tooltip.ars_nouveau.caster_level", tier + 1).setStyle(Style.EMPTY.withColor(TextFormatting.BLUE)));
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
