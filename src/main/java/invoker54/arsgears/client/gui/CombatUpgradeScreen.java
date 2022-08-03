package invoker54.arsgears.client.gui;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.capability.player.PlayerDataCap;
import invoker54.arsgears.client.ClientUtil;
import invoker54.arsgears.client.gui.button.UpgradeButton;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.network.NetworkHandler;
import invoker54.arsgears.network.message.SyncServerCombatGearMsg;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public class CombatUpgradeScreen extends UpgradeScreen{
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    protected void init() {
        LOGGER.debug("I AM OPENING THE COMBAT UPGRADE SCREEN");
        super.init();

        createSharpness();
    }

    @Override
    public void tick() {
        //If the player ends up dropping the gear at some point while this screen is on, close the screen
        if (ArsUtil.getHeldItem(ClientUtil.mC.player, CombatGearItem.class).isEmpty()) ClientUtil.mC.setScreen(null);
    }

    public void createSharpness(){
        String catName = "Sharpness";
        ResourceLocation image = new ResourceLocation(ArsGears.MOD_ID, "textures/gui/upgrade_screen/combat/sharpness.png");
        PlayerEntity player = ClientUtil.mC.player;
        ItemStack gearStack = ArsUtil.getHeldItem(ClientUtil.mC.player, CombatGearItem.class);
        Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(gearStack);

        createUpgrade(catName, image, 10, (button -> {

            //Make sure they don't have this enchant already
            if (enchants.containsKey(Enchantments.SHARPNESS)){
                button.active = false;
                return ITextComponent.nullToEmpty("Purchased");
            }
            //Make sure they can afford it
            else if (button.getPrice() > player.totalExperience) {
                button.active = false;
                return ITextComponent.nullToEmpty("\247cYou need: " + (button.getPrice() - player.totalExperience));
            }

            else {
                button.active = true;
                return ITextComponent.nullToEmpty("Purchase upgrade?");
            }
        }), (button) -> {
            //Grab the gear stacks current enchantments
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(gearStack);
            //Add this enchantment to their map
            enchantments.put(Enchantments.SHARPNESS, 1);
            //Place the updated map back into the gearStack
            EnchantmentHelper.setEnchantments(enchantments, gearStack);

            //Now finally make sure to sync these changes with the server
            NetworkHandler.INSTANCE.sendToServer(new SyncServerCombatGearMsg(gearStack.serializeNBT()));

            button.active = false;

        });

    }
}
