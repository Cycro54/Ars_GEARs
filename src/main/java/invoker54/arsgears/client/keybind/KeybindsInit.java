package invoker54.arsgears.client.keybind;

import com.hollingsworth.arsnouveau.client.keybindings.ModKeyBindings;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.client.gui.ModGuiRadialMenu;
import invoker54.arsgears.client.gui.ModGuiSpellBook;
import invoker54.arsgears.item.FakeSpellBook;
import invoker54.arsgears.item.GearTier;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.network.NetworkHandler;
import invoker54.arsgears.network.message.CycleGearMsg;
import invoker54.arsgears.network.message.OpenGearContainerMsg;
import invoker54.arsgears.network.message.QuickCastMsg;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = ArsGears.MOD_ID)
public class KeybindsInit {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final ArrayList<CustomKeybind> gearBinds = new ArrayList<>();

    //General Keybinds
    public static CustomKeybind gearInventory;
    public static CustomKeybind cycleGear;

    //Combat keybinds
    public static CustomKeybind openSpell_combat;
    public static CustomKeybind spellSelect_combat;

    public static CustomKeybind quick_cast;

    public static void registerKeys(){
        //Cycle selected item for combar and utility gear
        cycleGear = addBind(new CustomKeybind("cycle_gear", GLFW.GLFW_KEY_R, (action) ->{
            if(action != GLFW.GLFW_PRESS) return;

            if(ClientUtil.mC.screen != null) return;

            ItemStack item = ClientUtil.mC.player.getMainHandItem();
            GearCap cap = GearCap.getCap(item);
            if (cap == null && !(item.getItem() instanceof FakeSpellBook)) return;

            NetworkHandler.INSTANCE.sendToServer(new CycleGearMsg());
        }));

        //Open spell book screen to configure spell
        openSpell_combat = addBind(new CustomKeybind(ModKeyBindings.OPEN_BOOK, (action -> {
            if(action != GLFW.GLFW_PRESS) return;
            if (ClientUtil.mC.screen != null) return;

            ItemStack itemStack = ClientUtil.mC.player.getMainHandItem();
            CombatGearCap cap = CombatGearCap.getCap(itemStack);
            if (cap == null) return;

            if (ClientUtil.mC.screen instanceof ModGuiSpellBook) {
                    ClientUtil.mC.setScreen(null);
                    return;
            }
            //Make sure the player is GearTier Iron or higher
            if (cap.getTier().ordinal() < GearTier.IRON.ordinal()) {
                ClientUtil.mC.player.sendMessage(new TranslationTextComponent("ars_gears.chat.cant_use_spells"), Util.NIL_UUID);
                return;
            }

            //Make sure there are no active cooldowns on the current weapon
            for (int a = 0; a < cap.getTier().ordinal() - 1; a++){
                CompoundNBT tag = cap.getTag(cap.getSelectedItem());

                if (CombatGearItem.getCooldown(ClientUtil.mC.player, tag, a, true) > 0){
                    ClientUtil.mC.player.sendMessage(new TranslationTextComponent("ars_gears.chat.cant_craft_spells"), Util.NIL_UUID);
                    return;
                }
            }

            ModGuiSpellBook.open(itemStack);
        })));

        //Open spell select screen
        spellSelect_combat = addBind(new CustomKeybind(ModKeyBindings.OPEN_SPELL_SELECTION, (action -> {
            if (action != GLFW.GLFW_PRESS) return;

            if (ClientUtil.mC.screen instanceof ModGuiRadialMenu){
                ClientUtil.mC.setScreen(null);
                return;
            }

            ItemStack gearStack = ClientUtil.mC.player.getMainHandItem();
            CombatGearCap cap = CombatGearCap.getCap(gearStack);
            if (cap == null) return;

            if (cap.getTier().ordinal() < GearTier.IRON.ordinal()) {
                ClientUtil.mC.player.sendMessage(new TranslationTextComponent("ars_gears.chat.cant_use_spells"), Util.NIL_UUID);
                return;
            }

            if (ClientUtil.mC.screen == null){
                ClientUtil.mC.setScreen(new ModGuiRadialMenu(gearStack));
            }
        })));

        gearInventory = addBind(new CustomKeybind("gear_inventory", GLFW.GLFW_KEY_K, (action -> {
            if (action != GLFW.GLFW_PRESS) return;

            if (ClientUtil.mC.screen != null) return;

            ItemStack item = ClientUtil.mC.player.getMainHandItem();
            GearCap cap = GearCap.getCap(item);
            if (cap == null) return;

            NetworkHandler.INSTANCE.sendToServer(new OpenGearContainerMsg());
        })));

        quick_cast = addBind(new CustomKeybind("quick_cast", GLFW.GLFW_KEY_G, (action -> {
            if(action != GLFW.GLFW_PRESS) return;

            if(ClientUtil.mC.screen != null) return;

            ItemStack item = ArsUtil.getHeldGearCap(ClientUtil.mC.player, false, true);
            CombatGearCap cap = CombatGearCap.getCap(item);
            if (cap == null) return;

            NetworkHandler.INSTANCE.sendToServer(new QuickCastMsg());
        })));
    }

    public static CustomKeybind addBind(CustomKeybind keybind){
        gearBinds.add(keybind);
        return keybind;
    }
}
