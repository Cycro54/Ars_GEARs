package invoker54.arsgears.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.EnchantersMirror;
import com.hollingsworth.arsnouveau.common.items.EnchantersSword;
import com.hollingsworth.arsnouveau.common.items.Wand;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.config.ArsGearsConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID)
public class NonGearToolEvents {
    private static final Logger LOGGER = LogManager.getLogger();
    @SubscribeEvent
    public static void rightClick(PlayerInteractEvent.RightClickBlock event){
        PlayerEntity player = event.getPlayer();

        if (!canUseItem(event.getItemStack())){
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

        if (!canUseItem(event.getItemStack())){
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

        if (!canUseItem(event.getItemStack())){
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

        if (!canUseItem(attackStack)){
            event.setCanceled(true);
            if (event.getPlayer().level.isClientSide) return;

            PortUtil.sendMessageNoSpam(player, new TranslationTextComponent("ars_gears.chat.use_other_tool"));
            if (Math.random() < 0.05f) {
                PortUtil.sendMessage(player, new TranslationTextComponent("ars_gears.chat.use_other_tool_jealous"));
            }
        }
    }

    public static boolean canUseItem(ItemStack itemStack){
        Item item = itemStack.getItem();

        //This will tell me if the item is a gear item or not
        if (GearCap.getCap(itemStack) != null) return true;

        String nameSpace = item.getRegistryName().getNamespace();
        //This will tell me if it's a Minecraft or Ars Nouveau item
//        LOGGER.info("Does name space equal minecraft: " + nameSpace.equalsIgnoreCase("minecraft"));
        if (!nameSpace.equalsIgnoreCase("minecraft") && !nameSpace.equalsIgnoreCase(ArsNouveau.MODID)) return true;

        //This is for Utility items
        if (item instanceof TieredItem) {
            IItemTier tier = ((TieredItem) itemStack.getItem()).getTier();
            if (tier == ItemTier.STONE || tier == ItemTier.WOOD) return true;

            if (item instanceof PickaxeItem) return ArsGearsConfig.useUtilityItems;
            if (item instanceof HoeItem) return ArsGearsConfig.useUtilityItems;
            if (item instanceof AxeItem) return ArsGearsConfig.useUtilityItems;
            if (item instanceof ShovelItem) return ArsGearsConfig.useUtilityItems;

            //Except this one, this is for Minecraft swords
            if (item instanceof SwordItem) return ArsGearsConfig.useCombatItems;
        }
        if (item instanceof FishingRodItem) return ArsGearsConfig.useUtilityItems;

        //This is for combat items
        if (item instanceof BowItem) return ArsGearsConfig.useCombatItems;
        if (item instanceof EnchantersMirror) return ArsGearsConfig.useCombatItems;
        if (item instanceof Wand) return ArsGearsConfig.useCombatItems;
        if (item instanceof EnchantersSword) return ArsGearsConfig.useCombatItems;

        return true;
    }
}
