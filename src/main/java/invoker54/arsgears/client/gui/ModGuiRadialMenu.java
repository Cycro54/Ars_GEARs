package invoker54.arsgears.client.gui;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.network.NetworkHandler;
import invoker54.arsgears.network.message.edited.PacketSetBookMode;
import net.minecraft.block.Blocks;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;


//Had to just copy the entire class, too many private variables
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsGears.MOD_ID)
public class ModGuiRadialMenu extends Screen {
    private static final float PRECISION = 5.0f;
    private static final Logger LOGGER = LogManager.getLogger();

    private boolean closing;
    private double startAnimation;
    private double totalTime;
    private double prevTick;
    private CompoundNBT tag;
    private int selectedItem;

    int numberOfSlices;
    int gearCycle;

    public ModGuiRadialMenu(ItemStack gearStack) {
        super(new StringTextComponent(""));
        this.tag = gearStack.getOrCreateTag();
        this.numberOfSlices = ((CombatGearItem)gearStack.getItem()).getTier().ordinal();
        this.gearCycle = CombatGearCap.getCap(gearStack).getSelectedItem();
        this.closing = false;
        this.minecraft = Minecraft.getInstance();
//        this.startAnimation = getMinecraft().level.getGameTime() + (double) getMinecraft().getFrameTime();
        this.startAnimation = getMinecraft().level.getGameTime() + ClientUtil.mC.getDeltaFrameTime();
        this.selectedItem = -1;
    }

    @SubscribeEvent
    public static void overlayEvent(RenderGameOverlayEvent.Pre event) {
        if (ClientUtil.mC.screen instanceof ModGuiRadialMenu) {
            if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
                event.setCanceled(true);
            }
        }
    }
    @SubscribeEvent
    public static void updateInputEvent(InputUpdateEvent event) {
        if (ClientUtil.mC.screen instanceof ModGuiRadialMenu) {
            GameSettings settings = Minecraft.getInstance().options;
            MovementInput eInput = event.getMovementInput();
            eInput.up = InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), settings.keyUp.getKey().getValue());
            eInput.down = InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), settings.keyDown.getKey().getValue());
            eInput.left = InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), settings.keyLeft.getKey().getValue());
            eInput.right = InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), settings.keyRight.getKey().getValue());

            eInput.forwardImpulse = eInput.up == eInput.down ? 0.0F : (eInput.up ? 1.0F : -1.0F);
            eInput.leftImpulse = eInput.left == eInput.right ? 0.0F : (eInput.left ? 1.0F : -1.0F);
            eInput.jumping = InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), settings.keyJump.getKey().getValue());
            eInput.shiftKeyDown = InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), settings.keyShift.getKey().getValue());
            if (Minecraft.getInstance().player.isMovingSlowly()) {
                eInput.leftImpulse = (float)((double)eInput.leftImpulse * 0.3D);
                eInput.forwardImpulse = (float)((double)eInput.forwardImpulse * 0.3D);
            }
        }
    }

    @Override
    public void render(MatrixStack ms,int mouseX, int mouseY, float partialTicks) {
        super.render(ms,mouseX, mouseY, partialTicks);
//        final float OPEN_ANIMATION_LENGTH = 2.5f;
        final float OPEN_ANIMATION_LENGTH = 0.25f;
//        double worldTime = ClientUtil.mC.level.getGameTime();
        float animationTime = (float) (startAnimation + totalTime);
        float openAnimation = closing ? (float) (1.0f - (totalTime / OPEN_ANIMATION_LENGTH)) : (float) (totalTime / OPEN_ANIMATION_LENGTH);
//        if (totalTime < 3) LOGGER.debug("WHATS the PARTIAL TICKS AT? " + ClientUtil.mC.getFrameTime());
//        if (totalTime < 3) LOGGER.debug("WHATS the DELTA FRAME AT? " + ClientUtil.mC.getDeltaFrameTime());

        //If the partialTick is on a new tick, make sure the equation is adjusted
        totalTime += (prevTick > partialTicks ? (partialTicks + 1 - prevTick) : partialTicks - prevTick)/20f;

        float animProgress = MathHelper.clamp(openAnimation, 0, 1);
        //This will make it so the animation is Quad ease out (smooth ending, fast beginning
        //animProgress = 1 - (1 - animProgress) * (1 - animProgress);
        animProgress = (float) (1 - Math.pow(1 - animProgress, 3));

        float radiusIn = Math.max(0.1f, 25 * animProgress);
        float radiusOut = (radiusIn + 60) * animProgress;
        float itemRadius = (radiusIn + radiusOut) * 0.5f;
        float animTop = (1 - animProgress) * height / 2.0f;
        int x = width / 2;
        int y = height / 2;

        //This number will be based on the tier the player is on, no longer 10
        //int numberOfSlices = 10;

        double a = Math.toDegrees(Math.atan2(mouseY - y, mouseX - x));
        double d = Math.sqrt(Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2));
        float s0 = (((0 - 0.5f) / (float) numberOfSlices) + 0.25f) * 360;
        if (a < s0) {
            a += 360;
        }

        RenderSystem.pushMatrix();
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        //RenderSystem.translated(0, animTop, 0);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        boolean hasMouseOver = false;
        int mousedOverSlot = -1;
        //Category mousedOverCategory = null;

        if (!closing) {
            selectedItem = -1;
            for (int i = 0; i < numberOfSlices; i++) {
                float s = (((i - 0.5f) / (float) numberOfSlices) + 0.25f) * 360;
                float e = (((i + 0.5f) / (float) numberOfSlices) + 0.25f) * 360;
                if (a >= s && a < e && d >= radiusIn && d < radiusOut) {
                    selectedItem = i;
                    break;
                }
            }
        }

        for (int i = 0; i < numberOfSlices; i++) {
            float s = (((i - 0.5f) / (float) numberOfSlices) + 0.25f) * 360;
            float e = (((i + 0.5f) / (float) numberOfSlices) + 0.25f) * 360;
            if (selectedItem == i) {
                drawSlice(buffer, x, y, 10, radiusIn, radiusOut, s, e, 63, 161, 191, 60);
                hasMouseOver = true;
                mousedOverSlot = selectedItem;
            }
            else
                drawSlice(buffer, x, y, 10, radiusIn, radiusOut, s, e, 0, 0, 0, 64);
        }

        tessellator.end();
        RenderSystem.enableTexture();

        if (hasMouseOver && mousedOverSlot != -1) {
            int adjusted =  (mousedOverSlot+ 6) % 10;
            adjusted = adjusted == 0 ? 10 : adjusted;
            drawCenteredString(ms,font, SpellBook.getSpellName(tag,  adjusted), width/2,(height - font.lineHeight) / 2,16777215);
        }

        RenderHelper.turnBackOn();
        RenderSystem.popMatrix();
        for(int i = 0; i< numberOfSlices; i++){
            ItemStack stack = new ItemStack(Blocks.DIRT);
            float angle1 = ((i / (float) numberOfSlices) - 0.25f) * 2 * (float) Math.PI;
            float posX = x - 8 + itemRadius * (float) Math.cos(angle1);
            float posY = y - 8 + itemRadius * (float) Math.sin(angle1);

            //Can't do cast type, cast method isn't applied till after the spell is cast
            String resourceIcon = "";
//            String castType = "";
            for(AbstractSpellPart p : SpellBook.getRecipeFromTag(tag, (3 * gearCycle)+1+i).recipe){
//                if(p instanceof AbstractCastMethod)
//                    castType = p.getIcon();

                if(p instanceof AbstractEffect){
                    resourceIcon = p.getIcon();
                    break;
                }
            }
            RenderSystem.disableRescaleNormal();
            RenderHelper.turnOff();
            RenderSystem.disableLighting();
            RenderSystem.disableDepthTest();
            if(!resourceIcon.isEmpty()) {
                GuiSpellBook.drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/items/" + resourceIcon),
                        (int) posX, (int) posY, 0, 0, 16, 16, 16, 16,ms);
                //Can't draw castType since now it only gets applied during cast.
//                GuiSpellBook.drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/items/" + castType),
//                        (int) posX +3 , (int) posY - 10, 0, 0, 10, 10, 10, 10,ms);
            }
            this.itemRenderer.renderGuiItemDecorations(font, stack, (int) posX + 5, (int) posY, String.valueOf(i + 1 + (3 * gearCycle)));

        }

        LOGGER.debug("THIS IS THE SELECTED ITEM: " + selectedItem);

//        if (mousedOverSlot != -1) {
//            int adjusted = (mousedOverSlot + 6) % 10;
//            adjusted = adjusted == 0 ? 10 : adjusted;
//            selectedItem = adjusted;
//        }

    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        int adjustedKey = key - 48;
        if(adjustedKey >= 0 && adjustedKey < 10){
            selectedItem = adjustedKey == 0 ? 10 : adjustedKey;
            mouseClicked(0,0,0);
            return true;
        }
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        if(this.selectedItem != -1){
            SpellBook.setMode(tag, selectedItem + (3 * gearCycle)+1);
            NetworkHandler.INSTANCE.sendToServer(new PacketSetBookMode(tag));
            minecraft.player.closeContainer();
        }
        return true;
    }

    private void drawSlice(
            BufferBuilder buffer, float x, float y, float z, float radiusIn, float radiusOut, float startAngle, float endAngle, int r, int g, int b, int a) {
        float angle = endAngle - startAngle;
        int sections = Math.max(1, MathHelper.ceil(angle / PRECISION));

        startAngle = (float) Math.toRadians(startAngle);
        endAngle = (float) Math.toRadians(endAngle);
        angle = endAngle - startAngle;

        for (int i = 0; i < sections; i++)
        {
            float angle1 = startAngle + (i / (float) sections) * angle;
            float angle2 = startAngle + ((i + 1) / (float) sections) * angle;

            float pos1InX = x + radiusIn * (float) Math.cos(angle1);
            float pos1InY = y + radiusIn * (float) Math.sin(angle1);
            float pos1OutX = x + radiusOut * (float) Math.cos(angle1);
            float pos1OutY = y + radiusOut * (float) Math.sin(angle1);
            float pos2OutX = x + radiusOut * (float) Math.cos(angle2);
            float pos2OutY = y + radiusOut * (float) Math.sin(angle2);
            float pos2InX = x + radiusIn * (float) Math.cos(angle2);
            float pos2InY = y + radiusIn * (float) Math.sin(angle2);

            buffer.vertex(pos1OutX, pos1OutY, z).color(r, g, b, a).endVertex();
            buffer.vertex(pos1InX, pos1InY, z).color(r, g, b, a).endVertex();
            buffer.vertex(pos2InX, pos2InY, z).color(r, g, b, a).endVertex();
            buffer.vertex(pos2OutX, pos2OutY, z).color(r, g, b, a).endVertex();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
