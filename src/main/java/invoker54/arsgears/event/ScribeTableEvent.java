package invoker54.arsgears.event;

import com.hollingsworth.arsnouveau.common.block.ScribesBlock;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.client.ClientOnly;
import invoker54.arsgears.init.ItemInit;
import invoker54.arsgears.item.FakeSpellBook;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import software.bernie.example.registry.ItemRegistry;
import sun.nio.ch.Net;

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
