package invoker54.arsgears.entity;

import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.item.utilgear.ModFishingRodItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ModBobberEntity extends FishingBobberEntity {

    public ModBobberEntity(PlayerEntity player, World worldIn, int luck, int lureSpeed) {
        super(player, worldIn, luck, lureSpeed);
    }

    @Override
    public boolean shouldStopFishing(PlayerEntity player) {
        ItemStack fishRod = ArsUtil.getHeldItem(player, ModFishingRodItem.class);
        if (!player.removed && player.isAlive() && !fishRod.isEmpty() && !(this.distanceToSqr(player) > 1024.0D)) {
            return false;
        } else {
            this.remove();
            return true;
        }
    }
}