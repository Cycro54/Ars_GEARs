package invoker54.arsgears.client.gui.button;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.arsgears.capability.player.PlayerDataCap;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.client.gui.ModGuiSpellBook;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ModCreateSpellButton extends ModGuiImageButton {
    private final ResourceLocation image = new ResourceLocation(ArsNouveau.MODID, "textures/gui/create_icon.png");

    public ModCreateSpellButton(ModGuiSpellBook parent, int x, int y, IPressable onPress) {
        super(x, y, 0,0,50, 12, 50, 12, "textures/gui/create_icon.png", onPress);
        this.parent = parent;
    }

    @Override
    public void render(MatrixStack ms, int parX, int parY, float partialTicks) {
        if (visible) {
            PlayerEntity player = ClientUtil.mC.player;
            PlayerDataCap cap = PlayerDataCap.getCap(player);
            if (cap == null) return;
            ItemStack gearStack = cap.getCombatGear();
            float coolDown = CombatGearItem.getCooldown(player, gearStack.getOrCreateTag(), parent.selected_cast_slot, true);
            if (parent.validationErrors.isEmpty() && coolDown <= 0) {
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            } else {
                GL11.glColor4f(1.0F, 0.7F, 0.7F, 1.0F);
            }

            ModGuiSpellBook.drawFromTexture(image, x, y, u, v, width, height, image_width, image_height, ms);

            if (parent.isMouseInRelativeRange(parX, parY, x, y, width, height)) {
                if (!parent.validationErrors.isEmpty() || coolDown > 0) {
                    List<ITextComponent> tooltip = new ArrayList<>();
                    boolean foundGlyphErrors = false;

                    tooltip.add(new TranslationTextComponent("ars_nouveau.spell.validation.crafting.invalid").withStyle(TextFormatting.RED));

                    // Add any spell-wide errors
                    for (SpellValidationError error : parent.validationErrors) {
                        if (error.getPosition() < 0) {
                            tooltip.add(error.makeTextComponentExisting());
                        } else {
                            foundGlyphErrors = true;
                        }
                    }

                    // Show a single placeholder for all the per-glyph errors
                    if (foundGlyphErrors) {
                        tooltip.add(new TranslationTextComponent("ars_nouveau.spell.validation.crafting.invalid_glyphs"));
                    }
                    else{
                        tooltip.add(new TranslationTextComponent("ars_gears.chat.cast_cooldown"));
                    }

                    parent.tooltip = tooltip;
                }
            }
        }
        // We've handled all of the required rendering at this point.  No need to call super.
        //super.render(ms, parX, parY, partialTicks);
    }
}
