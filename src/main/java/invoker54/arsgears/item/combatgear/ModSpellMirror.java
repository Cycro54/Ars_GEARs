package invoker54.arsgears.item.combatgear;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.renderer.item.MirrorRenderer;
import com.hollingsworth.arsnouveau.common.items.EnchantersMirror;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.spell.method.MethodSelf;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.item.GearUpgrades;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

import static com.hollingsworth.arsnouveau.common.items.SpellBook.getMode;
import static com.hollingsworth.arsnouveau.common.items.SpellBook.getSpellColor;

public class ModSpellMirror extends EnchantersMirror {
    public ModSpellMirror(IItemTier tier) {
        super(new Properties().durability(tier.getUses()).setISTER(() -> MirrorRenderer::new));
    }

    @Override
    public void inventoryTick(ItemStack gearStack, World worldIn, Entity entity, int itemSlot, boolean isSelected) {
        PlayerEntity player = (PlayerEntity) entity;
        //Grabing spell code shtuff
        Spell spell = CombatGearItem.SpellM.getCurrentRecipe(gearStack);
        spell.recipe.add(0, MethodSelf.INSTANCE);
        SpellResolver resolver = new SpellResolver(new SpellContext(spell, player));

        //Grab the items tag
        CompoundNBT itemTag = gearStack.getOrCreateTag();

        //Get the cap
        CombatGearCap cap = CombatGearCap.getCap(gearStack);

        //Make sure the player can even cast the spell
        boolean flag = resolver.canCast(player);
        //This is if the item is still on cooldown
        boolean flag2 = CombatGearItem.getCooldown(player, itemTag, SpellBook.getMode(itemTag), true) <= 0;
        //This is if the spell has no glyphs after the Touch glyph
        boolean flag3 = resolver.spell.recipe.size() != 1;

        //If the player can afford the spell, AND the combat gear isn't activated, activate the combat gear
        if ((flag && flag2 && flag3) && !cap.getActivated()){
            cap.setActivated(true);
        }
        else if ((!flag || !flag2 || !flag3) && cap.getActivated()){
            cap.setActivated(false);
        }
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        //My new, terrible code. HAHAHAA!
        //First grab the itemstack
        ItemStack gearStack = playerIn.getItemInHand(handIn);
        CombatGearCap cap = CombatGearCap.getCap(gearStack);
        CompoundNBT itemTag = gearStack.getOrCreateTag();

        //Grabing spell code shtuff
        Spell spell = CombatGearItem.SpellM.getCurrentRecipe(gearStack);
        spell.recipe.add(0, MethodSelf.INSTANCE);
        //Get the spell resolver
        SpellResolver resolver = new SpellResolver((new SpellContext(spell, playerIn)).
                withColors(getSpellColor(gearStack.getOrCreateTag(), getMode(gearStack.getOrCreateTag()))));

        //For client side, and if the gearstack doesn't have a tag, AND if the gear isn't activated
        if (worldIn.isClientSide() || !gearStack.hasTag() || !cap.getActivated() || spell.recipe.size() == 1){
            if (!cap.getActivated()) PortUtil.sendMessageNoSpam(playerIn, new TranslationTextComponent("ars_nouveau.spell.no_mana"));
            if (spell.recipe.size() == 1) PortUtil.sendMessageNoSpam(playerIn, new TranslationTextComponent("ars_nouveau.spell.validation.exists.non_empty_spell"));
            return ActionResult.consume(gearStack);
        }

        //Now let's cast the spell on the player
        resolver.onCast(gearStack, playerIn, worldIn);
        //This sets the cooldown for the current spell
        float cooldown = CombatGearItem.calcCooldown(resolver.spell, true) + playerIn.level.getGameTime();
        CombatGearItem.setCooldown(itemTag, SpellBook.getMode(itemTag), cooldown);
        return ActionResult.success(gearStack);



        //Original code
//        ItemStack stack = playerIn.getItemInHand(handIn);
//        ISpellCaster caster = getSpellCaster(stack);
//        caster.getSpell().setCost((int) (caster.getSpell().getCastingCost() - caster.getSpell().getCastingCost() * 0.25));
//        return caster.castSpell(worldIn, playerIn, handIn, new TranslationTextComponent("ars_nouveau.mirror.invalid"));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(final ItemStack gearStack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        CombatGearCap cap = CombatGearCap.getCap(gearStack);
        CompoundNBT upgrades = GearUpgrades.getUpgrades(CombatGearItem.mirrorInt, cap);

        if (upgrades.contains(GearUpgrades.mirrorFreeGlyph))
            tooltip.add(GearUpgrades.getFullName(GearUpgrades.mirrorFreeGlyph, upgrades));

        if (upgrades.contains(GearUpgrades.mirrorManaDiscount))
            tooltip.add(GearUpgrades.getFullName(GearUpgrades.mirrorManaDiscount, upgrades));

        if (upgrades.contains(GearUpgrades.mirrorQuickCast))
            tooltip.add(GearUpgrades.getFullName(GearUpgrades.mirrorQuickCast, upgrades));
    }
}