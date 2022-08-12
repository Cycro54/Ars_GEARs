package invoker54.arsgears.client.gui.upgrade;

import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.client.gui.button.UpgradeButton;
import invoker54.arsgears.item.GearTier;
import invoker54.arsgears.item.GearUpgrades;
import invoker54.arsgears.network.NetworkHandler;
import invoker54.arsgears.network.message.SyncServerGearMsg;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

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
    int paddingX = 4;
    int paddingY = 16;
    int buttonSize = 20;

    LinkedHashMap<String, ArrayList<UpgradeButton>> categories = new LinkedHashMap<>();

    ClientUtil.Bounds bounds;

    private final int imageWidth = 252;
    private final int imageHeight = 205;
    int halfWidthSpace;
    int halfHeightSpace;

    int colorBlack = new Color(0,0,0,255).getRGB();
    int colorTransparentBlack = new Color(0,0,0, 155).getRGB();
    int colorWhite = new Color(255,255,255,255).getRGB();
    int colorPurchased = new Color(4, 94, 18,255).getRGB();
    int colorSale = new Color(23, 217, 44,255).getRGB();
    int colorDeny = new Color(201, 17, 17, 134).getRGB();
    //int colorDeny = new Color(101, 7, 7,255).getRGB();

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


        //Now render the lines going side to side
        for (int a = 0; a < 4; a++){

            UpgradeButton leftButton = null;
            UpgradeButton rightButton = null;
            for (ArrayList<UpgradeButton> list : categories.values()){
                if (list.isEmpty()) {
                    if (rightButton != null){
                        ClientUtil.blitColor(stack, leftButton.x, rightButton.x - leftButton.x, (int) (leftButton.y + ((buttonSize - 3)/2f)), 3, colorBlack);
                    }

                    leftButton = null;
                    rightButton = null;
                    continue;
                }
                UpgradeButton currButton = list.get(a);

                if (currButton == null) continue;

                if (leftButton == null) leftButton = currButton;
                else rightButton = currButton;
            }
            //This will render the lines for the final category (if there is one)
            if (rightButton != null){
                ClientUtil.blitColor(stack, leftButton.x, rightButton.x - leftButton.x, (int) (leftButton.y + ((buttonSize - 3)/2f)), 3, colorBlack);
            }
        }
        //Render the lines connecting the upgrades up and down
        TEXTURE_MANAGER.release(background);
        ArrayList<UpgradeButton> prevList = null;
        for (ArrayList<UpgradeButton> list : categories.values()){
//            if (list.isEmpty() && prevList != null){
//                for (UpgradeButton button : prevList){
//                    if (button != null){
//                        ClientUtil.blitColor(stack, (int) (button.x + buttonSize + paddingX + ((buttonSize - 3)/2f)), 3, halfHeightSpace, imageHeight, colorBlack);
//                    }
//                }
//            }

            UpgradeButton prevButton = null;
            for (UpgradeButton button : list){

                if (button == null) continue;

                if (!button.purchased && button.active) {
                    ClientUtil.blitColor(stack, button.x - 1, buttonSize + 2, button.y - 1, buttonSize + 2, colorSale);
                }

                //This is where we will grab the first previous button
                if (prevButton == null && button != null){
                    prevButton = button;
                    continue;
                }

                //If the previous button is null
                if (prevButton == null) continue;

                //This will place a line in the middle of the button going up and down
                int prevX = (int) (prevButton.x + ((buttonSize - 4)/2f));
                int prevY = (int) (prevButton.y + (buttonSize/2f));
                int height = (int) ((button.y + (buttonSize/2f)) - prevY);

                //This will make the black line
                ClientUtil.blitColor(stack, prevX, 3, prevY, height, colorBlack);

                //This will make the white line
                if (prevButton.purchased){
                    ClientUtil.blitColor(stack, prevX + 1, 1, prevY, height, colorWhite);
                }

                //Don't forget to reassign the previous button variable
                prevButton = button;
            }

            if (!list.isEmpty()) prevList = list;
        }
        
        //This will render the buttons
        try {
            for(int i = 0; i < this.buttons.size(); ++i) {
                this.buttons.get(i).render(stack, xMouse, yMouse, partialTicks);
                UpgradeButton button = (UpgradeButton) this.buttons.get(i);
                if (button.purchased){
                    ClientUtil.blitColor(stack, button.x, buttonSize, button.y, buttonSize, colorTransparentBlack);
                }
                else if (!button.active){
                    ClientUtil.blitColor(stack, button.x, buttonSize, button.y, buttonSize, colorDeny);
                }
            }
        }
        catch (Exception e){
            LOGGER.error(e);
        }

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
        //LOGGER.debug("WHATS THE CURRENT LEVEL? " + currLvl);

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

    //This was taken from the EnchantmentHelper class
    public static void setEnchantments(Map<Enchantment, Integer> enchants, CompoundNBT tag) {
        ListNBT listnbt = new ListNBT();

        for(Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (enchantment != null) {
                int i = entry.getValue();
                CompoundNBT compoundnbt = new CompoundNBT();
                compoundnbt.putString("id", String.valueOf((Object) Registry.ENCHANTMENT.getKey(enchantment)));
                compoundnbt.putShort("lvl", (short)i);
                listnbt.add(compoundnbt);
            }
        }

        tag.put("Enchantments", listnbt);
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

    public void createCustomUpgrade(int gearCycle, String upgradeName, int[] upgradeLvl, ResourceLocation image){
        String catName = GearUpgrades.getName(upgradeName).getString();

        if (categories.containsKey(catName)){
            LOGGER.error("DUPLICATE NAME FOUND " + catName);
            return;
        }
        else {categories.put(catName, new ArrayList<>());}

        //This is just to make sure that the upgrade lvl integers are all correct (also to get the total levels)
        int totalLvl = 0;
        if (upgradeLvl.length != 4){
            LOGGER.error("THIS ARRAY ISN'T THE RIGHT SIZE!! " + (catName));
            return;
        }
        else {
            int prevLvl = 0;
            for (int a : upgradeLvl){
                if (a == 0) continue;
                if (a == prevLvl){
                    LOGGER.error("UPGRADES ARE INCORRECT " + (catName));
                    return;
                }
                if (a < prevLvl) {
                    LOGGER.error("UPGRADES ARE INCORRECT " + (catName));
                    return;
                }
                prevLvl = a;
                totalLvl++;
            }
        }

        UpgradeButton prevButton = null;
        for (int lvl : upgradeLvl) {
            if (lvl == 0) {
                createEmptyUpgrade(catName);
                continue;
            }
            ItemStack gearStack = mC.player.getMainHandItem();
            int playerTier = GearCap.getCap(gearStack).GetTier().ordinal();
            int upgradeTier = categories.get(catName).size() + 1;

            //Make the requirement
            UpgradeButton finalPrevButton = prevButton;
            UpgradeButton.Irequirement iRequire = (button) -> {
                //Grabs the custom upgrades for the related item
                CompoundNBT customUpgrades = GearUpgrades.getUpgrades(gearCycle, getCap());
                PlayerEntity player = ClientUtil.mC.player;

                //Make sure they don't have this enchant already
                if (customUpgrades.contains(upgradeName) && customUpgrades.getInt(upgradeName) >= lvl) {
                    button.active = false;
                    button.purchased = true;
                    return ITextComponent.nullToEmpty("\247aPurchased");
                }
                //Make sure they bought the previous upgrade
                else if (finalPrevButton != null && finalPrevButton.purchased == false) {
                    button.active = false;
                    return ITextComponent.nullToEmpty("\247cYou must purchase\nthe previous upgrade");
                }
                //Make sure the gear capability is at this tier
                else if (playerTier < upgradeTier) {
                    button.active = false;
                    return ITextComponent.nullToEmpty("\247cYou must upgrade\nto tier " + (GearTier.values()[upgradeTier]));
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

            int price = getPrice(totalLvl, catName);

            //Make the pressable
            Button.IPressable iPress = (button) -> {
                //Place the lvl into the custom Upgrade Compount NBT with its upgradeName
                GearUpgrades.getUpgrades(gearCycle, getCap()).putInt(upgradeName, lvl);

                //Now finally make sure to sync these changes with the server
                NetworkHandler.INSTANCE.sendToServer(new SyncServerGearMsg(getCap().getTag(gearCycle), gearCycle, price));

                mC.player.giveExperiencePoints(-price);

                button.active = false;
            };

            prevButton = createUpgrade(catName, image, price, iRequire, iPress);
        }
    }

    public void createEnchantUpgrade(int cycleInt, Enchantment enchantment, int[] upgradeLvl, ResourceLocation image){
        String catName = new TranslationTextComponent(enchantment.getDescriptionId()).getString();

        if (categories.containsKey(catName)){
            LOGGER.error("DUPLICATE NAME FOUND " + catName);
            return;
        }
        else {categories.put(catName, new ArrayList<>());}
        
        //This is just to make sure that the upgrade lvl integers are all correct (also to get the total levels)
        int totalLvl = 0;
        if (upgradeLvl.length != 4){
            LOGGER.error("THIS ARRAY ISN'T THE RIGHT SIZE!! " + (catName));
            return;
        }
        else {
            int prevLvl = 0;
            for (int a : upgradeLvl){
                if (a == 0) continue;
                if (a == prevLvl){
                    LOGGER.error("UPGRADES ARE INCORRECT " + (catName));
                    return;
                }
                if (a < prevLvl) {
                    LOGGER.error("UPGRADES ARE INCORRECT " + (catName));
                    return;
                }
                prevLvl = a;
                totalLvl++;
            }
        }

        UpgradeButton prevButton = null;
        for (int lvl : upgradeLvl) {
            if (lvl == 0) {
                createEmptyUpgrade(catName);
                continue;
            }

            ItemStack gearStack = mC.player.getMainHandItem();
            int playerTier = GearCap.getCap(gearStack).GetTier().ordinal();
            int upgradeTier = categories.get(catName).size() + 1;

            //Make the requirement
            UpgradeButton finalPrevButton = prevButton;
            UpgradeButton.Irequirement iRequire = (button) -> {
                //Grabs the enchantments for the related item
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.deserializeEnchantments(getCap().getTag(cycleInt).getList("Enchantments", 10));
                PlayerEntity player = ClientUtil.mC.player;

                //Make sure they don't have this enchant already
                if (enchantments.containsKey(enchantment) && enchantments.get(enchantment) >= lvl) {
                    button.active = false;
                    button.purchased = true;
                    return ITextComponent.nullToEmpty("\247aPurchased");
                }
                //Make sure they bought the previous upgrade
                else if (finalPrevButton != null && finalPrevButton.purchased == false) {
                    button.active = false;
                    return ITextComponent.nullToEmpty("\247cYou must purchase\nthe previous upgrade");
                }
                //Make sure the gear capability is at this tier
                else if (playerTier < upgradeTier) {
                    button.active = false;
                    return ITextComponent.nullToEmpty("\247cYou must upgrade\nto tier " + (GearTier.values()[upgradeTier]));
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

            int price = getPrice(totalLvl, catName);

            //Make the pressable
            Button.IPressable iPress = (button) -> {
                //Grabs the enchantments for the related item
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.deserializeEnchantments(getCap().getTag(cycleInt).getList("Enchantments", 10));
                
                //Add this enchantment to their map
                enchantments.put(enchantment, lvl);
                //Place the updated map back into the gearStack
                setEnchantments(enchantments, getCap().getTag(cycleInt));

                //Now finally make sure to sync these changes with the server
                NetworkHandler.INSTANCE.sendToServer(new SyncServerGearMsg(getCap().getTag(cycleInt), cycleInt, price));

                mC.player.giveExperiencePoints(-price);

                button.active = false;
            };
            
            //Grab the button just made to use in the next iteration
            prevButton = createUpgrade(catName, image, price, iRequire, iPress);
        }
    }
    
    protected GearCap getCap(){
        ItemStack gearStack = ClientUtil.mC.player.getMainHandItem();

        return GearCap.getCap(gearStack);
    }
    public UpgradeButton createUpgrade(String category, ResourceLocation image, int price, UpgradeButton.Irequirement require, Button.IPressable purchaseFunc){
        if (!categories.containsKey(category)) categories.put(category, new ArrayList<>());

        int index = 0;
        for (UpgradeButton button : categories.get(category)){
            if (button != null) index++;
        }

       UpgradeButton button = new UpgradeButton(0,0, buttonSize, buttonSize,
               category + " " + (index + 1), image, this.bounds, price,require, purchaseFunc);

       addButton(button);

       categories.get(category).add(button);

       UpdatePositions();

       return button;
    }
}
