package invoker54.arsgears.client.gui.container;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.client.Ticker;
import invoker54.arsgears.init.SoundsInit;
import invoker54.arsgears.network.NetworkHandler;
import invoker54.arsgears.network.message.FeedGearMsg;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;


@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsGears.MOD_ID)
public class GearContainerScreen extends ContainerScreen<GearContainer> {

    protected final ResourceLocation GEAR_LOCATION = new ResourceLocation(ArsGears.MOD_ID,"textures/gui/container/gear_inventory.png");
    //public static final ContainerType<MerchantContainer> MERCHANT = register("merchant", MerchantContainer::new);

    int halfWidthSpace;
    int halfHeightSpace;
    double totalTime = 0;

//    //current durability + extra durability from food
//    int totalDurability;
    private ItemStack gearStack;

    private ClientUtil.SimpleButton eatButton;

    public GearContainerScreen(GearContainer inst, PlayerInventory inv, ITextComponent title) {
        super(inst, inv, title);
        this.passEvents = false;
        this.isQuickCrafting = false;
        this.imageWidth = 176;
        this.imageHeight = 200;
    }

    @Override
    protected void init() {
        super.init();

        halfWidthSpace = (this.width - imageWidth)/2;
        halfHeightSpace = (this.height - imageHeight)/2;

        titleLabelX = (imageWidth - font.width(title.getString()))/2;
        titleLabelY = 8;

        //region This will be the feed button
        int buttonWidth = 64;
        int buttonHeight = 22;
        int buttonXPos = halfWidthSpace + (imageWidth - buttonWidth)/2;
        int buttonYPos = halfHeightSpace + 91;

        gearStack = ClientUtil.mC.player.getMainHandItem();
        eatButton = addButton(new ClientUtil.SimpleButton(buttonXPos, buttonYPos, buttonWidth, buttonHeight, ITextComponent.nullToEmpty("Feed"), (button) ->
        {
            //If the item isn't damaged, leave it be.
            if(gearStack.getDamageValue() == 0) return;

            //Make sure the food array isn't empty (everything is set to 0)
            boolean empty = true;
            for (int a = 0; a < this.menu.foodToEat.length; a++){
                if (this.menu.foodToEat[a] != 0){
                    empty = false;
                    break;
                }
            }
            if (empty) return;

            //Play the munch sound
            ClientUtil.mC.player.playSound(SoundsInit.GEAR_EAT, 1,0.6F + ClientUtil.mC.player.getRandom().nextFloat() * 0.4F);

            //Set the damage value
            gearStack.setDamageValue(gearStack.getDamageValue() - menu.repairValue);

            boolean isCombat = GearCap.getCap(gearStack) instanceof CombatGearCap;

            //Finally, sync player cap with server
            NetworkHandler.INSTANCE.sendToServer(new FeedGearMsg(gearStack.getDamageValue(), this.menu.foodToEat, isCombat));
        }));
        //endregion

        //this.menu.tempInv.addListener((container) -> recalculateFood());
    }

    @Override
    public void tick() {
        eatButton.active = (this.menu.repairValue != 0);
    }

    @Override
    protected void renderLabels(MatrixStack stack, int p_230451_2_, int p_230451_3_) {
        this.font.draw(stack, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);

        double beforePercent = getDurabilityForDisplay(gearStack, 0);
        String foodText;
        if (beforePercent <= 0.25f) {foodText = "Starving!";}
        else if (beforePercent <= 0.5f) {foodText = "Hungry";}
        else if (beforePercent <= 0.80f) {foodText = "Peckish";}
        else if (beforePercent <= 0.99f) {foodText = "Almost Full";}
        else {foodText = "Full";}

        float textX = (imageWidth - font.width(foodText))/2f;
        this.font.drawShadow(stack, foodText, textX - 1, 57, getRGBDurabilityForDisplay(beforePercent));

        String durability = "(" + ((gearStack.getMaxDamage() - gearStack.getDamageValue()) + menu.repairValue) + "/" + (gearStack.getMaxDamage()) + ")";
        textX = (imageWidth - font.width(durability))/2f;
        this.font.drawShadow(stack, durability, textX, 69, TextFormatting.WHITE.getColor());
    }

    //Stole this method from IForgeItem (changed ItemStack param to int)
    private int getRGBDurabilityForDisplay (double percent){
        return 255 << 24|MathHelper.hsvToRgb(Math.max(0.0F, (float) (percent)) / 3.0F, 1.0F, 1.0F);
    }

    //Stole this method from IForgeItem (Added int param)
    private double getDurabilityForDisplay(ItemStack stack, int extra)
    {
        return (MathHelper.clamp((double) (gearStack.getMaxDamage() - gearStack.getDamageValue()) + extra, 0, stack.getMaxDamage())) / (double) stack.getMaxDamage();
    }

    @Override
    protected void renderBg(MatrixStack stack, float partialTicks, int xMouse, int yMouse) {
        renderBackground(stack);

        ClientUtil.TEXTURE_MANAGER.bind(GEAR_LOCATION);
        //Render the bg
        ClientUtil.blitImage(stack,halfWidthSpace,imageWidth,halfHeightSpace,imageHeight,0,imageWidth,0,imageHeight,256);
        ClientUtil.TEXTURE_MANAGER.release(GEAR_LOCATION);

        double beforePercent = getDurabilityForDisplay(gearStack, 0);
        double afterPercent = getDurabilityForDisplay(gearStack, menu.repairValue);

        totalTime += Ticker.getDelta(false, false) * 5;
        double animationPercent = MathHelper.lerp ((Math.sin(totalTime) + 1)/2F, 0.25f, 1);
        int greyColor = new Color(107, 107, 107, (int) (255 * animationPercent)).getRGB();
        RenderSystem.enableBlend();
        //region render the durability bar
        //Render the background of the durability bar
        ClientUtil.blitColor(stack, halfWidthSpace + 22, 132, halfHeightSpace + 77, 11, Color.BLACK.getRGB());

        //Render the middleGround of the durability bar
        ClientUtil.blitColor(stack, halfWidthSpace + 23, (int) (130 * afterPercent), halfHeightSpace + 78, 9, greyColor);

        //Render the foreGround of the durability bar
        ClientUtil.blitColor(stack, halfWidthSpace + 23, (int) (130 * beforePercent), halfHeightSpace + 78, 9, getRGBDurabilityForDisplay(beforePercent));
        //endregion
        RenderSystem.disableBlend();
    }

    @Override
    public void render(MatrixStack stack, int xMouse, int yMouse, float partialTicks) {
        super.render(stack, xMouse, yMouse, partialTicks);
        this.renderTooltip(stack,xMouse,yMouse);
    }
}
