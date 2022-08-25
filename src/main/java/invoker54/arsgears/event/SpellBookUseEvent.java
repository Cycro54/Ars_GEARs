package invoker54.arsgears.event;


import com.hollingsworth.arsnouveau.common.items.SpellBook;
import invoker54.arsgears.ArsGears;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID)
public class SpellBookUseEvent {

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onUseSpellBook(PlayerInteractEvent.RightClickItem event) {
        ItemStack itemStack = event.getItemStack();

        //If this is happening on the client, just return.
        if (event.getSide() == LogicalSide.CLIENT) return;

        boolean isSpellBook = (itemStack.getItem() instanceof SpellBook);
        PlayerEntity player = event.getPlayer();

        if (isSpellBook) {
            //If they right click on a table while sneaking, don't cancel the event
            if (!player.isCrouching()) {
                player.sendMessage(new TranslationTextComponent("ars_gears.chat.use_spell_book"), Util.NIL_UUID);
            }
            event.setCanceled(true);
        }
    }
}
