package invoker54.arsgears.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.client.gui.button.UpgradeButton;
import invoker54.arsgears.init.ItemInit;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.stream.FactoryConfigurationError;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static invoker54.arsgears.client.ClientUtil.*;

public class UpgradeScreen extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();

    //Upgrade screen background
    ResourceLocation background = new ResourceLocation("textures/gui/advancements/backgrounds/adventure.png");

    //Upgrade screen Border
    ResourceLocation upgradeWindow = new ResourceLocation(ArsGears.MOD_ID, "textures/gui/upgrade_screen/upgrade_window.png");

    //These are the scrolls things
    public float scrollX = 0;
    public float scrollY = 0;
    private boolean isScrolling;

    //This is padding between buttons
    int paddingX = 8;
    int paddingY = 16;
    int buttonSize = 20;

    LinkedHashMap<String, ArrayList<UpgradeButton>> categories = new LinkedHashMap<>();

    ClientUtil.Bounds bounds;

    private final int imageWidth = 252;
    private final int imageHeight = 205;
    int halfWidthSpace;
    int halfHeightSpace;

    public UpgradeScreen() {
        super(ITextComponent.nullToEmpty(""));
    }

    @Override
    protected void init() {
        categories.clear();
        halfWidthSpace = (width - imageWidth) /2;
        halfHeightSpace = (height - imageHeight) /2;

        bounds = new Bounds(halfWidthSpace, imageWidth, halfHeightSpace, imageHeight);
    }

    @Override
    public void render(MatrixStack stack, int xMouse, int yMouse, float partialTicks) {
        //Start the crop
        ClientUtil.beginCrop(halfWidthSpace + 2, imageWidth - 2, halfHeightSpace + 2, imageHeight - 2, true);
        //This will render the background
        TEXTURE_MANAGER.bind(background);
        ClientUtil.blitImage(stack, halfWidthSpace , imageWidth,
                halfHeightSpace, imageHeight, 0 - scrollX, imageWidth, 0 - scrollY, imageHeight, 16);


        //This will render the buttons
        super.render(stack, xMouse, yMouse, partialTicks);

        //End the crop
        ClientUtil.endCrop();

        //Draw the outline
        drawBoundingBox(stack);

        //Finally render a tooltip
        for(ArrayList<UpgradeButton> list : categories.values()){
            for (UpgradeButton button : list){
                if (button.isHovered()){
                    renderComponentTooltip(stack, button.getTooltip(), xMouse, yMouse);
                    return;
                }
            }
        }
    }

    public void drawBoundingBox(MatrixStack stack){
        TEXTURE_MANAGER.bind(upgradeWindow);
        ClientUtil.blitImage(stack, halfWidthSpace, imageWidth, halfHeightSpace, imageHeight, 0, imageWidth, 0, imageHeight, 256);
    }

    public boolean mouseDragged(double xOrigin, double yOrigin, int mouseButton, double xDistance, double yDistance) {
        if (mouseButton != 0) {
            this.isScrolling = false;
            return false;
        } else {
            if (!this.isScrolling) {
                this.isScrolling = true;
            } else {
                this.scroll(xDistance, yDistance);
            }

            return true;
        }
    }
    public void scroll(double xDistance, double yDistance) {
        this.scrollX = (float) MathHelper.clamp(this.scrollX + xDistance, -imageWidth/2f, imageWidth/2f);
        LOGGER.debug("SCROLL X IS AT " + scrollX);

        this.scrollY = (float) MathHelper.clamp(this.scrollY + yDistance, -imageHeight/2f, imageHeight/2f);

        //This will update the position of the buttons
        UpdatePositions();
    }
    public void UpdatePositions(){
        //What this effectively does is centers the lists perfectly into the middle (buttonSize is the button width and height)
        int xPos = (int) (halfWidthSpace + (imageWidth - (categories.size() * buttonSize) + ((categories.size() - 1) * paddingX))/2 + this.scrollX);
        //Y won't be centered
        int yPos = (int) (halfHeightSpace + 40 + scrollY);
        for (ArrayList<UpgradeButton> buttonList : categories.values()){
            int yPosDupe = yPos;
            for (UpgradeButton button : buttonList){
                button.x = xPos;
                button.y = yPosDupe;
                
                yPosDupe += button.getHeight() + paddingY;
            }
            
            xPos += buttonSize + paddingX;
        }
    }
    public Button createUpgrade(String category, ResourceLocation image, int price, UpgradeButton.Irequirement require, Button.IPressable purchaseFunc){
        if (!categories.containsKey(category)) categories.put(category, new ArrayList<>());

       UpgradeButton button = new UpgradeButton(0,0, buttonSize, buttonSize,
               category + " " + (categories.get(category).size() + 1), image, this.bounds, price,require, purchaseFunc);

       addButton(button);

       categories.get(category).add(button);

       UpdatePositions();

       return button;
    }
}
