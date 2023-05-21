package invoker54.arsgears.client.event;

import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.config.ArsGearsConfig;
import invoker54.arsgears.item.FakeSpellBook;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class OpenBookEvent {

    @SubscribeEvent
    public static void openScreenEvent(GuiOpenEvent event){
        if (!(event.getGui() instanceof GuiSpellBook)) return;
        if (ArsGearsConfig.disableSpellBookCooldown) return;

        PlayerEntity player = ClientUtil.mC.player;
        ItemStack bookStack = player.getMainHandItem();

        if (!(bookStack.getItem() instanceof SpellBook)) bookStack = player.getOffhandItem();
        if (!(bookStack.getItem() instanceof SpellBook)) return;
        if (bookStack.getItem() instanceof FakeSpellBook) return;

        CompoundNBT bookNBT = CombatGearCap.getCap(player).getBookTag();
        if (CombatGearItem.getCooldown(player, bookNBT, SpellBook.getMode(bookStack.getOrCreateTag()), true) > 0){
            player.sendMessage(new TranslationTextComponent("ars_gears.chat.cant_craft_spells"), Util.NIL_UUID);
            event.setCanceled(true);
        }
    }
}
