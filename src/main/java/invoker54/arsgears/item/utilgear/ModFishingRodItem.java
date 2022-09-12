package invoker54.arsgears.item.utilgear;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.entity.ModBobberEntity;
import invoker54.arsgears.item.GearUpgrades;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
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
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

public class ModFishingRodItem extends FishingRodItem {


    public ModFishingRodItem(Properties builder) {
        super(builder);
    }

    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemStack = playerIn.getItemInHand(handIn);

        /** This will be moved to the food overhaul mod I am making */
//        if (ArsUtil.getItemStack(playerIn, BaitItem.class).isEmpty()) {
//            if (!worldIn.isClientSide()) {
//                PortUtil.sendMessageNoSpam(playerIn, new TranslationTextComponent("ars_gears.chat.need_bait"));
//            }
//            return ActionResult.fail(itemStack);
//        }
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

        if (GearUpgrades.getUpgrades(gearStack).size() == 0) return;

        CompoundNBT upgrades = GearUpgrades.getUpgrades(gearStack);

        /** This will be moved to the food overhaul mod I am making */
//        if (upgrades.contains(GearUpgrades.fishrodBaitKeep))
//            tooltip.add(GearUpgrades.getFullName(GearUpgrades.fishrodBaitKeep, upgrades));
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

    @Mod.EventBusSubscriber(modid = ArsGears.MOD_ID)
    public static class FishingRodAbilities{

        //This is for xp gain AND bait losing
        @SubscribeEvent
        public static void onFish(ItemFishedEvent event){
            if (event.isCanceled()) return;

            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            ItemStack gearStack = ArsUtil.getHeldGearCap(player, true, false);
            if (!(gearStack.getItem() instanceof ModFishingRodItem)) return;

            //This will be used for taking the bait
            /** This will be moved to the food overhaul mod I am making */
//            ItemStack baitStack = ArsUtil.getItemStack(player, BaitItem.class);
//            if (baitStack.isEmpty()){
//                event.setCanceled(true);
//                return;
//            }
//
//            if (!player.abilities.instabuild || baitStack.getItem() != ItemInit.STARBUNCLE_BAIT) {
//                GearCap cap = GearCap.getCap(gearStack);
//                int baitKeepLvl = GearUpgrades.getUpgrade(gearStack, GearUpgrades.fishrodBaitKeep);
//                float chanceToKeep;
//                switch (baitKeepLvl){
//                    default:
//                        chanceToKeep = 0;
//                        break;
//                    case 1:
//                        chanceToKeep = 0.2F;
//                        break;
//                    case 2:
//                        chanceToKeep = 0.4F;
//                        break;
//                }
//                //Chance to keep falls as you grow in tier (every 2 tiers)
//                chanceToKeep -= ((int)(cap.getTier().ordinal()/2f) * 0.2f);
//                float chanceToLose = (float) Math.random();
//                baitStack.shrink((chanceToLose < chanceToKeep) ? 0 : 1);
//            }


            //This will be for additional xp ( I might just give the playe a bottle o' enchanting)
            double x = player.getX();
            double y = player.getY() + 0.5D;
            double z = player.getZ() + 0.5D;
            //player.level.addFreshEntity(new ExperienceOrbEntity(player.level, x, y, z, event.getHookEntity()..nextInt(6) + 1));

        }
    }
}
