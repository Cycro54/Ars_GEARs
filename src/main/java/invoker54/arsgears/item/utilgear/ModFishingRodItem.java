package invoker54.arsgears.item.utilgear;

import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.entity.ModBobberEntity;
import invoker54.arsgears.item.GearUpgrades;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

import java.util.List;

public class ModFishingRodItem extends FishingRodItem {


    public ModFishingRodItem(Properties builder) {
        super(builder);
    }

    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemStack = playerIn.getItemInHand(handIn);
        int j;
        if (playerIn.fishing != null) {
            if (!worldIn.isClientSide) {
                j = playerIn.fishing.retrieve(itemStack);
                itemStack.hurtAndBreak(j, playerIn, (p_220000_1_) -> {
                    p_220000_1_.broadcastBreakEvent(handIn);
                });
            }

            worldIn.playSound((PlayerEntity)null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundCategory.NEUTRAL, 1.0F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
        } else {
            worldIn.playSound((PlayerEntity)null, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
            if (!worldIn.isClientSide) {
                j = EnchantmentHelper.getFishingSpeedBonus(itemStack);
                int k = EnchantmentHelper.getFishingLuckBonus(itemStack);
                worldIn.addFreshEntity(new ModBobberEntity(playerIn, worldIn, k, j));
            }

            playerIn.awardStat(Stats.ITEM_USED.get(this));
        }

        return ActionResult.sidedSuccess(itemStack, worldIn.isClientSide());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(final ItemStack gearStack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        if (world == null) return;

        GearCap cap = GearCap.getCap(gearStack);

        if (GearUpgrades.getUpgrades(cap.getSelectedItem(), cap).size() == 0) return;

        CompoundNBT upgrades = GearUpgrades.getUpgrades(UtilGearItem.fishingInt, cap);

        if (upgrades.contains(GearUpgrades.fishrodBaitKeep))
            tooltip.add(GearUpgrades.getFullName(GearUpgrades.fishrodBaitKeep, upgrades));
        if (upgrades.contains(GearUpgrades.fishrodXPGain))
            tooltip.add(GearUpgrades.getFullName(GearUpgrades.fishrodXPGain, upgrades));
    }

    @Nullable
    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        CompoundNBT cNBT = stack.getOrCreateTag();

        CompoundNBT capNBT = GearCap.getCap(stack).serializeNBT();

        cNBT.put(UtilGearItem.UTIL_GEAR_CAP, capNBT);
        return cNBT;
    }

    @Override
    public void readShareTag(ItemStack stack, @org.jetbrains.annotations.Nullable CompoundNBT nbt) {
        super.readShareTag(stack, nbt);
        GearCap.getCap(stack).deserializeNBT(nbt.getCompound(UtilGearItem.UTIL_GEAR_CAP));
    }
}
