package invoker54.arsgears.client.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.client.gui.CircleRender;
import invoker54.arsgears.item.GearUpgrades;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.item.combatgear.ModSpellMirror;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

import static invoker54.arsgears.item.combatgear.CombatGearItem.mirrorInt;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsGears.MOD_ID)
public class DrawGuiEvent {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final modGuiSpellHUD modSpellHUD = new modGuiSpellHUD();
    private static final ResourceLocation spell_slot_fill = new ResourceLocation("ars_gears", "textures/gui/quick_cast_slot_fill.png");
    private static final ResourceLocation spell_slot_frame = new ResourceLocation("ars_gears", "textures/gui/quick_cast_slot_frame.png");
    private static int colorRed = new Color(255, 77, 77,255).getRGB();
    private static int colorGreen = new Color(75, 232, 82,255).getRGB();
    private static int transparentGreyColor = new Color(91, 91, 91, 187).getRGB();
    private static int greyColor = new Color(194, 194, 194, 236).getRGB();
    private static int blueColor = new Color(0, 140, 255, 255).getRGB();


    @SubscribeEvent
    public static void renderSpellHUD(final RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        modSpellHUD.drawHUD(event.getMatrixStack());
    }

    @SubscribeEvent
    public static void renderHudInfo(final RenderGameOverlayEvent.Post event){
        if (ClientUtil.mC.screen != null) return;
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        PlayerEntity player = ClientUtil.mC.player;
        ItemStack gearStack = ArsUtil.getHeldGearCap(player, false, false);

        //If the player isn't holding the combat gear, return
        if (gearStack.isEmpty()) return;

        int moveAmount = 50;
        int x = (event.getWindow().getGuiScaledWidth()/2) - moveAmount - 15;
        int y = event.getWindow().getGuiScaledHeight()/2 - moveAmount;
        MatrixStack stack = event.getMatrixStack();

        Spell spell = CombatGearItem.SpellM.getCurrentRecipe(gearStack);
        int gearCycle = GearCap.getCap(gearStack).getSelectedItem();

        renderCircle(event.getMatrixStack(), x, y, 12, spell, gearStack.getOrCreateTag(), gearStack, gearCycle, false);
    }

    @SubscribeEvent
    public static void renderQuickSlot(final RenderGameOverlayEvent.Post event){
        if (ClientUtil.mC.screen != null) return;
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;
        PlayerEntity player = ClientUtil.mC.player;

        ItemStack gearStack = ArsUtil.getHeldGearCap(player, false, true);
        if (gearStack.isEmpty()) return;
        CombatGearCap gearCap = CombatGearCap.getCap(gearStack);

        CompoundNBT itemTag;
        if (gearStack.getItem() instanceof ModSpellMirror){
            itemTag = gearStack.getOrCreateTag();
        }
        else{
            itemTag = gearCap.getTag(mirrorInt);
        }

        int quickLvl = GearUpgrades.getUpgrade(gearStack, GearUpgrades.mirrorQuickCast);
//        LOGGER.debug("DOES QUICK LEVEL EQUAL 0? " + (quickLvl == 0));
        //Make sure the player has the upgrade
        if (quickLvl == 0) return;
        //Now let's see if the player has a spell equipped
//        LOGGER.debug("DOES ITEM TAG HAVE MODE STRING? " + (itemTag.contains("mode")));
        if (!itemTag.contains("mode")) return;
        int mode = itemTag.getInt("mode");

        ResourceLocation spell_icon = spell_slot_fill;

        Spell spell = SpellBook.getRecipeFromTag(itemTag, mode);
        if (!spell.isEmpty() && spell.getSpellSize() > 1){
            spell_icon = new ResourceLocation(ArsNouveau.MODID, "textures/items/" + spell.recipe.get(1).getIcon());
        }
//        SpellResolver resolver = new SpellResolver(new SpellContext(spell, ClientUtil.mC.player));

        int x = 16;
        int y = event.getWindow().getGuiScaledHeight() - 32 - 16 - 16;
        MatrixStack stack = event.getMatrixStack();

        //First the spell icon
        ClientUtil.TEXTURE_MANAGER.bind(spell_icon);
        ClientUtil.blitImage(stack, x, 32, y, 32, 0, 16, 0, 16, 16);
        //Then the outline
        ClientUtil.TEXTURE_MANAGER.bind(spell_slot_frame);
        ClientUtil.blitImage(stack, x, 32, y, 32, 0, 16, 0, 16, 16);
        ClientUtil.TEXTURE_MANAGER.release(spell_slot_frame);

        //If the spell is on cooldown, or mana is low, render the mana circle
        renderCircle(stack, x + 16, y + 16, 16, spell, itemTag, gearStack, mirrorInt, true);
    }

    public static void renderCircle(MatrixStack stack, int x, int y, float radius, Spell spell, CompoundNBT itemTag, ItemStack gearStack, int gearCycle, boolean hide){
        PlayerEntity player = ClientUtil.mC.player;

        IMana cap = ManaCapability.getMana(player).resolve().get();
        int cost = CombatGearItem.SpellM.getInitialCost(spell, gearCycle, gearStack);
        //float costAngle = (180f * ((float) cost/cap.getMaxMana()));
        //Mana side
        float manaAngle = (float) (180 * (cap.getCurrentMana()/(float)cost));
        manaAngle = (cost > cap.getMaxMana()) ? 180 : manaAngle;
        manaAngle = MathHelper.clamp(manaAngle, 0, 180);
        boolean flag = cost <= cap.getCurrentMana();
        int color = flag ? colorGreen : colorRed;

        //Cooldown Side
        float maxCooldown = CombatGearItem.calcCooldown(gearCycle, spell, true);
//        LOGGER.debug("WHATS MAX COOLDOWN: " + maxCooldown);
        float currentCooldown = CombatGearItem.getCooldown(player, itemTag, SpellBook.getMode(itemTag), true);
//        LOGGER.debug("WHATS CURRENT COOLDOWN: " + currentCooldown);
        float cooldownAngle = 180 * (1 - (currentCooldown/maxCooldown));
//        LOGGER.debug("WHATS the angle going to be: " + cooldownAngle);
        if (maxCooldown == 0) cooldownAngle = 180;
        cooldownAngle =  MathHelper.clamp(cooldownAngle, 0, 180);
        int blackColor = new Color(19, 19, 19, 255).getRGB();

        if (currentCooldown <= 0 && (cap.getCurrentMana() >= cost || player.abilities.instabuild) && hide) return;

        //Rendering part
        RenderSystem.enableBlend();
        //Background
        CircleRender.drawArc(stack, x, y, radius, 0, 360, blackColor);
        //Color for if you can cast the spell or not
        CircleRender.drawArc(stack, x, y, radius - 1,  manaAngle, 0, color);
        //Cooldown
        CircleRender.drawArc(stack, x, y, radius - 1, 0, cooldownAngle, (cooldownAngle == 180 ? greyColor : transparentGreyColor));

        //The divider
        CircleRender.drawArcLine(stack, x, y, radius, 0, blueColor);
        CircleRender.drawArcLine(stack, x, y, radius, 180, blueColor);
        RenderSystem.disableBlend();
    }
}
