package invoker54.arsgears.event;


import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.config.ArsGearsConfig;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID)
public class SpellBookUseEvent {

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onUseSpellBook(PlayerInteractEvent.RightClickItem event) {
        ItemStack itemStack = event.getItemStack();
        CombatGearCap gearCap = CombatGearCap.getCap(event.getPlayer());
        CompoundNBT bookNBT = gearCap.getBookTag();
        CompoundNBT itemTag = itemStack.getOrCreateTag();

        //If this is happening on the client, just return.
        if (event.getSide() == LogicalSide.CLIENT) return;

        if (!(itemStack.getItem() instanceof SpellBook)) return;
        PlayerEntity player = event.getPlayer();

        //Can only use the spell book if you are crouching
        if (!ArsGearsConfig.useSpellbook && !player.isCrouching()) {
            player.sendMessage(new TranslationTextComponent("ars_gears.chat.use_spell_book"), Util.NIL_UUID);
            event.setCanceled(true);
        }


        //This will stop the player from casting if there is a cooldown
        if (ArsGearsConfig.disableSpellBookCooldown) return;
        if (player.isCreative()) return;

        if (CombatGearItem.getCooldown(player, bookNBT, SpellBook.getMode(itemTag), true) > 0) {
            PortUtil.sendMessageNoSpam(player, new TranslationTextComponent("ars_gears.chat.cast_cooldown"));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void checkCastEvent(SpellCastEvent event){
        if (!(event.getEntityLiving() instanceof PlayerEntity)) return;
        if (event.isCanceled()) return;
        if (ArsGearsConfig.disableSpellBookCooldown) return;

        PlayerEntity player = (PlayerEntity) event.getEntityLiving();
        ItemStack magicStack = ArsUtil.getHeldItem(player, SpellBook.class);
        CombatGearCap gearCap = CombatGearCap.getCap(player);
        CompoundNBT bookNBT = gearCap.getBookTag();

        if (magicStack == ItemStack.EMPTY) return;

        CompoundNBT itemTag = magicStack.getOrCreateTag();

        //Set the cooldown
        Spell spell = SpellBook.getRecipeFromTag(itemTag, SpellBook.getMode(itemTag));
        if (!new SpellResolver(new SpellContext(spell, player)).canCast(player)) return;

        float cooldown = CombatGearItem.calcCooldown(-1, spell, true) + player.level.getGameTime();
        //One for the bookNBT in your Combat Gear cap
        CombatGearItem.setCooldown(bookNBT, SpellBook.getMode(itemTag), cooldown);
        // One for the spell book itself!
        CombatGearItem.setCooldown(itemTag, SpellBook.getMode(itemTag), cooldown);
        gearCap.setBookTag(bookNBT);
    }
}
