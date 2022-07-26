package invoker54.arsgears.item.utilgear;

import invoker54.arsgears.capability.player.PlayerDataCap;
import invoker54.arsgears.capability.utilgear.UtilGearCap;
import invoker54.arsgears.init.ItemInit;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class UtilGearItem extends ToolItem {
    private static final Logger LOGGER = LogManager.getLogger();
    private final String UTIL_GEAR_CAP = "UTIL_GEAR_CAP";

    private final PaxelItem paxel = (PaxelItem) ItemInit.WOOD_PAXEL.getItem();
    private final FishingRodItem fishingRodItem = (FishingRodItem) Items.FISHING_ROD.getItem();
    private final HoeItem hoeItem = (HoeItem) Items.WOODEN_HOE.getItem();

    public UtilGearItem(IItemTier tier, Item.Properties builder) {
        super(0, 1, tier, null, builder);
    }

    @Override
    public @NotNull ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        //LOGGER.debug("Is this in the main hand? " + (handIn == Hand.MAIN_HAND));
        switch (getSelectedItem(playerIn.getItemInHand(handIn))){
            default:
                return paxel.use(worldIn, playerIn, handIn);
            case 1:
                return customFishingMethod(worldIn, playerIn, handIn);
            case 2:
                return hoeItem.use(worldIn, playerIn, handIn);
        }
    }

    @Override
    public int getEnchantmentValue() {
        return 0;

//        switch (getSelectedItem()){
//            default:
//                return paxel.getItemEnchantability();
//            case 1:
//                return fishingRodItem.getItemEnchantability();
//            case 2:
//                return hoeItem.getItemEnchantability();
//        }
    }

    @Override
    public boolean canHarvestBlock(ItemStack itemStack, BlockState blockIn) {
        switch (getSelectedItem(itemStack)){
            default:
                return paxel.canHarvestBlock(itemStack, blockIn);
            case 1:
                return fishingRodItem.canHarvestBlock(itemStack, blockIn);
            case 2:
                return hoeItem.canHarvestBlock(itemStack, blockIn);
        }
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        switch (getSelectedItem(context.getItemInHand())){
            default:
                return paxel.useOn(context);
            case 1:
                return fishingRodItem.useOn(context);
            case 2:
                return hoeItem.useOn(context);
        }    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state){
        switch (getSelectedItem(stack)){
            default:
                return paxel.getDestroySpeed(stack, state);
            case 1:
                return fishingRodItem.getDestroySpeed(stack, state);
            case 2:
                return hoeItem.getDestroySpeed(stack, state);
        }
    }

    public int getSelectedItem(ItemStack itemStack) {
        UtilGearCap cap = UtilGearCap.getCap(itemStack);

        return cap.getSelectedItem();
    }

    @Nullable
    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        CompoundNBT cNBT = stack.getOrCreateTag();

        CompoundNBT capNBT = UtilGearCap.getCap(stack).serializeNBT();

        cNBT.put(UTIL_GEAR_CAP, capNBT);
        return cNBT;
    }

    //region Fishing shtuff
    public ActionResult<ItemStack> customFishingMethod(World worldIn, PlayerEntity playerIn, Hand handIn) {
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
                worldIn.addFreshEntity(new CustomBobberEntity(playerIn, worldIn, k, j));
            }

            playerIn.awardStat(Stats.ITEM_USED.get(this));
        }

        return ActionResult.sidedSuccess(itemStack, worldIn.isClientSide());
    }

    //endregion
}
