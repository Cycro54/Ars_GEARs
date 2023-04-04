package invoker54.arsgears.event;

import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentExtract;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentFortune;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSplit;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.capability.player.PlayerDataCap;
import invoker54.arsgears.init.ItemInit;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

import static invoker54.arsgears.item.combatgear.CombatGearItem.swordINT;
import static invoker54.arsgears.item.utilgear.UtilGearItem.paxelINT;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID)
public class CombatGearEvents {
    private static final Logger LOGGER = LogManager.getLogger();
    @SubscribeEvent
    public static void checkCombatGear(TickEvent.PlayerTickEvent event){
        //make sure it's server side
        if(event.side == LogicalSide.CLIENT) return;
        //Make sure it's the last phase (only needs to run once)
        if(event.phase == TickEvent.Phase.END) return;
        //Make sure the player is alive
        if (!event.player.isAlive()) return;
        //I need the player
        PlayerEntity player = event.player;
        //The player capability
        PlayerDataCap cap = PlayerDataCap.getCap(player);
        if (cap == null) return;
        //the tracked item in the capability
        ItemStack trackedGear = cap.getCombatGear();
        //Check hands for combat gear, spell sword, bow, or mirror
        ItemStack focusedGear = player.getMainHandItem();
        CombatGearCap itemCap = CombatGearCap.getCap(focusedGear);
        if (itemCap == null) {
            focusedGear = player.getOffhandItem();
            itemCap = CombatGearCap.getCap(focusedGear);
        }

        //If it STILL equals null, that means there is not Combat Gear equipped
        if (itemCap == null) return;

        if (focusedGear.isEmpty()) return;

        //If the trackedGear and focusedGear don't match, set focusedGear to be the new trackedGear
        if (trackedGear != focusedGear) {
            ArsUtil.replaceItemStack(player, focusedGear, cap.getCombatGear());
        }
        //Finally, sync the data between the copy and the trackedGear
        cap.syncCombatGearData();
    }

    @SubscribeEvent
    public static void onDrop(ItemTossEvent event){
        ItemStack oldStack = event.getEntityItem().getItem();

        if (CombatGearCap.getCap(oldStack) == null) return;

        ItemStack newStack = new ItemStack(ItemInit.WOOD_COMBAT_GEAR);

        newStack.deserializeNBT(oldStack.serializeNBT());

        event.getEntityItem().setItem(newStack);
    }

    @SubscribeEvent
    public static void onCast(SpellCastEvent event){
        Spell spell = event.spell;

        if (event.getWorld().isClientSide) return;
        if (!(event.getEntityLiving() instanceof PlayerEntity)) return;
        if (((PlayerEntity) event.getEntityLiving()).isCreative()) return;
//        if (!(event.getEntityLiving().getMainHandItem().getItem() instanceof CombatGearItem)) return;

        PlayerDataCap playerCap = PlayerDataCap.getCap(event.getEntityLiving());
        if (playerCap == null) return;

        //First lets check for the fortune augment
        if (spell.recipe.contains(AugmentFortune.INSTANCE)){
            CombatGearCap gearCap = CombatGearCap.getCap(playerCap.getCombatGear());
            //Check how many fortune levels they have on the sword
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.deserializeEnchantments(gearCap.getTag(swordINT).getList("Enchantments", 10));
            if (enchantments.containsKey(Enchantments.MOB_LOOTING)) {
                int level = enchantments.get(Enchantments.MOB_LOOTING);
                int augmentCount = spell.getInstanceCount(AugmentFortune.INSTANCE);

                //If the mob looting level is less than the Fortune augment, cancel the event and tell the player
                if (level < augmentCount){
                    PortUtil.sendMessageNoSpam(event.getEntityLiving(), new TranslationTextComponent("ars_gears.chat.fortune_augment.mob_looting_low"));
                    event.setCanceled(true);
                    return;
                }
            }
        }

        //Next check for the extract augment
        if (spell.recipe.contains(AugmentExtract.INSTANCE)){
            GearCap gearCap = CombatGearCap.getCap(playerCap.getUtilityGear());
            //Check if they have the Silk Touch Enchantment
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.deserializeEnchantments(gearCap.getTag(paxelINT).getList("Enchantments", 10));
            if (!enchantments.containsKey(Enchantments.SILK_TOUCH)) {
                PortUtil.sendMessageNoSpam(event.getEntityLiving(), new TranslationTextComponent("ars_gears.chat.extract_augment.no_silk_touch"));
                event.setCanceled(true);
                return;
            }
        }

        //Finally, check for the split augment
        if (spell.recipe.contains(AugmentSplit.INSTANCE)){
            GearCap gearCap = CombatGearCap.getCap(playerCap.getCombatGear());
            if (gearCap.getTier().ordinal() - 1 < event.spell.getInstanceCount(AugmentSplit.INSTANCE)) {
                PortUtil.sendMessageNoSpam(event.getEntityLiving(), new TranslationTextComponent("ars_gears.chat.split_augment.gear_tier_low"));
                event.setCanceled(true);
                return;
            }
        }
    }
}
