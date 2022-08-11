package invoker54.arsgears.client.event;

import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.client.gui.CircleRender;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsGears.MOD_ID)
public class DrawGuiEvent {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final modGuiSpellHUD modSpellHUD = new modGuiSpellHUD();
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
        ItemStack gearStack = ArsUtil.getHeldItem(player, CombatGearItem.class);
        CompoundNBT itemTag = gearStack.getOrCreateTag();

        //If the player isn't holding the combat gear, return
        if (gearStack.isEmpty()) return;

        int moveAmount = 50;
        int x = (event.getWindow().getGuiScaledWidth()/2) - moveAmount - 15;
        int y = event.getWindow().getGuiScaledHeight()/2 - moveAmount;
        MatrixStack stack = event.getMatrixStack();

        Spell spell = CombatGearItem.SpellM.getCurrentRecipe(gearStack);
        spell.recipe.add(0, MethodTouch.INSTANCE);
        SpellResolver spellResolver = new SpellResolver(new SpellContext(spell, player));
        IMana cap = ManaCapability.getMana(player).resolve().get();
        int cost = CombatGearItem.SpellM.getInitialCost(spell);
        //float costAngle = (180f * ((float) cost/cap.getMaxMana()));
        //Mana side
        float manaAngle = (float) (180 * (cap.getCurrentMana()/(float)cost));
        manaAngle = (cost > cap.getMaxMana()) ? 180 : manaAngle;
        manaAngle = MathHelper.clamp(manaAngle, 0, 180);
        boolean flag = cost <= cap.getCurrentMana();
        int color = flag ? colorGreen : colorRed;

        //Cooldown Side
        float maxCooldown = CombatGearItem.calcCooldown(CombatGearItem.SpellM.getCurrentRecipe(gearStack), true);
        float currentCooldown = CombatGearItem.getCooldown(player, itemTag, SpellBook.getMode(itemTag), true);
        float cooldownAngle = 180 * (1 - (currentCooldown/maxCooldown));
        cooldownAngle =  MathHelper.clamp(cooldownAngle, 0, 180);
        int blackColor = new Color(19, 19, 19, 255).getRGB();

        //Rendering part
        RenderSystem.enableBlend();
        //Background
        CircleRender.drawArc(stack, x, y, 12, 0, 360, blackColor);
        //Color for if you can cast the spell or not
        CircleRender.drawArc(stack, x, y, 11,  manaAngle, 0, color);
        //Cooldown
        CircleRender.drawArc(stack, x, y, 11, 0, cooldownAngle, (cooldownAngle == 180 ? greyColor : transparentGreyColor));

//        //This is the threshold line for casting
//        CircleRender.drawArcLine(stack, x, y, 13, (180 - costAngle) + 180, colorRed);

        //The divider
        CircleRender.drawArcLine(stack, x, y, 13, 0, blueColor);
        CircleRender.drawArcLine(stack, x, y, 13, 180, blueColor);
        RenderSystem.disableBlend();
    }
}
