package invoker54.arsgears.client.gui;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.client.gui.book.BaseBook;
import com.hollingsworth.arsnouveau.client.gui.buttons.GuiImageButton;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.event.item.combatgear.CombatGearItem;
import invoker54.arsgears.network.NetworkHandler;
import invoker54.arsgears.network.message.edited.PacketUpdateSpellColors;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.OptionSlider;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ModGuiColorScreen extends BaseBook {

    double red;
    double blue;
    double green;
    int slot;

    protected ModGuiColorScreen(double red, double green, double blue, int slot) {
        super();
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.slot = slot;
    }

    OptionSlider redW;
    OptionSlider greenW;
    OptionSlider blueW;
    @Override
    public void init() {
        super.init();
        redW = (OptionSlider)buildSlider(new TranslationTextComponent("ars_nouveau.color_gui.red_slider").getString(), s -> red, (settings, d) -> red = d).createButton(Minecraft.getInstance().options, bookLeft + 28, bookTop + 49, 100);
        greenW = (OptionSlider)buildSlider(new TranslationTextComponent("ars_nouveau.color_gui.green_slider").getString(), s -> green, (settings, d) -> green = d).createButton(Minecraft.getInstance().options, bookLeft + 28, bookTop + 89, 100);
        blueW = (OptionSlider)buildSlider(new TranslationTextComponent("ars_nouveau.color_gui.blue_slider").getString(), s -> blue, (settings, d) -> blue = d).createButton(Minecraft.getInstance().options, bookLeft + 28, bookTop + 129, 100);
        addButton(redW);
        addButton(greenW);
        addButton(blueW);
        addButton(new GuiImageButton(bookLeft+ 55, bookBottom - 36, 0,0,37, 12, 37, 12, "textures/gui/save_icon.png", this::onSaveClick));
        addPresets();
    }

    public void addPresets(){
        // Default
        addButton(new GuiImageButton(bookRight - 131, bookTop + 44, 0,0,48, 11, 48, 11,
                "textures/gui/default_color_icon.png", (_2) -> setFromPreset(255, 25, 180)));
        // Purple
        addButton(new GuiImageButton(bookRight - 131, bookTop + 68, 0,0,48, 11, 48, 11,
                "textures/gui/purple_color_icon.png", (_2) -> setFromPreset(80, 25, 255)));

        // Blue
        addButton(new GuiImageButton(bookRight - 131, bookTop + 92, 0,0,48, 11, 48, 11,
                "textures/gui/blue_color_icon.png", (_2) -> setFromPreset(30, 25, 255)));

        // Red
        addButton(new GuiImageButton(bookRight - 131, bookTop + 116, 0,0,48, 11, 48, 11,
                "textures/gui/red_color_icon.png", (_2) -> setFromPreset(255, 25, 25)));

        // Green
        addButton(new GuiImageButton(bookRight - 131, bookTop + 140, 0,0,48, 11, 48, 11,
                "textures/gui/green_color_icon.png", (_2) -> setFromPreset(25, 255, 25)));

        // Yellow
        addButton(new GuiImageButton(bookRight - 73, bookTop + 44, 0,0,48, 11, 48, 11,
                "textures/gui/yellow_color_icon.png", (_2) -> setFromPreset(255, 255, 25)));
        // White
        addButton(new GuiImageButton(bookRight - 73, bookTop + 68, 0,0,48, 11, 48, 11,
                "textures/gui/white_color_icon.png", (_2) -> setFromPreset(255, 255, 255)));

        // Orange
        addButton(new GuiImageButton(bookRight - 73, bookTop + 92, 0,0,48, 11, 48, 11,
                "textures/gui/orange_color_icon.png", (_2) -> setFromPreset(255, 90, 1)));

        // Cyan
        addButton(new GuiImageButton(bookRight - 73, bookTop + 116, 0,0,48, 11, 48, 11,
                "textures/gui/cyan_color_icon.png", (_2) -> setFromPreset(25, 255, 255)));




    }
    public void setFromPreset(int r, int g, int b){
        red = r;
        green = g;
        blue = b;
        redW.setValue((r)/255.0);
        greenW.setValue((g)/255.0);
        blueW.setValue((b)/255.0);

    }

    public void onSaveClick(Button button){
        NetworkHandler.INSTANCE.sendToServer(new PacketUpdateSpellColors(slot, red, green, blue));
        ItemStack gearStack = ArsUtil.getHeldItem(ClientUtil.mC.player, CombatGearItem.class);
        if(!gearStack.isEmpty()){
            CompoundNBT tag = gearStack.hasTag() ? gearStack.getTag() : new CompoundNBT();
            SpellBook.setSpellColor(tag, new ParticleColor.IntWrapper((int) red * 255, (int) green * 255, (int) blue * 255), slot);
            gearStack.setTag(tag);
            SpellBook.setMode(tag, slot);
        }
        ModGuiSpellBook.open(gearStack);
    }

    @Override
    public void drawBackgroundElements(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(stack, mouseX, mouseY, partialTicks);
        drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/gui/slider_gilding.png"), 22, 47, 0, 0, 112, 104,112,104, stack);
        int color = -8355712;
        minecraft.font.draw(stack, new TranslationTextComponent("ars_nouveau.color_gui.title").getString(), 51, 24,  color);
        minecraft.font.draw(stack, new TranslationTextComponent("ars_nouveau.color_gui.presets").getString(), 159, 24,  color);
        minecraft.font.draw(stack, new TranslationTextComponent("ars_nouveau.color_gui.default").getString(), 170, 46,  color);
        minecraft.font.draw(stack, new TranslationTextComponent("ars_nouveau.color_gui.purple").getString(), 170, 70,  color);
        minecraft.font.draw(stack, new TranslationTextComponent("ars_nouveau.color_gui.blue").getString(), 170, 94,  color);
        minecraft.font.draw(stack, new TranslationTextComponent("ars_nouveau.color_gui.red").getString(), 170, 118,  color);
        minecraft.font.draw(stack, new TranslationTextComponent("ars_nouveau.color_gui.green").getString(), 170, 142,  color);
        minecraft.font.draw(stack, new TranslationTextComponent("ars_nouveau.color_gui.yellow").getString(), 228, 46,  color);
        minecraft.font.draw(stack, new TranslationTextComponent("ars_nouveau.color_gui.white").getString(), 228, 70,  color);
        minecraft.font.draw(stack, new TranslationTextComponent("ars_nouveau.color_gui.orange").getString(), 228, 94,  color);
        minecraft.font.draw(stack, new TranslationTextComponent("ars_nouveau.color_gui.cyan").getString(), 228, 118,  color);
       // minecraft.fontRenderer.drawString(stack, "Ice", 218, 115,  0);
        minecraft.font.draw(stack, new TranslationTextComponent("ars_nouveau.color_gui.save").getString(), 67, 160,  color);
        int xOffset = 10;


//
//        fillGradient(stack,
//                0, 0,
//                50,
//                100,
//                1073741824, 1073741824);
//        int r = (int)red & 0xFF;
//        int g = (int)green & 0xFF;
//        int b = (int)blue & 0xFF;
//        int a = 1 & 0xFF;
//
//        int rgb = (r << 24) + (g << 16) + (b << 8) + (a);
//       //System.out.println(rgb);
//        System.out.println(red);
//        fill(stack,
//                0, 0,
//                50,
//                100,
//                new ParticleColor(red, green, blue).getColor());

    }

    @Override
    public void drawForegroundElements(int mouseX, int mouseY, float partialTicks) {
        super.drawForegroundElements(mouseX, mouseY, partialTicks);
    }

    protected SliderPercentageOption buildSlider(String key, Function<GameSettings, Double> getter, BiConsumer<GameSettings, Double> setter){
        return new SliderPercentageOption(key, 1.0D, 255.0D, 1.0F, getter, setter, (settings, optionValues) -> new StringTextComponent(key + (int)optionValues.get(settings)));
    }
}
