package invoker54.arsgears.item.combatgear;

import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.client.keybindings.ModKeyBindings;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectBreak;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.config.ArsGearsConfig;
import invoker54.arsgears.item.GearTier;
import invoker54.arsgears.item.GearUpgrades;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

import static com.hollingsworth.arsnouveau.common.items.SpellBook.getMode;

public class CombatGearItem extends Item {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String COMBAT_GEAR = "COMBAT_GEAR";
    public static final String COOLDOWN = "_COOLDOWNS";
    public static int swordINT = 0;
    public static int bowInt = 1;
    public static int mirrorInt = 2;

    public CombatGearItem(GearTier tier, Item.Properties builder) {
        super(builder.durability(tier.getUses()));
    }
    public static boolean checkInvTick(ItemStack gearStack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (worldIn.isClientSide()) return false;

        PlayerEntity player = (PlayerEntity) entityIn;
        CombatGearCap cap = CombatGearCap.getCap(gearStack);

        //If the player goes off the weapon, turn off the spell
        if (!isSelected && cap.getActivated()) cap.setActivated(false);

        if (ArsUtil.getHeldGearCap(player, false, false).isEmpty()) return false;

        //If it's set to the crafting mode, set it to the 1st spell slot instead.
        if (SpellBook.getMode(gearStack.getOrCreateTag()) == 0){SpellBook.setMode(gearStack.getOrCreateTag(),0);}

        //If the item tier isn't high enough and the item is somehow activated, deactivate it.
        if (cap.getTier().ordinal() <= 1 && cap.getActivated()) cap.setActivated(false);

        //Make sure the player has all the glyph bonuses
        IMana mana = ManaCapability.getMana(player).resolve().get();
        int glyphCount = SpellBook.getUnlockedSpells(gearStack.getOrCreateTag()).size();
        if (glyphCount != mana.getGlyphBonus()) mana.setGlyphBonus(glyphCount);

        //Make sure the player isn't trying to use a banned glyph
        if (cap.getActivated()){
            int mode = SpellBook.getMode(gearStack.getOrCreateTag());
            Spell spell = SpellBook.getRecipeFromTag(gearStack.getOrCreateTag(), mode);

            for (AbstractSpellPart spellPart: spell.recipe){
                if (CombatGearItem.isBanned(spellPart, false)){
                    cap.setActivated(false);
                    String glyphName = spellPart.getLocaleName();
                    String reason = new TranslationTextComponent("ars_gears.chat.use_glyph_banned").getString();
                    player.sendMessage(new StringTextComponent(reason + glyphName), Util.NIL_UUID);
                    return false;
                }
            }
        }

        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean checkHoverText(final ItemStack gearStack, @Nullable final World world, List<ITextComponent> toolTip) {
        if (world == null) return false;

        CombatGearCap cap = CombatGearCap.getCap(gearStack);
        GearTier tier = cap.getTier();

        if (tier.ordinal() < GearTier.IRON.ordinal()) return false;

        getHoverText(gearStack, toolTip);

        return GearUpgrades.getUpgrades(gearStack).size() != 0;
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

    public static float calcCooldown(int gearCycle, Spell spell, boolean inTicks){
        //Every 40 mana will increase the spell cooldown by 1 second (augments will be every 100 mana)
        //Methods won't cost a thing.
        float coolDown = 0;
        for (AbstractSpellPart spellPart : spell.recipe){
            if (spellPart instanceof AbstractEffect){
                coolDown += spellPart.getManaCost()/40f;
            }
            else {
                coolDown += spellPart.getManaCost()/100F;
            }
        }

        //This will increase the cooldown (For the bow only)
        if (gearCycle == bowInt){
            coolDown += (coolDown * 0.6F);
        }

        //THIS IS THE COOLDOWN MULTIPLER
        if (ArsGearsConfig.coolDownMultiplier != 1F){
            coolDown *= ArsGearsConfig.coolDownMultiplier;
        }

        //THIS IS COOLDOWN VALUE CHANGE
        if (ArsGearsConfig.coolDownValueChange != 0){
            coolDown = (float) Math.max(0, coolDown + ArsGearsConfig.coolDownValueChange);
        }

        //Round to 2 decimal places
        coolDown = Math.round(coolDown * 100)/100F;

        return (coolDown * (inTicks ? 20 : 1));
    }
    public static float getCooldown(PlayerEntity playerEntity, CompoundNBT tag, int spellMode, boolean getDifference){
        if (!tag.contains(COMBAT_GEAR + COOLDOWN)) tag.put(COMBAT_GEAR + COOLDOWN, new CompoundNBT());

        if (playerEntity.abilities.instabuild || ArsGearsConfig.disableCooldown){
            return 0;
        }

        CompoundNBT cooldownNBT = tag.getCompound(COMBAT_GEAR + COOLDOWN);
        float endTime = cooldownNBT.getFloat("" + (spellMode));
        return getDifference ? endTime - playerEntity.level.getGameTime() : endTime;
    }
    public static void setCooldown(CompoundNBT tag, int spellMode, float endTime){
        if (!tag.contains(COMBAT_GEAR + COOLDOWN)) tag.put(COMBAT_GEAR + COOLDOWN, new CompoundNBT());

        CompoundNBT cooldownNBT = tag.getCompound(COMBAT_GEAR + COOLDOWN);
        cooldownNBT.putFloat(("" + spellMode), endTime);
    }

    public static boolean isBanned(AbstractSpellPart spellPart, boolean inSpellBook){
        if (spellPart instanceof EffectBreak) return true;

        if (spellPart instanceof AbstractCastMethod && inSpellBook) return true;

        return false;
    }

    //These are SpellBook methods that should've been static
    public static class SpellM {
        public static Spell getCurrentRecipe(ItemStack stack) {
            return SpellBook.getRecipeFromTag(stack.getTag(), getMode(stack.getTag()));
        }

        public static int getInitialCost(Spell spell, int gearCycle, ItemStack gearStack) {
            int cost = 0;
            if (spell.recipe != null) {
                for (int i = 0; i < spell.recipe.size(); ++i) {
                    AbstractSpellPart spellPiece = (AbstractSpellPart) spell.recipe.get(i);
                    if (!(spellPiece instanceof AbstractAugment)) {
                        List<AbstractAugment> augments = spell.getAugments(i, (LivingEntity) null);
                        cost += spellPiece.getAdjustedManaCost(augments);
                    }
                }

                //This will increase and decrease the mana cost (For the mirror only)
                if (gearCycle == mirrorInt) {
                    //First get the cost times 2
                    cost *= 2;

                    //Then the mana discount
                    int discountLvl = GearUpgrades.getUpgrade(gearStack, GearUpgrades.mirrorManaDiscount);
                    switch (discountLvl) {
                        default:
                            break;
                        case 1:
                            cost -= (cost * 0.15F);
                            break;
                        case 2:
                            cost -= (cost * 0.4F);
                            break;
                    }
                }
            }
            return cost;
        }

        public static boolean canCast(PlayerEntity player, Spell spell, CompoundNBT itemTag) {
            IMana cap = ManaCapability.getMana(player).resolve().get();
            int cost = spell.getCastingCost();
            boolean flag1 = (cap.getCurrentMana() >= cost);
            boolean flag2 = CombatGearItem.getCooldown(player, itemTag, SpellBook.getMode(itemTag), true) <= 0;

            return flag1 && flag2;
        }
    }
}
