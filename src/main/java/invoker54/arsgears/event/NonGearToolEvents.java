package invoker54.arsgears.event;

import com.hollingsworth.arsnouveau.common.items.EnchantersMirror;
import com.hollingsworth.arsnouveau.common.items.EnchantersSword;
import com.hollingsworth.arsnouveau.common.items.Wand;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.capability.gear.GearCap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID)
public class NonGearToolEvents {
    @SubscribeEvent
    public static void rightClick(PlayerInteractEvent.RightClickBlock event){
        PlayerEntity player = event.getPlayer();

        if (!IsValidItem(event.getItemStack())){
            event.setCanceled(true);
            if (event.getWorld().isClientSide) return;

            PortUtil.sendMessageNoSpam(player, new TranslationTextComponent("ars_gears.chat.use_other_tool"));
            if (Math.random() < 0.05f) {
                PortUtil.sendMessage(player, new TranslationTextComponent("ars_gears.chat.use_other_tool_jealous"));
            }
        }
    }

    @SubscribeEvent
    public static void leftClick(PlayerInteractEvent.LeftClickBlock event){
        PlayerEntity player = event.getPlayer();

        if (!IsValidItem(event.getItemStack())){
            event.setCanceled(true);
            if (event.getWorld().isClientSide) return;

            PortUtil.sendMessageNoSpam(player, new TranslationTextComponent("ars_gears.chat.use_other_tool"));
            if (Math.random() < 0.05f) {
                PortUtil.sendMessage(player, new TranslationTextComponent("ars_gears.chat.use_other_tool_jealous"));
            }
        }
    }

    @SubscribeEvent
    public static void useItem(PlayerInteractEvent.RightClickItem event){
        PlayerEntity player = event.getPlayer();

        if (!IsValidItem(event.getItemStack())){
            event.setCanceled(true);
            if (event.getWorld().isClientSide) return;

            PortUtil.sendMessageNoSpam(player, new TranslationTextComponent("ars_gears.chat.use_other_tool"));
            if (Math.random() < 0.05f) {
                PortUtil.sendMessage(player, new TranslationTextComponent("ars_gears.chat.use_other_tool_jealous"));
            }
        }
    }

    @SubscribeEvent
    public static void useItem(AttackEntityEvent event){
        PlayerEntity player = event.getPlayer();
        ItemStack attackStack = event.getPlayer().getMainHandItem();

        if (!IsValidItem(attackStack)){
            event.setCanceled(true);
            if (event.getPlayer().level.isClientSide) return;

            PortUtil.sendMessageNoSpam(player, new TranslationTextComponent("ars_gears.chat.use_other_tool"));
            if (Math.random() < 0.05f) {
                PortUtil.sendMessage(player, new TranslationTextComponent("ars_gears.chat.use_other_tool_jealous"));
            }
        }
    }

    public static boolean IsValidItem(ItemStack itemStack){
        Item item = itemStack.getItem();

        //This will tell me if the item is a gear item or not
        if (GearCap.getCap(itemStack) != null) return true;

        if (item instanceof ToolItem && (item instanceof PickaxeItem || item instanceof AxeItem || item instanceof ShovelItem
                || item instanceof HoeItem || item instanceof SwordItem)){
            //the player will be allowed to use tool items only at wood tier, if it isn't wood tier, then don't let em use it
            if (((ToolItem) itemStack.getItem()).getTier() != ItemTier.WOOD){
                //Small chance for this to be said
                return false;
            }
        }

        //Combat stuff (that aren't tools)
        if ((item instanceof BowItem) || (item instanceof EnchantersMirror) || (item instanceof Wand) || (item instanceof EnchantersSword)){
            return false;
        }

        //Utility stuff (that aren't tools)
        if (item instanceof FishingRodItem){
            return false;
        }

        return true;
    }
}
