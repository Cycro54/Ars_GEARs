package invoker54.arsgears.item.utilgear;

import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.item.GearUpgrades;
import invoker54.arsgears.item.GearTier;
import invoker54.arsgears.network.NetworkHandler;
import invoker54.arsgears.network.message.OpenGearContainerMsg;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class UtilGearItem extends Item {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String UTIL_GEAR_CAP = "UTIL_GEAR_CAP";
    public static int paxelINT = 0;
    public static int fishingInt = 1;
    public static int hoeInt = 2;

    public UtilGearItem(IItemTier tier, Item.Properties builder) {
        super(builder.durability(tier.getUses()));
    }

    @Override
    public boolean isFoil(ItemStack p_77636_1_) {
        return false;
    }

    @Override
    public boolean isValidRepairItem(ItemStack p_82789_1_, ItemStack p_82789_2_) {
        return false;
    }
    @Override
    public boolean isRepairable(ItemStack p_isRepairable_1_) {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack p_77616_1_) {
        return false;
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

    //region Fishing shtuff
    //endregion
}
