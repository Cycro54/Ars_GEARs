package invoker54.arsgears.item.combatgear;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.client.keybindings.ModKeyBindings;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.item.GearTier;
import invoker54.arsgears.item.GearUpgrades;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

import java.util.List;

import static com.hollingsworth.arsnouveau.common.items.SpellBook.*;

public class CombatGearItem extends Item {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String COMBAT_GEAR = "COMBAT_GEAR";
    public static final String COOLDOWN = "_COOLDOWNS";
    public static int swordINT = 0;
    public static int bowInt = 1;
    public static int mirrorInt = 2;

    public CombatGearItem(Item.Properties builder) {
        super(builder);
    }
    public static boolean checkInvTick(ItemStack gearStack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isClientSide()) return false;

        PlayerEntity player = (PlayerEntity) entityIn;
        CombatGearCap cap = CombatGearCap.getCap(gearStack);

        //If the player goes off the weapon, turn off the spell
        if (!isSelected && cap.getActivated()) cap.setActivated(false);

        if (ArsUtil.getHeldGearCap(player, false).isEmpty()) return false;

        //If it's set to the crafting mode, set it to the 1st spell slot instead.
        if (SpellBook.getMode(gearStack.getOrCreateTag()) == 0){SpellBook.setMode(gearStack.getOrCreateTag(),0);}

        //If the item tier isn't high enough and the item is somehow activated, deactivate it.
        if (cap.getTier().ordinal() <= 1 && cap.getActivated()) cap.setActivated(false);

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean checkHoverText(final ItemStack gearStack, @Nullable final World world, List<ITextComponent> toolTip) {
        if (world == null) return false;

        CombatGearCap cap = CombatGearCap.getCap(gearStack);
        GearTier tier = cap.getTier();

        if (tier.ordinal() < GearTier.IRON.ordinal()) return false;
        if (GearUpgrades.getUpgrades(cap.getSelectedItem(), cap).size() == 0) return false;

        getHoverText(gearStack, toolTip);
        return true;
    }
    @OnlyIn(Dist.CLIENT)
    public static List<ITextComponent> getHoverText(final ItemStack gearStack, final List<ITextComponent> tooltip) {
        CombatGearCap cap = CombatGearCap.getCap(gearStack);

        if(gearStack.hasTag()) {
            tooltip.add(new StringTextComponent(SpellBook.getSpellName(gearStack.getTag())));

            tooltip.add(new TranslationTextComponent("ars_nouveau.spell_book.select", KeyBinding.createNameSupplier(ModKeyBindings.OPEN_SPELL_SELECTION.getKeyBinding().getName()).get().getString()));
            tooltip.add(new TranslationTextComponent("ars_nouveau.spell_book.craft", KeyBinding.createNameSupplier(ModKeyBindings.OPEN_BOOK.getKeyBinding().getName()).get().getString()));
        }
        tooltip.add(new TranslationTextComponent("tooltip.ars_nouveau.caster_level", cap.getTier().ordinal() - 1).setStyle(Style.EMPTY.withColor(TextFormatting.BLUE)));

        return tooltip;
    }

    public static float calcCooldown(Spell spell, boolean inTicks){
        //Every 50 mana will increase the spell cooldown by 1 second
        return spell.getCastingCost()/50f * (inTicks ? 20 : 1);
    }
    public static float getCooldown(PlayerEntity playerEntity, CompoundNBT tag, int spellMode, boolean getDifference){
        if (!tag.contains(COMBAT_GEAR + COOLDOWN)) tag.put(COMBAT_GEAR + COOLDOWN, new CompoundNBT());

        CompoundNBT cooldownNBT = tag.getCompound(COMBAT_GEAR + COOLDOWN);
        float endTime = cooldownNBT.getInt("" + (spellMode));
        return getDifference ? endTime - playerEntity.level.getGameTime() : endTime;
    }
    public static void setCooldown(CompoundNBT tag, int spellMode, float endTime){
        if (!tag.contains(COMBAT_GEAR + COOLDOWN)) tag.put(COMBAT_GEAR + COOLDOWN, new CompoundNBT());

        CompoundNBT cooldownNBT = tag.getCompound(COMBAT_GEAR + COOLDOWN);
        cooldownNBT.putFloat(("" + spellMode), endTime);
    }

    //These are SpellBook methods that should've been static
    public static class SpellM {
        public static Spell getCurrentRecipe(ItemStack stack){
            return SpellBook.getRecipeFromTag(stack.getTag(), getMode(stack.getTag()));
        }

        public static int getInitialCost(Spell spell) {
            int cost = 0;
            if (spell.recipe != null) {
                for (int i = 0; i < spell.recipe.size(); ++i) {
                    AbstractSpellPart spellPiece = (AbstractSpellPart) spell.recipe.get(i);
                    if (!(spellPiece instanceof AbstractAugment)) {
                        List<AbstractAugment> augments = spell.getAugments(i, (LivingEntity) null);
                        cost += spellPiece.getAdjustedManaCost(augments);
                    }
                }
            }
            return cost;
        }
    }
}
