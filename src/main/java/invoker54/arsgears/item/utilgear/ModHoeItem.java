package invoker54.arsgears.item.utilgear;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.item.GearTier;
import invoker54.arsgears.item.GearUpgrades;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropsBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ModHoeItem extends HoeItem {

    private static final Logger LOGGER = LogManager.getLogger();

    public ModHoeItem(GearTier gearTier, int attack, float speed, Properties builder) {
        super(gearTier, attack, speed, builder);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(final ItemStack gearStack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        if (world == null) return;

        GearCap cap = GearCap.getCap(gearStack);

        if (GearUpgrades.getUpgrades(gearStack).size() == 0) return;

        CompoundNBT upgrades = GearUpgrades.getUpgrades(gearStack);
        if (upgrades.contains(GearUpgrades.hoeDrops))
            tooltip.add(GearUpgrades.getFullName(GearUpgrades.hoeDrops, upgrades));
        if (upgrades.contains(GearUpgrades.hoeRadius))
            tooltip.add(GearUpgrades.getFullName(GearUpgrades.hoeRadius, upgrades));
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
    public static class HoeAbilities{
        //This is for harvest radius and multiplier
        @SubscribeEvent(priority = EventPriority.LOW)
        public static void cropHarvest(BlockEvent.BreakEvent event) {
            if (event.isCanceled()) return;

            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            ItemStack gearStack = ArsUtil.getHeldGearCap(player, true, false);
            if (!(gearStack.getItem() instanceof ModHoeItem)) return;

            //Grab all the needed block details
            BlockState state = event.getState();
            BlockPos blockPos = event.getPos();
            IWorld world = event.getWorld();

            //Make sure we aren't on the client
            if (world.isClientSide()) return;

            //Make sure this is a farmable block
            if (!(state.getBlock() instanceof CropsBlock)) return;

            //Make sure to cancel the event!
            event.setCanceled(true);

            //Lets make sure the player has this upgrade
            int radiusLvl = GearUpgrades.getUpgrade(gearStack, GearUpgrades.hoeRadius);
            ArrayList<BlockPos> blockPosList = new ArrayList<>();
            blockPosList.add(blockPos);

            // LOGGER.debug("RADIUS LEVEL " + (radiusLvl));
            if (radiusLvl != 0) {
                //Left to right
                for (int a = -(radiusLvl); a < radiusLvl + 1; a++) {
                    //Backward to forward
                    for (int b = -(radiusLvl); b < radiusLvl + 1; b++) {
                        if (a == 0 && b == 0) continue;
                        // LOGGER.debug("pos offset X:" + (a) + " Y:" + (0) + " Z:" + (b));
                        BlockState state2 = world.getBlockState(blockPos.offset(a, 0, b));
                        // LOGGER.debug("THE BLOCK STATE is empty? " + (state2.getBlock() instanceof AirBlock));
                        // LOGGER.debug("What is the block for this block state? " + (state2.getBlock()));
                        // LOGGER.debug("Does it equal first block state? " + (state.equals(state2)));

                        if (!(state2.getBlock() instanceof CropsBlock)) continue;

                        boolean maxAge = ((CropsBlock) state2.getBlock()).isMaxAge(state2);
                        if (maxAge) blockPosList.add(blockPos.offset(a, 0, b));
                    }
                }
            }

            int multiplyLvl = GearUpgrades.getUpgrade(gearStack, GearUpgrades.hoeDrops);
            //Next lets get them drops!
            for (BlockPos pos : blockPosList) {
                //Damage the item
                gearStack.hurtAndBreak(1, player, (playerEntity) -> {
                    playerEntity.broadcastBreakEvent(player.getUsedItemHand());
                });

                BlockState cropState = world.getBlockState(pos);
                List<ItemStack> drops = Block.getDrops(state, (ServerWorld) world, pos, world.getBlockEntity(pos));

                boolean maxAge = ((CropsBlock) world.getBlockState(pos).getBlock()).isMaxAge(cropState);

                for (ItemStack foodDrop : drops) {
                    if (maxAge && multiplyLvl != 0) {
                        for (int a = 0; a < multiplyLvl + 1; a++) {
                            Block.popResource((World) world, blockPos, foodDrop);
                        }
                    } else {
                        Block.popResource((World) world, blockPos, foodDrop);
                    }
                }

                //Now let's destroy the crops while I'm at it
                ((ServerWorld) world).setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
            }
        }

        /** This will be moved to the food overhaul mod I am making */
//        @SubscribeEvent(priority = EventPriority.HIGHEST)
//        public static void onCropHit(BlockEvent.BreakEvent event){
//            if (event.isCanceled()) return;
//
//            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
//            ItemStack itemStack = event.getPlayer().getUseItem();
//            boolean isHoe = (itemStack.getItem() instanceof HoeItem);
//
//            //Grab all the needed block details
//            BlockState state = event.getState();
//            BlockPos blockPos = event.getPos();
//            IWorld world = event.getWorld();
//
//            //Make sure we aren't on the client
//            if (world.isClientSide()) return;
//
//            //Make sure this is a farmable block
//            if (!(state.getBlock() instanceof CropsBlock)) return;
//
//            //Destroy the crop if the player uses something besides a hoe and destroy chance is higher than 25%
//            if (!isHoe && Math.random() > 0.25f) {
//                ((ServerWorld) world).setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());
//                event.setCanceled(true);
//            }
//        }
    }
}
