package invoker54.arsgears.client.gui.button;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.arsgears.client.gui.ModGuiSpellBook;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Slots for selecting the spell recipes stored in the book.
 */
public class ModGuiSpellSlot extends ModGuiImageButton {


    public int slotNum;
    public boolean isSelected;

    public ModGuiSpellSlot(ModGuiSpellBook parent, int x, int y, int slotNum) {
        super(x, y, 0, 0, 18, 13, 18, 13,"textures/gui/spell_tab.png", parent::onSlotChange);
        this.parent = parent;
        this.slotNum = slotNum;
        this.isSelected = false;
    }

    @Override
    public void render(MatrixStack stack, int parX, int parY, float partialTicks) {
        if (visible)
        {
            if(parent.isMouseInRelativeRange(parX, parY, x, y, width, height)){
                String name = SpellBook.getSpellName(parent.spell_book_tag, slotNum);
                if(!name.isEmpty()){
                    List<ITextComponent> tip = new ArrayList<>();
                    tip.add(new StringTextComponent(name));
                    parent.tooltip = tip;
                }
            }

            ResourceLocation image;
            image = this.isSelected ? new ResourceLocation(ArsNouveau.MODID, "textures/gui/spell_tab_selected.png") : new ResourceLocation(ArsNouveau.MODID,"textures/gui/spell_tab.png");
            //ModGuiSpellBook.drawFromTexture(image, x, y, u, v, width, height, width, height);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            ModGuiSpellBook.drawFromTexture(image, x, y, u, v, width, height, image_width, image_height, stack);
            drawCenteredString(stack,Minecraft.getInstance().font, String.valueOf(this.slotNum), x + 8, y + 3,  16777215); // White

        }
    }
}
