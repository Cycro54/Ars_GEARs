package invoker54.arsgears.client.event;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.network.NetworkHandler;
import invoker54.arsgears.network.message.ActivateGearMsg;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

/**
 * This is used when the player has a combat gear and has it set to bow mode
 */
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsGears.MOD_ID)
public class ActivateBowEvent {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onAttackMouse(InputEvent.MouseInputEvent event){
        changeActiveState(event.getAction(), event.getButton());
    }

    @SubscribeEvent
    public static void onAttackKey(InputEvent.KeyInputEvent event){
        changeActiveState(event.getAction(), event.getKey());
    }

    public static void changeActiveState(int action, int key){
        if (ClientUtil.mC.level == null) return;

        if (ClientUtil.mC.screen != null) return;

        if (key != ClientUtil.mC.options.keyAttack.getKey().getValue()) return;

        if (action != GLFW.GLFW_PRESS) return;

        if (!ClientUtil.mC.options.keyUse.isDown()) return;

        PlayerEntity player = ClientUtil.mC.player;
        ItemStack gearStack = ArsUtil.getHeldGearCap(player, false);
        CombatGearCap cap = CombatGearCap.getCap(ArsUtil.getHeldGearCap(player, false));

        if (cap == null) return;

        //Make sure the bow is selected
        if (cap.getSelectedItem() != CombatGearItem.bowInt) return;

        Spell spell = CombatGearItem.SpellM.getCurrentRecipe(gearStack);

        //If the spell is empty, tell them they can't cast it
        if (spell.isEmpty()) {
            PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.spell.validation.adding.non_empty_spell"));
            return;
        }

        CompoundNBT itemTag = gearStack.getOrCreateTag();

        spell.recipe.add(0, MethodProjectile.INSTANCE);
        //This will stop the bow from activating if the player doesn't have enough mana
        boolean flag = new SpellResolver(new SpellContext(spell, player)).canCast(player);
        //This is if the spell has no glyphs after the Touch glyph
        boolean flag2 = spell.recipe.size() != 1;
        if (!flag2) PortUtil.sendMessage(player, new TranslationTextComponent("ars_nouveau.spell.validation.exists.non_empty_spell"));
        //This is if the item is still on cooldown
        boolean flag3 = CombatGearItem.getCooldown(player, itemTag, SpellBook.getMode(itemTag), true) <= 0;
        if (!flag3) PortUtil.sendMessage(player, new TranslationTextComponent("ars_gears.chat.cast_cooldown"));

        //1 is the bow, make sure the player is charging it too
        if (flag && flag2 && flag3) NetworkHandler.INSTANCE.sendToServer(new ActivateGearMsg());
    }

}
