package invoker54.arsgears.item.utilgear;

import invoker54.arsgears.capability.UtilGearCap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CustomBobberEntity extends FishingBobberEntity {

    public CustomBobberEntity(PlayerEntity p_i50220_1_, World p_i50220_2_, int p_i50220_3_, int p_i50220_4_) {
        super(p_i50220_1_, p_i50220_2_, p_i50220_3_, p_i50220_4_);
    }

    @Override
    public boolean shouldStopFishing(PlayerEntity p_234600_1_) {
        ItemStack itemstack = p_234600_1_.getMainHandItem();
        ItemStack itemstack1 = p_234600_1_.getOffhandItem();
        boolean flag = itemstack.getItem() instanceof UtilGearItem;
        if (flag) flag = checkIfOnRod(itemstack);
        boolean flag1 = itemstack1.getItem() instanceof UtilGearItem;
        if (flag1) flag1 = checkIfOnRod(itemstack1);
        if (!p_234600_1_.removed && p_234600_1_.isAlive() && (flag || flag1) && !(this.distanceToSqr(p_234600_1_) > 1024.0D)) {
            return false;
        } else {
            this.remove();
            return true;
        }
    }
    private boolean checkIfOnRod(ItemStack itemStack){
        UtilGearCap cap = UtilGearCap.getCap(itemStack);
        return cap.getSelectedItem() == 1;
    }
}

