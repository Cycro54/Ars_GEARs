package invoker54.arsgears.event;


import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.common.items.Glyph;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.capability.player.PlayerDataCap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID)
public class GlyphUseEvent {

    @SubscribeEvent
    public static void onUseGlyph(PlayerInteractEvent.RightClickItem event){
        ItemStack itemStack = event.getItemStack();

        if (!(itemStack.getItem() instanceof Glyph)) return;

        if (((Glyph)itemStack.getItem()).spellPart instanceof AbstractCastMethod) {
            event.getPlayer().sendMessage(new StringTextComponent("You can't learn cast methods!"),  Util.NIL_UUID);
            return;
        }
        //If this is happening on the client, just return.
        if (event.getSide() == LogicalSide.CLIENT) return;

        //Grab the extra needed objects
        PlayerEntity player = event.getPlayer();
        Glyph glyph = (Glyph) itemStack.getItem();
        PlayerDataCap cap = PlayerDataCap.getCap(player);
        ItemStack gearStack = cap.getCombatGear();
        if (gearStack.isEmpty()) gearStack = cap.getCombatGear();

        //Now lets use the method stuff directly from the Glyph use item class
        if (SpellBook.getUnlockedSpells(gearStack.getOrCreateTag()).contains(glyph.spellPart)){
            player.sendMessage(new StringTextComponent("You already know this spell!"),  Util.NIL_UUID);
            return;
        }
        SpellBook.unlockSpell(gearStack.getOrCreateTag(), glyph.spellPart.getTag());
        itemStack.shrink(1);
        player.sendMessage(new StringTextComponent("Unlocked " + glyph.spellPart.getName()), Util.NIL_UUID);

        //Make sure to cancel the event if it makes it to the end
        event.setCanceled(true);
    }
}
