package invoker54.arsgears.client.event;

import com.hollingsworth.arsnouveau.api.util.StackUtil;
import com.hollingsworth.arsnouveau.client.gui.GuiSpellHUD;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsGears.MOD_ID)
public class modGuiSpellHUD extends GuiSpellHUD {

    @Override
    public void drawHUD(MatrixStack ms) {
        ItemStack stack = ArsUtil.getHeldItem(ClientUtil.mC.player, CombatGearItem.class);
        if(!stack.isEmpty() && ((CombatGearItem)stack.getItem()).getTier().ordinal() > 1){
            int offsetLeft = 10;
            CompoundNBT tag = stack.getOrCreateTag();
            int mode = tag.getInt(SpellBook.BOOK_MODE_TAG);
            String renderString;
            renderString = mode + " " + SpellBook.getSpellName(stack.getTag());
            ClientUtil.mC.font.drawShadow(ms,renderString, offsetLeft, ClientUtil.mC.getWindow().getGuiScaledHeight() - 30 , 0xFFFFFF);
        }
    }
}
