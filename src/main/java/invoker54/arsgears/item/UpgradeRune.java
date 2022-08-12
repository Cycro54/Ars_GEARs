package invoker54.arsgears.item;

import com.hollingsworth.arsnouveau.common.util.PortUtil;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.player.PlayerDataCap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

    final GearTier gearTier;
    Item gear0;
    Item gear1;
    Item gear2;

    boolean forUtility;

    public UpgradeRune(GearTier gearTier, Item gear0, Item gear1, Item gear2, boolean forUtility, Properties builder) {
        super(builder);
        this.gearTier = gearTier;
        this.gear0 = gear0;
        this.gear1 = gear1;
        this.gear2 = gear2;
        this.forUtility = forUtility;
    }

    public int getTier(){
        return gearTier.ordinal();
    }

    @Override
    public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack runeStack = playerIn.getItemInHand(handIn);
        PlayerDataCap cap = PlayerDataCap.getCap(playerIn);
        GearCap gearCap;
        ItemStack gearStack;
        int playerTier;

        if (forUtility){
            gearCap = GearCap.getCap(cap.getUtilityGear());
            playerTier = gearCap.getTier().ordinal();
            gearStack = cap.getUtilityGear();
        }
        else {
            gearCap = GearCap.getCap(cap.getCombatGear());
            playerTier = gearCap.getTier().ordinal();
            gearStack = cap.getCombatGear();
        }

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
            CompoundNBT tag0 = gearCap.getTag(0);
            CompoundNBT tag1 = gearCap.getTag(1);
            CompoundNBT tag2 = gearCap.getTag(2);
            gearCap.setTier(gearTier);

            tag0.putString("id", ForgeRegistries.ITEMS.getKey(gear0).toString());
            tag1.putString("id", ForgeRegistries.ITEMS.getKey(gear1).toString());
            tag2.putString("id", ForgeRegistries.ITEMS.getKey(gear2).toString());
            //Make sure to rune the upgrade method, or else it may revert back to an old itemstack
            CompoundNBT currentItemTag = gearStack.serializeNBT();
            currentItemTag.merge(gearCap.getTag(gearCap.getSelectedItem()));


            //UTILITY
            if (forUtility){
                cap.upgradeUtilityGear(ItemStack.of(currentItemTag));
            }
            //COMBAT
            else {
                cap.upgradeCombatGear(ItemStack.of(currentItemTag));
            }

            //Finally, shrink the itemStack and send the upgrade message
            PortUtil.sendMessage(playerIn, new TranslationTextComponent("ars_gears.chat.upgrade_success").append(String.valueOf(getTier() + 1)));
            runeStack.shrink(1);
        }

            return ActionResult.consume(runeStack);
    }
}
