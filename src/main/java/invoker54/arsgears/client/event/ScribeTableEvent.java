package invoker54.arsgears.client.event;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.client.ClientOnly;
import invoker54.arsgears.event.item.combatgear.CombatGearItem;
import invoker54.arsgears.event.item.utilgear.UtilGearItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID)
public class ScribeTableEvent {
    @SubscribeEvent
    public static void rightClickTable(PlayerInteractEvent.RightClickBlock event){
        if(event.getWorld().getBlockState(event.getPos()).getBlock() == BlockRegistry.SCRIBES_BLOCK){
            ItemStack gearStack = event.getItemStack();
            if (gearStack.getItem() instanceof UtilGearItem || gearStack.getItem() instanceof CombatGearItem) {
                event.setCanceled(true);
                event.setCancellationResult(ActionResultType.FAIL);
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientOnly.openUpgradeScreen(gearStack));
            }
        }
    }
}
