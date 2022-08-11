package invoker54.arsgears.event;

import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.api.mana.IMana;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.capability.player.PlayerDataCap;
import invoker54.arsgears.item.GearUpgrades;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.item.utilgear.UtilGearItem;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import static invoker54.arsgears.item.combatgear.CombatGearItem.swordINT;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID)
public class CombatGearEvents {
    private static final Logger LOGGER = LogManager.getLogger();
    @SubscribeEvent
    public static void checkCombatGear(TickEvent.PlayerTickEvent event){
        //make sure it's server side
        if(event.side == LogicalSide.CLIENT) return;
        //Make sure it's the last phase (only needs to run once)
        //if(event.phase == TickEvent.Phase.END) return;

        //I need the player
        PlayerEntity player = event.player;
        //The player capability
        PlayerDataCap cap = PlayerDataCap.getCap(player);
        //the tracked item in the capability
        ItemStack trackedGear = cap.getCombatGear();
        //Check hands for combat gear
        ItemStack focusedGear = ArsUtil.getHeldItem(player, CombatGearItem.class);

        //make sure we have a focused gear
        if (focusedGear.isEmpty()) return;

        //If the trackedGear and focusedGear don't match, set focusedGear to be the new trackedGear
        if (trackedGear != focusedGear) {
            //LOGGER.info("THEY WERENT THE SAME");
            ArsUtil.replaceItemStack(player, focusedGear, cap.getCombatGear());
        }
        //Finally, sync the data between the copy and the trackedGear
        cap.syncCombatGearData();
    }

    @SubscribeEvent
    public static void hitCombatGear(LivingDamageEvent event){
        //If it is not a player, return
        if (!(event.getSource().getEntity() instanceof PlayerEntity)) return;

        PlayerEntity player = (PlayerEntity) event.getSource().getEntity();

        //If the player isn't holding a combat gear, return
        ItemStack gearStack = player.getMainHandItem();
        if (!(gearStack.getItem() instanceof CombatGearItem)) return;

        CombatGearCap cap = CombatGearCap.getCap(gearStack);

        //If it's the sword selected, then add some damage
        if (cap.getSelectedItem() == swordINT && !cap.isSweep){
            event.setAmount(((CombatGearItem)gearStack.getItem()).modSword.getDamage());
            int manaStealLvl = GearUpgrades.getUpgrade(swordINT, cap, GearUpgrades.swordManaSteal);
            if (manaStealLvl != 0){
                LOGGER.debug("MANA STOLEN " + (event.getAmount() * manaStealLvl));
                ManaCapability.getMana(player).ifPresent((mana) -> mana.addMana(event.getAmount() * manaStealLvl));
            }
        }
    }

    //region Failed attempt at switching out attributes
//    @SubscribeEvent
//    public static void onAttributeGrab(ItemAttributeModifierEvent event){
//        Item item = event.getItemStack().getItem();
//
//        //If it isn't a combat GEAR, retun
//        if (!(item instanceof CombatGearItem)) return;
//
//        CombatGearCap cap = CombatGearCap.getCap(event.getItemStack());
//
//        //If the selected mode isn't sword, return
//        if (cap.getSelectedItem() != CombatGearItem.swordINT) return;
//
//        //Grab the base damage modifier, and replace it with the swords base damage modifier
//        AttributeModifier modifier = event.getItemStack().getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE).stream().findFirst().get();
//        event.removeModifier(Attributes.ATTACK_DAMAGE, modifier);
//
//        event.addModifier(Attributes.ATTACK_DAMAGE,
//                ((CombatGearItem)item).modSword.getAttributeModifiers(EquipmentSlotType.MAINHAND, event.getItemStack()).get(Attributes.ATTACK_DAMAGE)
//                        .stream().findFirst().get());
//    }
    //endregion
}
