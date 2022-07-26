package invoker54.arsgears.event;

import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.client.ClientOnly;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
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
            if (event.getHand() != Hand.OFF_HAND) return;
            if (!event.getPlayer().isCrouching()) return;

            ItemStack gearStack = event.getItemStack();
            GearCap cap = GearCap.getCap(gearStack);

            if (cap != null) {
                event.setCanceled(true);
                event.setCancellationResult(ActionResultType.FAIL);
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientOnly.openUpgradeScreen(cap));
            }
        }
    }
}
