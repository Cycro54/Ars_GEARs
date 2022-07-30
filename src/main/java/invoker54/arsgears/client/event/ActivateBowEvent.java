package invoker54.arsgears.client.event;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.network.NetworkHandler;
import invoker54.arsgears.network.message.ActivateGearMsg;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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

        PlayerEntity player = ClientUtil.mC.player;
        ItemStack gearStack = ArsUtil.getHeldItem(player, CombatGearItem.class);

        if (gearStack.isEmpty()) return;

        CombatGearCap cap = CombatGearCap.getCap(gearStack);

        LOGGER.debug("THIS IS ACTUALLY RUNNING");

        Spell spell = CombatGearItem.SpellM.getCurrentRecipe(gearStack);
        spell.recipe.add(0, MethodProjectile.INSTANCE);
        boolean flag = new SpellResolver(new SpellContext(spell, player)).canCast(player);

        //1 is the bow, make sure the player is charging it too
        if (cap.getSelectedItem() == 1){
            //This will stop the bow from activating if the player doesn't have enough mana
            if (!cap.getActivated() && flag)
                NetworkHandler.INSTANCE.sendToServer(new ActivateGearMsg());
        }
    }

    /**
    This will only run if the player is using the bow in combat gear
     */
    @SubscribeEvent
    public static void changeFOV(FOVUpdateEvent event){
        float f = event.getFov();
        PlayerEntity player = event.getEntity();
        if (player.isUsingItem() && player.getUseItem().getItem() instanceof CombatGearItem) {
            int i = player.getTicksUsingItem();
            float f1 = (float)i / 20.0F;
            if (f1 > 1.0F) {
                f1 = 1.0F;
            } else {
                f1 = f1 * f1;
            }

            f *= 1.0F - f1 * 0.15F;
        }

        event.setNewfov(f);
    }
}
