package invoker54.arsgears.network.message;

import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import invoker54.arsgears.ArsUtil;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import invoker54.arsgears.init.SoundsInit;
import invoker54.arsgears.item.combatgear.CombatGearItem;
import invoker54.arsgears.item.combatgear.ModSpellMirror;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

import static com.hollingsworth.arsnouveau.common.items.SpellBook.getMode;
import static com.hollingsworth.arsnouveau.common.items.SpellBook.getSpellColor;
import static invoker54.arsgears.item.combatgear.CombatGearItem.getCooldown;
import static invoker54.arsgears.item.combatgear.CombatGearItem.mirrorInt;

public class QuickCastMsg {
    //This is how the Network Handler will handle the message
    private static final Logger LOGGER = LogManager.getLogger();

    public static void handle(QuickCastMsg msg, Supplier<NetworkEvent.Context> contextSupplier){
        NetworkEvent.Context context = contextSupplier.get();

        context.enqueueWork(() -> {
            PlayerEntity player = context.getSender();
            if (player == null) return;

            ItemStack gearStack = ArsUtil.getHeldGearCap(player, false, true);
            CombatGearCap cap = CombatGearCap.getCap(gearStack);
            if (cap == null) return;

            //Now lets grab the gearStack tag from the cap OR the itemStack itself
            CompoundNBT itemTag;
            if (gearStack.getItem() instanceof ModSpellMirror){
                itemTag = gearStack.getOrCreateTag();
            }
            else {
                itemTag = cap.getTag(mirrorInt);
            }

            //Now we see which recipe was selected
            int mode = SpellBook.getMode(itemTag);
            Spell spell = SpellBook.getRecipeFromTag(itemTag, mode);

            //Get the spell resolver
            SpellResolver resolver = new SpellResolver((new SpellContext(spell, player)).
                    withColors(getSpellColor(itemTag, getMode(itemTag)))).withSilent(true);

            boolean canCast = resolver.canCast(player);

            if (canCast){
                //Let's make sure it isn't on cooldown
                float coolDown = getCooldown(player, itemTag, mode, true);
                if (coolDown > 0) return;
                //Now let's get the soon to be cooldown
                coolDown = CombatGearItem.calcCooldown(mirrorInt, spell, true);

                //Get the upgrades
                ModSpellMirror.addFreeGlyph(resolver.spell, gearStack);

                //Cast the spell on the player
                resolver.onCast(gearStack, player, player.level);

                //Then finally set the cooldown
                CombatGearItem.setCooldown(itemTag, mode, coolDown + player.level.getGameTime());

                player.level.playSound(null, player.blockPosition(), SoundsInit.GEAR_CAST, player.getSoundSource(), 1,0.8F + player.getRandom().nextFloat() * 0.4F);
            }
        });
        context.setPacketHandled(true);
    }
}
