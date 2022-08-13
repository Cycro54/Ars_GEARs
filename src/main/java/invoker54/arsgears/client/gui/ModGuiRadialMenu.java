package invoker54.arsgears.client.gui;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.client.gui.book.GuiSpellBook;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.network.NetworkHandler;
import invoker54.arsgears.network.message.edited.PacketSetBookMode;
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
import net.minecraft.util.text.TextFormatting;
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
    private float totalTime;
    private float prevTick;
    private float extraTick;
    private CompoundNBT tag;
    private int selectedItem;

    int numberOfSlices;
    public ModGuiRadialMenu(ItemStack gearStack) {
        super(new StringTextComponent(""));
        this.tag = gearStack.getOrCreateTag();
        //This is minus 1 because the 2nd tier (which is 1 for ordinal) cannot cast spells either & you can only have a max of 3 spells
        this.numberOfSlices = CombatGearCap.getCap(gearStack).getTier().ordinal() - 1;
        this.closing = false;
        this.minecraft = Minecraft.getInstance();
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
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack,mouseX, mouseY, partialTicks);
        final float OPEN_ANIMATION_LENGTH = 0.5f;
        float openAnimation = closing ? 1.0f - totalTime / OPEN_ANIMATION_LENGTH : totalTime / OPEN_ANIMATION_LENGTH;
        float currTick = minecraft.getFrameTime();
        totalTime += (currTick + extraTick - prevTick)/20f;
        extraTick = 0;
        prevTick = currTick;


        float animProgress = MathHelper.clamp(openAnimation, 0, 1);
        //This will make it so the animation is Cubic ease out (fast beginning, smooth ending)
        animProgress = (float) (1 - Math.pow(1 - animProgress, 3));
        float radiusIn = Math.max(0.1f, 25 * animProgress);
        float radiusOut = (radiusIn + 60) * animProgress;
        float itemRadius = (radiusIn + radiusOut) * 0.5f;
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
                    selectedItem = i + 1;
                    break;
                }
            }
        }

        for (int i = 0; i < numberOfSlices; i++) {
            float s = (((i - 0.5f) / (float) numberOfSlices) + 0.25f) * 360;
            float e = (((i + 0.5f) / (float) numberOfSlices) + 0.25f) * 360;
            if (selectedItem == i + 1) {
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
            drawCenteredString(stack,font, SpellBook.getSpellName(tag,  adjusted), width/2,(height - font.lineHeight) / 2,16777215);
        }

        RenderHelper.turnBackOn();
        RenderSystem.popMatrix();
        for(int i = 0; i < numberOfSlices; ++i){
            //ItemStack stack = new ItemStack(Blocks.DIRT);
            double startAngle = Math.floor(((float)i/numberOfSlices) * 360f);
            startAngle = Math.toRadians(startAngle);
            float posX = (float) (x + (-itemRadius * Math.sin(startAngle)));
            float posY = (float) (y + (itemRadius * Math.cos(startAngle)));
//            float angle1 = ((i / (float) numberOfSlices) - 0.25f) * 2 * (float) Math.PI;
//            float posX = x - 8 + itemRadius * (float) Math.cos(angle1);
//            float posY = y - 8 + itemRadius * (float) Math.sin(angle1);

            //Can't do cast type, cast method isn't applied till after the spell is cast
            String resourceIcon = "";
//            String castType = "";
            for(AbstractSpellPart p : SpellBook.getRecipeFromTag(tag, i + 1).recipe){
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
                        (int) posX - 8, (int) posY - 8, 0, 0, 16, 16, 16, 16,stack);
                //Can't draw castType since now it only gets applied during cast.
//                GuiSpellBook.drawFromTexture(new ResourceLocation(ArsNouveau.MODID, "textures/items/" + castType),
//                        (int) posX +3 , (int) posY - 10, 0, 0, 10, 10, 10, 10,ms);
            }
            //this.itemRenderer.renderGuiItemDecorations(font, stack, (int) posX, (int) posY, String.valueOf(i + 1 + (3 * gearCycle)));
            font.drawShadow(stack, String.valueOf(i + 1), posX + 8, posY + 8, TextFormatting.WHITE.getColor());
        }

        //LOGGER.debug("THIS IS THE SELECTED ITEM: " + (selectedItem + 1));

//        if (mousedOverSlot != -1) {
//            int adjusted = (mousedOverSlot + 6) % numberOfSlices;
//            adjusted = adjusted == 0 ? numberOfSlices : adjusted;
//            selectedItem = adjusted;
//        }

    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        int adjustedKey = key - 48;
        if(adjustedKey >= 0 && adjustedKey < numberOfSlices){
            selectedItem = adjustedKey == 0 ? numberOfSlices : adjustedKey;
            mouseClicked(0,0,0);
            return true;
        }
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
        if(this.selectedItem != -1){
            SpellBook.setMode(tag, selectedItem);
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
    public void tick() {
        if (totalTime != 0.5f){
            extraTick += 1;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
