package invoker54.arsgears.event;


import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.event.SpellResolveEvent;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.util.MathUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.block.tile.IntangibleAirTile;
import com.hollingsworth.arsnouveau.common.block.tile.PhantomBlockTile;
import com.hollingsworth.arsnouveau.common.block.tile.ScribesTile;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOpenSpellBook;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import invoker54.arsgears.ArsGears;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.capability.player.PlayerDataCap;
import invoker54.arsgears.config.ArsGearsConfig;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static invoker54.arsgears.item.combatgear.CombatGearItem.SpellM.getCurrentRecipe;

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
        //Set the cooldown after doing a quick check
        else if (canCast(player, itemStack)){
            Spell spell = SpellBook.getRecipeFromTag(itemTag, SpellBook.getMode(itemTag));
            if (!new SpellResolver(new SpellContext(spell, player)).canCast(player)) return;

            float cooldown = CombatGearItem.calcCooldown(-1, spell, true) + event.getEntityLiving().level.getGameTime();
            //One for the bookNBT in your Combat Gear cap
            CombatGearItem.setCooldown(bookNBT, SpellBook.getMode(itemTag), cooldown);
            // One for the spell book itself!
            CombatGearItem.setCooldown(itemTag, SpellBook.getMode(itemTag), cooldown);
            gearCap.setBookTag(bookNBT);
        }
    }


    //Code ripped from the SpellBook code, tis messy yes.
    public static boolean canCast(PlayerEntity playerIn, ItemStack stack){
        World worldIn = playerIn.level;

        if(!stack.hasTag())
            return false;

        SpellResolver resolver = new SpellResolver(new SpellContext(getCurrentRecipe(stack), playerIn));
        boolean isSensitive = resolver.spell.getBuffsAtIndex(0, playerIn, AugmentSensitive.INSTANCE) > 0;
        RayTraceResult result = playerIn.pick(5, 0, isSensitive);
        if(result instanceof BlockRayTraceResult && worldIn.getBlockEntity(((BlockRayTraceResult) result).getBlockPos()) instanceof ScribesTile)
            return false;
        if(result instanceof BlockRayTraceResult && !playerIn.isShiftKeyDown()){
            if(worldIn.getBlockEntity(((BlockRayTraceResult) result).getBlockPos()) != null &&
                    !(worldIn.getBlockEntity(((BlockRayTraceResult) result).getBlockPos()) instanceof IntangibleAirTile
                            ||(worldIn.getBlockEntity(((BlockRayTraceResult) result).getBlockPos()) instanceof PhantomBlockTile))) {
                return false;
            }
        }

        EntityRayTraceResult entityRes = MathUtil.getLookedAtEntity(playerIn, 25);

        if(entityRes != null && entityRes.getEntity() instanceof LivingEntity)  return true;

        return result.getType() == RayTraceResult.Type.BLOCK || (isSensitive && result instanceof BlockRayTraceResult);
    }
}
