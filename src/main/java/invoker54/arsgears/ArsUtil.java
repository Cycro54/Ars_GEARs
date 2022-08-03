package invoker54.arsgears;

import net.minecraft.client.renderer.OutlineLayerBuffer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArsUtil {
    private static final Logger LOGGER = LogManager.getLogger();

    public static ItemStack getHeldItem(LivingEntity entity, Class<?> CLASS){
        if (entity.getMainHandItem().getItem().getClass() == CLASS){
            return entity.getMainHandItem();
        }
        else if(entity.getOffhandItem().getItem().getClass() == CLASS){
            return entity.getOffhandItem();
        }
        else return ItemStack.EMPTY;
    }

    public static void replaceItemStack(PlayerEntity player, ItemStack oldItemStack, ItemStack newItemStack){
        LOGGER.debug("Old Item: " + oldItemStack.getDisplayName().getString());
        LOGGER.debug("New Item: " + newItemStack.getDisplayName().getString());

        //First go through main inventory
        for (int a = 0; a < player.inventory.items.size(); a++){
            if (player.inventory.items.get(a) == oldItemStack){
                player.inventory.items.set(a, newItemStack);
                return;
            }
        }

        //Next offhand
        if (player.inventory.offhand.get(0) == oldItemStack){
            player.inventory.offhand.set(0, newItemStack);
            return;
        }

        //Lastly go through armor slots
        for (int a = 0; a < player.inventory.armor.size(); a++){
            if (player.inventory.armor.get(a) == oldItemStack){
                player.inventory.armor.set(a, newItemStack);
                return;
            }
        }
    }
}
