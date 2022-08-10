package invoker54.arsgears.client.gui.button;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.client.gui.ModGuiSpellBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import javax.xml.soap.Text;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class ModCraftingButton extends ModGuiImageButton {
    int slotNum;
    public String spellTag;
    public String resourceIcon;
    public int stack = 0;
    public List<SpellValidationError> validationErrors;

    public ModCraftingButton(ModGuiSpellBook parent, int x, int y, int slotNum, Button.IPressable onPress) {
        super( x, y, 0, 0, 22, 20, 22, 20, "textures/gui/spell_glyph_slot.png", onPress);
        this.slotNum = slotNum;
        this.spellTag = "";
        this.resourceIcon = "";
        this.validationErrors = new LinkedList<>();
        this.parent = parent;
    }

    public void clear() {
        this.spellTag = "";
        this.resourceIcon = "";
        this.stack = 0;
        this.validationErrors.clear();
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return button == GLFW.GLFW_MOUSE_BUTTON_LEFT || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT;
    }

    @Override
    public boolean mouseClicked(double xMouse, double yMouse, int button) {
        if (this.active && this.visible) {
            if (this.isValidClickButton(button)) {
                boolean flag = this.clicked(xMouse, yMouse);
                if (flag) {
                    this.playDownSound(Minecraft.getInstance().getSoundManager());

                    if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) this.onClick(xMouse, yMouse);
                    else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                        if (parent.sneakHeld) stack = parent.maxAugmentStack;
                        else stack += (stack == parent.maxAugmentStack) ? 0 : 1;
                        parent.validate();
                    }
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    @Override
    public void render(MatrixStack stack, int parX, int parY, float partialTicks) {
        if (visible)
        {
            if (validationErrors.isEmpty()) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                GL11.glColor4f(1.0F, 0.7F, 0.7F, 1.0F);
            }
            //ModGuiSpellBook.drawFromTexture(new ResourceLocation(ExampleMod.MODID, this.resourceIcon), x, y, 0, 0, 20, 20, 20, 20);
            if(!this.resourceIcon.equals("")){
                ModGuiSpellBook.drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/items/" + resourceIcon), x + 3, y + 2, u, v, 16, 16, 16, 16,stack);
            }
            if(parent.isMouseInRelativeRange(parX, parY, x, y, width, height)){

                if(parent.api.getSpell_map().containsKey(this.spellTag)) {
                    List<ITextComponent> tooltip = new LinkedList<>();
                    tooltip.add(new TranslationTextComponent(parent.api.getSpell_map().get(this.spellTag).getLocalizationKey()));
                    for (SpellValidationError ve : validationErrors) {
                        tooltip.add(ve.makeTextComponentExisting().withStyle(TextFormatting.RED));
                    }
                    parent.tooltip = tooltip;
                }
            }
        }
        super.render(stack, parX, parY, partialTicks);
        if (this.stack > 1) {
            ClientUtil.blitColor(stack, this.x - 2, ClientUtil.mC.font.width(("" + this.stack)) + 3, this.y - 1, 12, Color.black.getRGB());
            ClientUtil.mC.font.drawShadow(stack, String.valueOf(this.stack), this.x, this.y, TextFormatting.RED.getColor());
        }
    }
}
