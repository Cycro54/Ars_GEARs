package invoker54.arsgears.client.keybind;

import com.hollingsworth.arsnouveau.client.keybindings.ModKeyBindings;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.client.gui.modGuiSpellBook;
import invoker54.arsgears.item.GearTier;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import invoker54.arsgears.network.NetworkHandler;
import invoker54.arsgears.network.message.CycleGearMsg;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsGears.MOD_ID)
public class KeybindsInit {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ArrayList<CustomKeybind> gearBinds = new ArrayList<>();
    public static CustomKeybind cycleSelectedItem_utility;
    public static CustomKeybind configureSpell_combat;

    public static void registerKeys(FMLClientSetupEvent event){
        //Cycle selected item for combar and utility gear
        cycleSelectedItem_utility = new CustomKeybind("cycle_gear", GLFW.GLFW_KEY_GRAVE_ACCENT, (action) ->{
            if(action != GLFW.GLFW_PRESS) return;

            if(ClientUtil.mC.screen != null) return;

            ItemStack item = ClientUtil.mC.player.getMainHandItem();

            if(item.getItem() instanceof UtilGearItem
                    || item.getItem() instanceof CombatGearItem) {
                NetworkHandler.INSTANCE.sendToServer(new CycleGearMsg());
            }

        });
        gearBinds.add(cycleSelectedItem_utility);

        //Open spell book screen to configure spell
        configureSpell_combat = new CustomKeybind(ModKeyBindings.OPEN_BOOK, (action -> {
            if(action != GLFW.GLFW_PRESS) return;

            if (ClientUtil.mC.screen != null) return;

            ItemStack itemStack = ClientUtil.mC.player.getMainHandItem();
            if(itemStack.getItem() instanceof CombatGearItem) {
                if (ClientUtil.mC.screen instanceof modGuiSpellBook) {
                    ClientUtil.mC.setScreen(null);
                    return;
                }

                int tier = ((GearTier)((CombatGearItem) itemStack.getItem()).getTier()).ordinal();
                modGuiSpellBook.open(itemStack.getOrCreateTag(), tier, SpellBook.getUnlockedSpellString(itemStack.getOrCreateTag()));
            }
        }));
        gearBinds.add(configureSpell_combat);

    }
}
