package invoker54.arsgears;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class ArsUtil {


    public static ItemStack getHeldItem(LivingEntity entity, Class<?> CLASS){
        if (entity.getMainHandItem().getItem().getClass() == CLASS){
            return entity.getMainHandItem();
        }
        else if(entity.getOffhandItem().getItem().getClass() == CLASS){
            return entity.getOffhandItem();
        }
        else return ItemStack.EMPTY;
    }
}
