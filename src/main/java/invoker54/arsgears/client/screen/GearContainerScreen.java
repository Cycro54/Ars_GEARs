package invoker54.arsgears.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.capability.player.PlayerDataCap;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import invoker54.arsgears.network.NetworkHandler;
import invoker54.arsgears.network.message.FeedGearMsg;
import invoker54.arsgears.network.message.SyncServerPlayerCapMsg;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;


@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsGears.MOD_ID)
public class GearContainerScreen extends ContainerScreen<GearContainer> {

    protected final ResourceLocation GEAR_LOCATION = new ResourceLocation(ArsGears.MOD_ID,"textures/gui/container/gear_inventory.png");
    //public static final ContainerType<MerchantContainer> MERCHANT = register("merchant", MerchantContainer::new);

    int halfWidthSpace;
    int halfHeightSpace;

//    //current durability + extra durability from food
//    int totalDurability;

    private PlayerDataCap playerCap;
    private ItemStack utilGear;

    public GearContainerScreen(GearContainer inst, PlayerInventory inv, ITextComponent title) {
        super(inst, inv, title);
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
        int buttonWidth = 54;
        int buttonHeight = 14;
        int buttonXPos = halfWidthSpace + (imageWidth - buttonWidth)/2;
        int buttonYPos = halfHeightSpace + (imageHeight - buttonHeight)/2;

        playerCap = PlayerDataCap.getCap(ClientUtil.mC.player);
        utilGear = ClientUtil.mC.player.getMainHandItem();
        if(!(utilGear.getItem() instanceof UtilGearItem)) utilGear = ClientUtil.mC.player.getOffhandItem();
        addButton(new ClientUtil.SimpleButton(buttonXPos, buttonYPos, buttonWidth, buttonHeight, ITextComponent.nullToEmpty("Feed"), (button) ->
        {
            //If the item isn't damaged, leave it be.
            if(utilGear.getDamageValue() == 0) return;

            //Grab the food stack
            ItemStack foodStack = menu.tempInv.getItem(0);

            //If the item in the gear slot isn't food, leave it be.
            if (!foodStack.isEdible()) return;

            //Set the damage value
            utilGear.setDamageValue(utilGear.getDamageValue() - menu.totalNeededFood);

            //Make sure the food stack shrinks
            foodStack.shrink(menu.totalNeededCount);

            //Finally, sync player cap with server
            NetworkHandler.INSTANCE.sendToServer(new FeedGearMsg(utilGear.getDamageValue(),foodStack.getCount()));
        }));
        //endregion

        //this.menu.tempInv.addListener((container) -> recalculateFood());
    }

    @Override
    protected void renderLabels(MatrixStack p_230451_1_, int p_230451_2_, int p_230451_3_) {
        this.font.draw(p_230451_1_, this.title, (float)this.titleLabelX, (float)this.titleLabelY, 4210752);


        double beforePercent = getDurabilityForDisplay(utilGear, 0);
        String foodText;
        if (1 - beforePercent <= 0.25f) {foodText = "Starving!";}
        else if (1 - beforePercent <= 0.5f) {foodText = "Hungry";}
        else if (1 - beforePercent <= 0.75f) {foodText = "Peckish";}
        else {foodText = "Full";}

        float textX = (imageWidth - font.width(foodText))/2f;

        this.font.drawShadow(p_230451_1_, "§l" + foodText, textX, 58, getRGBDurabilityForDisplay(beforePercent));

    }

    //Stole this method from IForgeItem (changed ItemStack param to int)
    private int getRGBDurabilityForDisplay (double percent){
        return 255 << 24|MathHelper.hsvToRgb(Math.max(0.0F, (float) (1.0F - percent)) / 3.0F, 1.0F, 1.0F);
    }

    //Stole this method from IForgeItem (Added int param)
    private double getDurabilityForDisplay(ItemStack stack, int extra)
    {
        return (MathHelper.clamp((double) stack.getDamageValue() - extra, 0, stack.getMaxDamage())) / (double) stack.getMaxDamage();
    }

    @Override
    protected void renderBg(MatrixStack stack, float partialTicks, int xMouse, int yMouse) {
        renderBackground(stack);

        double beforePercent = getDurabilityForDisplay(utilGear, 0);
        double afterPercent = getDurabilityForDisplay(utilGear, menu.totalNeededFood);

        //region render the durability bar
        //Render the background of the durability bar
        ClientUtil.blitColor(stack, halfWidthSpace + 22, 132, halfHeightSpace + 73, 11, Color.BLACK.getRGB());

        //Render the middleGround of the durability bar
        ClientUtil.blitColor(stack, halfWidthSpace + 22, (int) (132 * (1 - afterPercent)), halfHeightSpace + 73, 11, getRGBDurabilityForDisplay(afterPercent));

        //Render the foreGround of the durability bar
        ClientUtil.blitColor(stack, halfWidthSpace + 22, (int) (132 * (1 - beforePercent)), halfHeightSpace + 73, 11, getRGBDurabilityForDisplay(beforePercent));
        //endregion

        ClientUtil.TEXTURE_MANAGER.bind(GEAR_LOCATION);
        //Render the bg
        ClientUtil.blitImage(stack,halfWidthSpace,imageWidth,halfHeightSpace,imageHeight,0,imageWidth,0,imageHeight,256);

        //Render the button



//
//        drawCenteredString(stack,font,"Total",halfWidthSpace + (imageWidth/2),halfHeightSpace + 91, TextFormatting.WHITE.getColor());
//
//        renderExperienceBar(stack);
//
//        //region Render the flags next
//        ClientUtil.TEXTURE_MANAGER.bind(ShopScreen.SHOP_LOCATION);
//
//        //Render buy flag
//        ClientUtil.blitImage(stack,halfWidthSpace + 3, 14,halfHeightSpace + imageHeight - offsetY,21,162, 28, 177, 42,256);
//        //Render Sell flag
//        ClientUtil.blitImage(stack,halfWidthSpace + 3 + 14, 14,halfHeightSpace + imageHeight - offsetY,28,134, 28, 177, 56,256);
//        //endregion
//
//        //Now render green slots for sellable items
//        for (int a = 0; a < menu.slots.size(); a++){
//            Slot slot = menu.getSlot(a);
//
//            if (!slot.hasItem()) continue;
//
//            if (ShopData.sellEntries.containsKey(slot.getItem().getItem())){
//                ClientUtil.blitColor(stack,slot.x + halfWidthSpace, 16, slot.y + halfHeightSpace, 16, sellableColor);
//            }
//            else {
//                ClientUtil.blitColor(stack,slot.x + halfWidthSpace, 16, slot.y + halfHeightSpace, 16, unSellableColor);
//            }
//        }
    }

    @Override
    public void render(MatrixStack stack, int xMouse, int yMouse, float partialTicks) {
        super.render(stack, xMouse, yMouse, partialTicks);
        this.renderTooltip(stack,xMouse,yMouse);
    }
}
