package invoker54.arsgears.item.utilgear;

import invoker54.arsgears.ArsUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BaitItem extends Item {
    public BaitItem(Properties builder) {
        super(builder);
    }

    public static ItemStack findBait(PlayerEntity player){
        return ArsUtil.getHeldItem(player, BaitItem.class);
    }
}
