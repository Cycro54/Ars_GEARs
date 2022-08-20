package invoker54.arsgears.item.utilgear;

import com.google.common.collect.Sets;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.item.GearUpgrades;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static invoker54.arsgears.item.utilgear.UtilGearItem.UTIL_GEAR_CAP;
import static invoker54.arsgears.item.utilgear.UtilGearItem.paxelInt;

public class PaxelItem extends ToolItem {
    private static final Logger LOGGER = LogManager.getLogger();

    //AxeItem stuff
    private static final Set<Material> AXE_DIGGABLE_MATERIALS = Sets.newHashSet(Material.WOOD, Material.NETHER_WOOD, Material.PLANT, Material.REPLACEABLE_PLANT, Material.BAMBOO, Material.VEGETABLE);
    private static final Set<Block> AXE_EFFECTIVE_ON = Sets.newHashSet(Blocks.LADDER, Blocks.SCAFFOLDING, Blocks.OAK_BUTTON, Blocks.SPRUCE_BUTTON, Blocks.BIRCH_BUTTON, Blocks.JUNGLE_BUTTON, Blocks.DARK_OAK_BUTTON, Blocks.ACACIA_BUTTON, Blocks.CRIMSON_BUTTON, Blocks.WARPED_BUTTON);
    //PickaxeItem stuff
    private static final Set<Block> PICKAXE_EFFECTIVE_ON = Sets.newHashSet(Blocks.ACTIVATOR_RAIL, Blocks.COAL_ORE, Blocks.COBBLESTONE, Blocks.DETECTOR_RAIL, Blocks.DIAMOND_BLOCK, Blocks.DIAMOND_ORE, Blocks.POWERED_RAIL, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE, Blocks.NETHER_GOLD_ORE, Blocks.ICE, Blocks.IRON_BLOCK, Blocks.IRON_ORE, Blocks.LAPIS_BLOCK, Blocks.LAPIS_ORE, Blocks.MOSSY_COBBLESTONE, Blocks.NETHERRACK, Blocks.PACKED_ICE, Blocks.BLUE_ICE, Blocks.RAIL, Blocks.REDSTONE_ORE, Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE, Blocks.CUT_SANDSTONE, Blocks.CHISELED_RED_SANDSTONE, Blocks.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE, Blocks.STONE, Blocks.GRANITE, Blocks.POLISHED_GRANITE, Blocks.DIORITE, Blocks.POLISHED_DIORITE, Blocks.ANDESITE, Blocks.POLISHED_ANDESITE, Blocks.STONE_SLAB, Blocks.SMOOTH_STONE_SLAB, Blocks.SANDSTONE_SLAB, Blocks.PETRIFIED_OAK_SLAB, Blocks.COBBLESTONE_SLAB, Blocks.BRICK_SLAB, Blocks.STONE_BRICK_SLAB, Blocks.NETHER_BRICK_SLAB, Blocks.QUARTZ_SLAB, Blocks.RED_SANDSTONE_SLAB, Blocks.PURPUR_SLAB, Blocks.SMOOTH_QUARTZ, Blocks.SMOOTH_RED_SANDSTONE, Blocks.SMOOTH_SANDSTONE, Blocks.SMOOTH_STONE, Blocks.STONE_BUTTON, Blocks.STONE_PRESSURE_PLATE, Blocks.POLISHED_GRANITE_SLAB, Blocks.SMOOTH_RED_SANDSTONE_SLAB, Blocks.MOSSY_STONE_BRICK_SLAB, Blocks.POLISHED_DIORITE_SLAB, Blocks.MOSSY_COBBLESTONE_SLAB, Blocks.END_STONE_BRICK_SLAB, Blocks.SMOOTH_SANDSTONE_SLAB, Blocks.SMOOTH_QUARTZ_SLAB, Blocks.GRANITE_SLAB, Blocks.ANDESITE_SLAB, Blocks.RED_NETHER_BRICK_SLAB, Blocks.POLISHED_ANDESITE_SLAB, Blocks.DIORITE_SLAB, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.PISTON, Blocks.STICKY_PISTON, Blocks.PISTON_HEAD);

    //ShovelItem stuff
    private static final Set<Block> SHOVEL_EFFECTIVE_ON = Sets.newHashSet(Blocks.CLAY, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.FARMLAND, Blocks.GRASS_BLOCK, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.RED_SAND, Blocks.SNOW_BLOCK, Blocks.SNOW, Blocks.SOUL_SAND, Blocks.GRASS_PATH, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.SOUL_SOIL);
    private static final Set<Block> DIGGABLES = Sets.newHashSet(Blocks.CLAY, Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.FARMLAND, Blocks.GRASS_BLOCK, Blocks.GRAVEL, Blocks.MYCELIUM, Blocks.SAND, Blocks.RED_SAND, Blocks.SNOW_BLOCK, Blocks.SNOW, Blocks.SOUL_SAND, Blocks.GRASS_PATH, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER, Blocks.SOUL_SOIL);


    public PaxelItem(IItemTier tier, float attackDamageIn, float attackSpeedIn, Item.Properties builder) {
        super(attackDamageIn, attackSpeedIn, tier, DIGGABLES, builder
                .addToolType(net.minecraftforge.common.ToolType.AXE, tier.getLevel())
                .addToolType(net.minecraftforge.common.ToolType.PICKAXE, tier.getLevel())
                .addToolType(net.minecraftforge.common.ToolType.SHOVEL, tier.getLevel()));}

    private static Set<Block> combineSets(){
        Set<Block> combinedSet = Sets.newHashSet();
        combinedSet.addAll(AXE_EFFECTIVE_ON);
        combinedSet.addAll(PICKAXE_EFFECTIVE_ON);
        combinedSet.addAll(SHOVEL_EFFECTIVE_ON);

        return combinedSet;
    }

    /**
     * Check whether this Item can harvest the given Block (mostly came from PickaxeItem class)
     */
    @Override
    public boolean canHarvestBlock(ItemStack itemStack, BlockState blockIn) {
        int i = this.getTier().getLevel();
        if (blockIn.getHarvestTool() == net.minecraftforge.common.ToolType.PICKAXE) {
            return i >= blockIn.getHarvestLevel();
        }
        Material material = blockIn.getMaterial();
        return material == Material.STONE || material == Material.METAL || material == Material.HEAVY_METAL
                //This comes from the ShovelItem class
                || blockIn.is(Blocks.SNOW) || blockIn.is(Blocks.SNOW_BLOCK);
    }

    /**
     * Called when this item is used when targetting a Block
     */
    @Override
    public ActionResultType useOn(ItemUseContext p_195939_1_) {
        World world = p_195939_1_.getLevel();
        BlockPos blockpos = p_195939_1_.getClickedPos();
        BlockState blockstate = world.getBlockState(blockpos);

        //Axe code
        BlockState block = blockstate.getToolModifiedState(world, blockpos, p_195939_1_.getPlayer(), p_195939_1_.getItemInHand(), net.minecraftforge.common.ToolType.AXE);
        if (block != null) {
            PlayerEntity playerentity = p_195939_1_.getPlayer();
            world.playSound(playerentity, blockpos, SoundEvents.AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!world.isClientSide) {
                world.setBlock(blockpos, block, 11);
                if (playerentity != null) {
                    p_195939_1_.getItemInHand().hurtAndBreak(1, playerentity, (p_220040_1_) -> {
                        p_220040_1_.broadcastBreakEvent(p_195939_1_.getHand());
                    });
                }
            }

            return ActionResultType.sidedSuccess(world.isClientSide);
        }
        //Shovel code
        else if (p_195939_1_.getClickedFace() == Direction.DOWN) {
            return ActionResultType.PASS;
        } else {
            PlayerEntity playerentity = p_195939_1_.getPlayer();
            BlockState blockstate1 = blockstate.getToolModifiedState(world, blockpos, playerentity, p_195939_1_.getItemInHand(), net.minecraftforge.common.ToolType.SHOVEL);
            BlockState blockstate2 = null;
            if (blockstate1 != null && world.isEmptyBlock(blockpos.above())) {
                world.playSound(playerentity, blockpos, SoundEvents.SHOVEL_FLATTEN, SoundCategory.BLOCKS, 1.0F, 1.0F);
                blockstate2 = blockstate1;
            } else if (blockstate.getBlock() instanceof CampfireBlock && blockstate.getValue(CampfireBlock.LIT)) {
                if (!world.isClientSide()) {
                    world.levelEvent((PlayerEntity)null, 1009, blockpos, 0);
                }

                CampfireBlock.dowse(world, blockpos, blockstate);
                blockstate2 = blockstate.setValue(CampfireBlock.LIT, Boolean.valueOf(false));
            }

            if (blockstate2 != null) {
                if (!world.isClientSide) {
                    world.setBlock(blockpos, blockstate2, 11);
                    if (playerentity != null) {
                        p_195939_1_.getItemInHand().hurtAndBreak(1, playerentity, (p_220041_1_) -> {
                            p_220041_1_.broadcastBreakEvent(p_195939_1_.getHand());
                        });
                    }
                }

                return ActionResultType.sidedSuccess(world.isClientSide);
            } else {
                return ActionResultType.PASS;
            }
        }

    }

    //Pickaxe custom method
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        Material material = state.getMaterial();
        //Below is PickaxeItem code
        return (material == Material.METAL || material == Material.HEAVY_METAL || material == Material.STONE) ? this.speed
                //Below is AxeItem code
                : (AXE_DIGGABLE_MATERIALS.contains(material) ? this.speed : super.getDestroySpeed(stack, state));

    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(final ItemStack gearStack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        if (world == null) return;

        GearCap cap = GearCap.getCap(gearStack);
        CompoundNBT upgrades = GearUpgrades.getUpgrades(UtilGearItem.paxelInt, cap);

        if (upgrades.contains(GearUpgrades.paxelAutoInv))
            tooltip.add(GearUpgrades.getFullName(GearUpgrades.paxelAutoInv, upgrades));
//        if (upgrades.contains(GearUpgrades.paxelRadialMine))
//            tooltip.add(GearUpgrades.getFullName(GearUpgrades.paxelRadialMine, upgrades));
    }

    @Nullable
    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        CompoundNBT cNBT = stack.getOrCreateTag();

        CompoundNBT capNBT = GearCap.getCap(stack).serializeNBT();

        cNBT.put(UTIL_GEAR_CAP, capNBT);
        return cNBT;
    }

    @Override
    public void readShareTag(ItemStack stack, @org.jetbrains.annotations.Nullable CompoundNBT nbt) {
        super.readShareTag(stack, nbt);
        GearCap.getCap(stack).deserializeNBT(nbt.getCompound(UTIL_GEAR_CAP));
    }

    @Mod.EventBusSubscriber(modid = ArsGears.MOD_ID)
    public static class PaxelAbilities{

        /** This is the method for auto placing blocks in your inventory, and switching between fortune and Silk touch
         * */
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onMine(BlockEvent.BreakEvent event){
            if (event.isCanceled()) return;

            PlayerEntity player = event.getPlayer();
            ItemStack gearStack = GearCap.getCap(player.getMainHandItem()) == null ? ItemStack.EMPTY : player.getMainHandItem();
            if (gearStack.isEmpty() || GearCap.getCap(gearStack) instanceof CombatGearCap) return;

            //Lets make sure the player has this upgrade
            int autoInvLvl = GearUpgrades.getUpgrade(paxelInt, GearCap.getCap(gearStack), GearUpgrades.paxelAutoInv);
            int silkTouchLvl = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, gearStack);
            if (player.isCrouching()) silkTouchLvl = 0;
            int fortuneLvl = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, gearStack);

            //Grab all the needed block details
            BlockState state = event.getState();
            BlockPos blockPos = event.getPos();
            IWorld world = event.getWorld();

            //Make sure we aren't on the client
            if (world.isClientSide()) return;

            //Cancel the event
            event.setCanceled(true);

            //Deal with Silk Touch
            ItemStack gearStack1 = gearStack.copy();
            Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(gearStack1);
            if (!player.isCrouching() && enchants.containsKey(Enchantments.SILK_TOUCH)){
                enchants.remove(Enchantments.SILK_TOUCH);
                EnchantmentHelper.setEnchantments(enchants, gearStack1);
            }

            //Let's grab those drops
            List<ItemStack> drops =
            Block.getDrops(state, (ServerWorld) world, blockPos, world.getBlockEntity(blockPos), null, gearStack1);

            //Destroy the block (this only works for piston and stairs.)
            state.getBlock().destroy(world, blockPos, state);

            boolean flag1 = state.canHarvestBlock(world, blockPos, player); // previously player.hasCorrectToolForDrops(state)
            gearStack.mineBlock((World) world, state, blockPos, player);
            if (gearStack.isEmpty() && !gearStack1.isEmpty())
                ForgeEventFactory.onPlayerDestroyItem(player, gearStack1, Hand.MAIN_HAND);
            boolean flag = state.removedByPlayer((World) world, blockPos, player, flag1, world.getFluidState(blockPos));

            if (flag && flag1) {
                player.awardStat(Stats.BLOCK_MINED.get(state.getBlock()));
                player.causeFoodExhaustion(0.005F);
            }

            //Destroy the block
            ((ServerWorld) world).setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState());

            int EXP = state.getExpDrop(world, blockPos, fortuneLvl, silkTouchLvl);
            if (autoInvLvl != 0) {
                BlockPos playerPos = new BlockPos(player.getX(), player.getY(), player.getZ());
                state.getBlock().popExperience((ServerWorld) world, playerPos, EXP);
                for (ItemStack drop : drops) {
                    if (!player.addItem(drop)) {
                        Block.popResource((World) world, blockPos, drop);
                    }
                }
            }
            else {
                state.getBlock().popExperience((ServerWorld) world, blockPos, EXP);
                for (ItemStack drop : drops) {
                    Block.popResource((World) world, blockPos, drop);
                }
            }
        }
//        //This is for gaining mana and stone while mining stone
//        @SubscribeEvent
//        public static void onMineStone(BlockEvent.BreakEvent event){
//            if (event.isCanceled()) return;
//
//            PlayerEntity player = event.getPlayer();
//            ItemStack gearStack = ArsUtil.getHeldGearCap(player, true);
//            if (gearStack.isEmpty()) return;
//
//            //Lets make sure the player has this upgrade
//            int level = GearUpgrades.getUpgrade(paxelINT, GearCap.getCap(gearStack), GearUpgrades.paxelAutoInv);
//
//            if (level == 0) return;
//
//            //Grab all the needed block details
//            BlockState state = event.getState();
//            BlockPos blockPos = event.getPos();
//            IWorld world = event.getWorld();
//
//            //Cancel the event
//            event.setCanceled(true);
//
//            //Make sure we aren't on the client
//            if (world.isClientSide()) return;
//
//            //Let's grab those drops
//            List<ItemStack> drops =
//            Block.getDrops(state, (ServerWorld) world, blockPos, world.getBlockEntity(blockPos));
//
//            //Destroy the block (this only works for piston and stairs.)
//            state.getBlock().destroy(world, blockPos, state);
//
//            ItemStack itemstack = player.getMainHandItem();
//            ItemStack itemstack1 = itemstack.copy();
//            boolean flag1 = state.canHarvestBlock(world, blockPos, player); // previously player.hasCorrectToolForDrops(state)
//            itemstack.mineBlock((World) world, state, blockPos, player);
//            if (itemstack.isEmpty() && !itemstack1.isEmpty())
//                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, itemstack1, Hand.MAIN_HAND);
//            boolean flag = state.removedByPlayer((World) world, blockPos, player, flag1, world.getFluidState(blockPos));
//
//            if (flag && flag1) {
//                player.awardStat(Stats.BLOCK_MINED.get(state.getBlock()));
//                player.causeFoodExhaustion(0.005F);
//            }
//
//            int EXP = state.getExpDrop(world, blockPos, 1, 0);
//            BlockPos playerPos = new BlockPos(player.getX(), player.getY(), player.getZ());
//            state.getBlock().popExperience((ServerWorld) world, playerPos, EXP);
//
//            for (ItemStack drop : drops){
//                if (!player.addItem(drop)){
//                    Block.popResource((World) world, blockPos, drop);
//                }
//            }
//        }
    }
}
