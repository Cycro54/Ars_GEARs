package invoker54.arsgears.client.gui.button;

import com.ibm.icu.lang.UCharacter;
import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.client.gui.UpgradeScreen;
import invoker54.arsgears.init.ItemInit;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.swing.*;

import java.util.ArrayList;
import java.util.List;

import static invoker54.arsgears.client.ClientUtil.*;

public class UpgradeButton extends Button{


    ResourceLocation image;
    public Irequirement requirement;
    ITextComponent optionalMsg;
    ClientUtil.Bounds bounds;
    int price;

    public UpgradeButton(int x, int y, int width, int height, String name, ResourceLocation image,
                         ClientUtil.Bounds bounds, int price, Irequirement requirement, IPressable onPress) {

        super(x, y, width, height, ITextComponent.nullToEmpty(name), onPress);
        this.image = image;
        this.requirement = requirement;
        this.visible = true;
        this.bounds = bounds;
        this.price = price;
    }

    @Override
    public void renderButton(MatrixStack stack, int xMouse, int yMouse, float partialTicks) {
        //Make it run so the button updates
        optionalMsg = requirement.check(this);

        if (this.isHovered) {
            this.isHovered = inBounds(xMouse, yMouse, bounds);
        }
        TEXTURE_MANAGER.bind(WIDGETS_LOCATION);
        int i = this.getYImage(this.isHovered());
        i = 46 + (i * 20);

        //left part of the button
        ClientUtil.blitImage(stack, x, this.width / 2, this.y, this.height,
                0, this.width / 2f, i, 20, 256);
//            //left part of the button
//            this.blit(stack, x, this.y, 0, 46 + i * 20, this.width / 2, this.height);

        //right part of the button
        ClientUtil.blitImage(stack, x + this.width / 2, this.width / 2, this.y, this.height,
                200 - (this.width / 2), this.width / 2, i, 20, 256);
//            //right part of the button
//            this.blit(stack, x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        //Finally render the image
        TEXTURE_MANAGER.bind(image);
        ClientUtil.blitImage(stack, x + (this.width - 16)/2, 16, y + (this.height - 16)/2, 16, 0, 16, 0, 16, 16);
    }

    public int getPrice(){
        return price;
    }

    public List<ITextComponent> getTooltip(){
        List<ITextComponent> textList = new ArrayList<>();

        //First add the title (\247a will make it green)
        textList.add(this.getMessage());
        //Next up is price
        textList.add(ITextComponent.nullToEmpty("Price:\247a §l" + price));
        //Players XP
        textList.add(ITextComponent.nullToEmpty("Your XP:\247a §l" + mC.player.totalExperience));
        //Now comes the space
        textList.add(ITextComponent.nullToEmpty(""));
        //Finally the last tid bit of info
        textList.add(optionalMsg);
        return textList;
    }

    @Override
    public boolean mouseClicked(double xMouse, double yMouse, int mouseButton) {
        if (!inBounds((float) xMouse, (float) yMouse, bounds)) return false;

        return super.mouseClicked(xMouse, yMouse, mouseButton);
    }

    public interface Irequirement {
        ITextComponent check(UpgradeButton button);
    }
}
