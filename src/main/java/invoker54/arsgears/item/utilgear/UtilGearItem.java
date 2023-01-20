package invoker54.arsgears.item.utilgear;

import invoker54.arsgears.capability.gear.GearCap;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class UtilGearItem extends Item {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String UTIL_GEAR_CAP = "UTIL_GEAR_CAP";
    public static int paxelINT = 0;
    public static int fishingINT = 1;
    public static int hoeINT = 2;

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
