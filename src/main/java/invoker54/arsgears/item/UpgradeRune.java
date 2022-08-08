package invoker54.arsgears.item;

import com.hollingsworth.arsnouveau.common.util.PortUtil;
import invoker54.arsgears.capability.player.PlayerDataCap;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpgradeRune extends Item {
    private static final Logger LOGGER = LogManager.getLogger();
    ToolItem gearType;

    public UpgradeRune(Item gearType, Properties builder) {
        super(builder);
        this.gearType = (ToolItem) gearType;
    }

    public int getTier(){
        return ((GearTier)gearType.getTier()).ordinal();
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack runeStack = playerIn.getItemInHand(handIn);
        PlayerDataCap cap = PlayerDataCap.getCap(playerIn);
        int playerTier;
        if (gearType instanceof UtilGearItem){ playerTier = ((UtilGearItem)cap.getUtilityGear().getItem()).getTier().ordinal(); }
        else { playerTier = ((CombatGearItem)cap.getCombatGear().getItem()).getTier().ordinal(); }


        if (worldIn.isClientSide()) return ActionResult.consume(runeStack);

        //Check if it's utility or combat upgrade rune
        //Make sure the player used the previous one already
        if (playerTier < getTier() - 1) {
            PortUtil.sendMessage(playerIn, new TranslationTextComponent("ars_gears.chat.cant_upgrade_low"));
        }
        //Also make sure the player tier isn't higher, or the same level
        else if (playerTier >= getTier()) {
            PortUtil.sendMessage(playerIn, new TranslationTextComponent("ars_gears.chat.cant_upgrade_high"));
        }
        //Now let's get to upgrading!
        else {
            //Grab the previous stack nbt data
            CompoundNBT cNBT;
            if (gearType instanceof UtilGearItem) cNBT = cap.getUtilityGear().serializeNBT();
            else cNBT = cap.getCombatGear().serializeNBT();

            LOGGER.warn("SO IS THIS THE UTILGEARITEM? " + (gearType instanceof UtilGearItem));
            LOGGER.warn("HEY THIS IS NEW!!!!");

            //Replace the id string with the upgrade item
            cNBT.putString("id", ForgeRegistries.ITEMS.getKey(gearType).toString());
            //Now update the gear

            if (gearType instanceof UtilGearItem) cap.upgradeUtilityGear(ItemStack.of(cNBT));
            else cap.upgradeCombatGear(ItemStack.of(cNBT));

            //Finally, shrink the itemStack and send the upgrade message
            PortUtil.sendMessage(playerIn, new TranslationTextComponent("ars_gears.chat.upgrade_success").append(String.valueOf(getTier() + 1)));
            runeStack.shrink(1);
        }

            return ActionResult.consume(runeStack);
    }
}
