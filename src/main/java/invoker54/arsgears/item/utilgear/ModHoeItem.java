package invoker54.arsgears.item.utilgear;

import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.item.GearTier;
import invoker54.arsgears.item.GearUpgrades;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

import java.util.List;

public class ModHoeItem extends HoeItem {


    public ModHoeItem(GearTier gearTier, int attack, float speed, Properties builder) {
        super(gearTier, attack, speed, builder);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(final ItemStack gearStack, @Nullable final World world, final List<ITextComponent> tooltip, final ITooltipFlag flag) {
        if (world == null) return;

        GearCap cap = GearCap.getCap(gearStack);

        if (GearUpgrades.getUpgrades(cap.getSelectedItem(), cap).size() == 0) return;

        CompoundNBT upgrades = GearUpgrades.getUpgrades(UtilGearItem.hoeInt, cap);
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
}
