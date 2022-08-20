package invoker54.arsgears.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.player.PlayerDataCap;
import invoker54.arsgears.item.GearTier;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.hollingsworth.arsnouveau.api.util.BlockUtil.destroyBlockSafely;
import static invoker54.arsgears.item.utilgear.UtilGearItem.paxelInt;

public class ModBreakEffect extends AbstractEffect {
    public static ModBreakEffect INSTANCE = new ModBreakEffect();
    private static final Logger LOGGER = LogManager.getLogger();

    public ModBreakEffect() {
        super("modded_break", "Break");
    }

    @Override
    public int getManaCost() {
        return 5;
    }

    public ItemStack getStack(LivingEntity shooter, boolean copy){
//        LOGGER.debug("IS REAL PLAYER? " + (isRealPlayer(shooter)));
//        LOGGER.debug("Who be the player?? " + (getPlayer(shooter, (ServerWorld) shooter.level)).getName().getString());
//        LOGGER.debug("ITEMS IN FAKE INVENTORY: " + getPlayer(shooter, (ServerWorld) shooter.level).inventory.items);
//        LOGGER.debug("HAS GEAR STACK? " + !(ArsUtil.getHeldGearCap(shooter, true, true)).isEmpty());
        if(isRealPlayer(shooter)){
            ItemStack mainHand = PlayerDataCap.getCap(shooter).getUtilityGear();
            return (copy) ? mainHand.copy() : mainHand;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        ItemStack gearStack = getStack(shooter, false);
//        if (gearStack.isEmpty()) {
//            PortUtil.sendMessageNoSpam(shooter, new TranslationTextComponent("ars_gears.chat.need_utility_gear"));
//            return;
//        }
        GearCap cap = GearCap.getCap(gearStack);
        CompoundNBT itemTag = cap.getTag(paxelInt);

        BlockPos pos = rayTraceResult.getBlockPos();
        BlockState state;

        int aoeBuff = spellStats.getBuffCount(AugmentAOE.INSTANCE);
        int pierceBuff = spellStats.getBuffCount(AugmentPierce.INSTANCE);
        List<BlockPos> posList = SpellUtil.calcAOEBlocks(shooter, pos, rayTraceResult, aoeBuff, pierceBuff);
        ItemStack gearCopy = spellStats.hasBuff(AugmentSensitive.INSTANCE) ? new ItemStack(Items.SHEARS) : getStack(shooter, true);
        //If the gearCopy is enchanted I should remove those enchants
        if (gearCopy.isEnchanted()){
            EnchantmentHelper.setEnchantments(new HashMap<>(), gearCopy);
        }

        //This is for counting how many blocks that can be broken before the tool takes damage
        String breakString = "glyph_break_count";
        int breakCount = itemTag.getInt(breakString);
        for(BlockPos pos1 : posList) {
            LOGGER.debug("BEGIN BREAK");
            breakCount++;

            if (gearStack.getDamageValue() == gearStack.getMaxDamage() - 1){
                PortUtil.sendMessageNoSpam(shooter, new TranslationTextComponent("ars_gears.chat.repair_utility_gear"));
                return;
            }

            state = world.getBlockState(pos1);
            if(!canBlockBeHarvested(spellStats.getAmpMultiplier(), cap.getTier(), world, pos1) || !BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerWorld) world), world, pos1)){
                continue;
            }
            /** This was the silk touch and fortune code, commented it out to use pickaxe stuff instead */
            //Grab the enchants
            Map<Enchantment, Integer> enchants = EnchantmentHelper.deserializeEnchantments(itemTag.getList("Enchantments", 10));
            int silkBonus = 0;
            int fortuneBonus = 0;

            if(spellStats.hasBuff(AugmentExtract.INSTANCE)) {
                silkBonus = enchants.getOrDefault(Enchantments.SILK_TOUCH, 0);
                gearCopy.enchant(Enchantments.SILK_TOUCH, silkBonus);
            }else if(spellStats.hasBuff(AugmentFortune.INSTANCE)) {
                fortuneBonus = enchants.getOrDefault(Enchantments.BLOCK_FORTUNE, 0);
                gearCopy.enchant(Enchantments.BLOCK_FORTUNE, fortuneBonus);
            }

            state.getBlock().popExperience((ServerWorld) world, pos1, state.getExpDrop(world, pos1, fortuneBonus, silkBonus));
            state.getBlock().playerDestroy(world, getPlayer(shooter, (ServerWorld) world), pos1, world.getBlockState(pos1), world.getBlockEntity(pos1), gearCopy);
            destroyBlockSafely(world, pos1, false, getPlayer(shooter, (ServerWorld) world));
            LOGGER.debug("WHATS COUNT? " + (breakCount));
            if (breakCount == 3) {
                breakCount = 0;
                gearStack.hurtAndBreak(1, getPlayer(shooter, (ServerWorld) world), (p_220009_1_) -> {
                    p_220009_1_.broadcastBreakEvent(shooter.getUsedItemHand());
                });
            }
            LOGGER.debug("END BREAK");
        }
        //Save the break count for later
        itemTag.putInt(breakString, breakCount);
    }

    public boolean canBlockBeHarvested(double ampMultiplier, GearTier tier, World world, BlockPos pos) {
        return world.getBlockState(pos).getDestroySpeed(world, pos) >= 0.0F && this.getBaseHarvestLevel(ampMultiplier, tier) >= world.getBlockState(pos).getHarvestLevel();
    }

    public int getBaseHarvestLevel(double ampMultiplier, GearTier tier) {
        return (int) (tier.getLevel() + ampMultiplier);
    }

    @Override
    public boolean defaultedStarterGlyph() {
        return false;
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        ItemStack gearCopy = getStack(shooter, true);
        GearTier tier = GearCap.getCap(gearCopy).getTier();

        return rayTraceResult instanceof BlockRayTraceResult && world.getBlockState(((BlockRayTraceResult) rayTraceResult).getBlockPos()).getMaterial() != Material.AIR && canBlockBeHarvested(this.getAmplificationBonus(augments), tier, world, ((BlockRayTraceResult) rayTraceResult).getBlockPos());
    }

    @Override
    public boolean dampenIsAllowed() {
        return true;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.IRON_PICKAXE;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(
//                AugmentAmplify.INSTANCE,
                AugmentDampen.INSTANCE,
                AugmentPierce.INSTANCE,
                AugmentAOE.INSTANCE,
//                AugmentExtract.INSTANCE,
//                AugmentFortune.INSTANCE,
                AugmentSensitive.INSTANCE
        );
    }

    @Override
    public String getBookDescription() {
//        return "A spell you start with. Breaks blocks of an average hardness. Can be amplified to increase the harvest level. Sensitive will simulate breaking blocks with Shears instead of a pickaxe.";
        return "Breaks blocks based on your Utility G.E.A.R tier. Can be dampened to decrease harvest level, Sensitive will simulate breaking blocks with Shears instead of a pickaxe.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.ELEMENTAL_EARTH);
    }
}
