package invoker54.arsgears.client.gui;

import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.SpellValidationError;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.client.gui.buttons.CraftingButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GlyphButton;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiSpellSlot;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.network.NetworkHandler;
import invoker54.arsgears.network.message.OpenGearContainerMsg;
import invoker54.arsgears.network.message.edited.PacketUpdateSpellbook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsGears.MOD_ID)
public class modGuiSpellBook extends GuiSpellBook {
    private static final Logger LOGGER = LogManager.getLogger();

    public modGuiSpellBook(CompoundNBT tag, int tier, String unlockedSpells) {
        super(tag, tier, unlockedSpells);
    }

    public static void open(CompoundNBT spell_book_tag, int tier, String unlockedSpells){
        Minecraft.getInstance().setScreen(new modGuiSpellBook(spell_book_tag, tier, unlockedSpells));
    }

    @Override
    public void init() {
        super.init();
        ArrayList<GuiSpellSlot> spellSlots = new ArrayList<>();
        for (IGuiEventListener button : this.children()){
            if (button instanceof GuiSpellSlot){
                LOGGER.debug("CHECKING THIS BUTTONS TYPE " + button.getClass());
                spellSlots.add((GuiSpellSlot) button);
            }
        }

        while (spellSlots.size() > max_spell_tier){
            LOGGER.debug("REMOVING BUTTON ");
            //This will remove the bottom most spell slot (so 10, 9, 8, etc.)
            this.children().remove(spellSlots.get(spellSlots.size() - 1));
            this.buttons.remove(spellSlots.get(spellSlots.size() - 1));

            spellSlots.remove(spellSlots.size() - 1);
            LOGGER.debug("Now the size is: " + this.children.size());
        }
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        super.render(ms, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onCreateClick(Button button) {
        super.onCreateClick(button);
        if (validationErrors.isEmpty()) {
            List<String> ids = new ArrayList<>();
            try {
                for (CraftingButton slot : getCraftingCells()) {
                    ids.add(slot.spellTag);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            try {
                NetworkHandler.INSTANCE.sendToServer(
                        new PacketUpdateSpellbook(ids.toString(), this.getCastSlot(), this.spell_name.getValue()));
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<CraftingButton> getCraftingCells() throws NoSuchFieldException, IllegalAccessException {
        Class myClass = GuiSpellBook.class;

        Field cells = myClass.getDeclaredField("craftingCells");
        cells.setAccessible(true);
        return (List<CraftingButton>) cells.get(this);
    }

    public int getCastSlot() throws NoSuchFieldException, IllegalAccessException {
        Class myClass = GuiSpellBook.class;

        Field cast_slot = myClass.getDeclaredField("selected_cast_slot");
        cast_slot.setAccessible(true);
        return (int) cast_slot.get(this);
    }
}
