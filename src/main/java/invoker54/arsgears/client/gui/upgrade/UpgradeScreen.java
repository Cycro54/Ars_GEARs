package invoker54.arsgears.client.gui.upgrade;

import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.IGearCap;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.client.gui.button.UpgradeButton;
import invoker54.arsgears.init.ItemInit;
import invoker54.arsgears.item.GearTier;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import invoker54.arsgears.network.NetworkHandler;
import invoker54.arsgears.network.message.SyncServerCombatGearMsg;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.stream.FactoryConfigurationError;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static invoker54.arsgears.client.ClientUtil.*;
import static invoker54.arsgears.client.gui.upgrade.CombatUpgradeScreen.setEnchantments;
import static invoker54.arsgears.item.combatgear.CombatGearItem.swordINT;

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

    int colorBlack = new Color(0,0,0,255).getRGB();
    int colorWhite = new Color(255,255,255,255).getRGB();

    //This is the base xp expected (xp level 33 which is 1758)
    int baseXP = 1758;
    //These are the max amount of slots for each upgrade
    int maxLvl = 4;

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


        //Render the lines connecting the upgrades
        TEXTURE_MANAGER.release(background);
        for (ArrayList<UpgradeButton> list : categories.values()){
            UpgradeButton prevButton = null;
            for (UpgradeButton button : list){
                if (button == null) continue;

                //This is where we will grab the first previous button
                if (prevButton == null && button != null){
                    prevButton = button;
                    continue;
                }

                //If the previous button is null
                if (prevButton == null) continue;

                //This will place a line in the middle of the button
                int prevX = (int) (prevButton.x + ((buttonSize - 4)/2f));
                int prevY = (int) (prevButton.y + (buttonSize/2f));
                int height = (int) ((button.y + (buttonSize/2f)) - prevY);

                //This will make the black line
                ClientUtil.blitColor(stack, prevX, 4, prevY, height, colorBlack);

                //This will make the white line
                if (prevButton.purchased){
                    ClientUtil.blitColor(stack, prevX + 1, 2, prevY, height, colorWhite);
                }

                //Don't forget to reassign the previous button variable
                prevButton = button;
            }
        }

        //This will render the buttons
        super.render(stack, xMouse, yMouse, partialTicks);

        //End the crop
        ClientUtil.endCrop();

        //Draw the outline
        drawBoundingBox(stack);

        //Finally render a tooltip
        for(ArrayList<UpgradeButton> list : categories.values()){
            for (UpgradeButton button : list){
                if (button != null && button.isHovered()){
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
        int xPos = (int) (halfWidthSpace + (imageWidth - ((categories.size() * buttonSize) + ((categories.size() - 1) * paddingX)))/2 + this.scrollX);
        //Y won't be centered
        int yPos = (int) (halfHeightSpace + 40 + scrollY);
        for (ArrayList<UpgradeButton> buttonList : categories.values()){
            int yPosDupe = yPos;
            for (UpgradeButton button : buttonList){
                if (button == null){
                    yPosDupe += buttonSize + paddingY;
                    continue;
                }

                button.x = xPos;
                button.y = yPosDupe;

                yPosDupe += button.getHeight() + paddingY;
            }
            
            xPos += buttonSize + paddingX;
        }
    }
    public int getPrice(int totalLevels, String category){
        if (!categories.containsKey(category)) categories.put(category, new ArrayList<>());

        float currLvl = categories.get(category).size() + 1;
        LOGGER.debug("WHATS THE CURRENT LEVEL? " + currLvl);

        //First do the easing (this is easeInQuad)
        float price = ((currLvl/maxLvl)*(currLvl/maxLvl));
        //Then multiply it with the baseXP
        price = price * baseXP;
        //This increases a tiny bit if the upgrade is rare
        price = (price * (1 + ((maxLvl - totalLevels) * 0.1f)));
        //Now make sure to grab the previous buttons and subtract their price from this price
        for (UpgradeButton button : categories.get(category)){
            if (button == null) continue;

            price -= button.getPrice();
        }

        return (int) price;
    }
    public void createEmptyCategory(){
        categories.put(("Empty" + categories.size()), new ArrayList<>());

        UpdatePositions();
    }
    public void createEmptyUpgrade(String category){
        if (!categories.containsKey(category)) categories.put(category, new ArrayList<>());

        categories.get(category).add((UpgradeButton) null);

        UpdatePositions();
    }

    public UpgradeButton createEnchantUpgrade(Class gearClass, int cycleInt, String catName, Enchantment enchantment,
                                              int maxUpgrades, int upgradeLvl, ResourceLocation image, UpgradeButton prevButton){

        if (!categories.containsKey(catName)) categories.put(catName, new ArrayList<>());

        ItemStack gearStack = ClientUtil.mC.player.getMainHandItem();

        IGearCap cap;

        if (gearClass == UtilGearItem.class) cap = GearCap.getCap(gearStack);
        else { cap = CombatGearCap.getCap(gearStack); }

        int playerTier = ((CombatGearItem)gearStack.getItem()).getTier().ordinal();
        int upgradeTier = categories.get(catName).size() + 1;

        //Grabs the enchantments for the related item
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.deserializeEnchantments(cap.getTag(cycleInt).getList("Enchantments", 10));

        //Make the requirement
        UpgradeButton.Irequirement iRequire = (button) ->{
            PlayerEntity player = ClientUtil.mC.player;

            //Make sure they don't have this enchant already
            if (enchantments.containsKey(enchantment) && enchantments.get(enchantment) >= upgradeLvl){
                button.active = false;
                button.purchased = true;
                return ITextComponent.nullToEmpty("\247aPurchased");
            }
            //Make sure the gear capability is at this tier
            else if (playerTier < upgradeTier){
                button.active = false;
                return ITextComponent.nullToEmpty("\247cYou must upgrade\nto tier " + (GearTier.values()[upgradeTier]));
            }
            //Make sure they bought the previous upgrade
            else if (prevButton != null && prevButton.purchased == false){
                button.active = false;
                return ITextComponent.nullToEmpty("\247cYou must purchase\nthe previous upgrade");
            }

            //Make sure they can afford it
            else if (button.getPrice() > player.totalExperience) {
                button.active = false;
                return ITextComponent.nullToEmpty("\247cYou need: " + (button.getPrice() - player.totalExperience));
            }

            //Finally tell them they can purchase it
            else {
                button.active = true;
                return ITextComponent.nullToEmpty("Purchase upgrade?");
            }

        };

        //Make the pressable
        Button.IPressable iPress = (button) -> {
            //Add this enchantment to their map
            enchantments.put(enchantment, upgradeLvl);
            //Place the updated map back into the gearStack
            setEnchantments(enchantments, cap.getTag(cycleInt));

            //Now finally make sure to sync these changes with the server
            NetworkHandler.INSTANCE.sendToServer(new SyncServerCombatGearMsg(cap.getTag(cycleInt), cycleInt));

            button.active = false;
        };

        int price = getPrice(maxUpgrades, catName);

        return createUpgrade(catName, image, price, iRequire, iPress);
    }
    public UpgradeButton createUpgrade(String category, ResourceLocation image, int price, UpgradeButton.Irequirement require, Button.IPressable purchaseFunc){
        if (!categories.containsKey(category)) categories.put(category, new ArrayList<>());

       UpgradeButton button = new UpgradeButton(0,0, buttonSize, buttonSize,
               category + " " + (categories.get(category).size() + 1), image, this.bounds, price,require, purchaseFunc);

       addButton(button);

       categories.get(category).add(button);

       UpdatePositions();

       return button;
    }
}
