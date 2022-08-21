package invoker54.arsgears.capability.gear.combatgear;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractCastMethod;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.GearProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.List;

import static invoker54.arsgears.item.combatgear.CombatGearItem.COMBAT_GEAR;
import static invoker54.arsgears.item.combatgear.CombatGearItem.COOLDOWN;

public class CombatGearCap extends GearCap implements ICombatGear {
    private static final Logger LOGGER = LogManager.getLogger();

    private final String ACTIVATED = "ACTIVATED";
    private boolean activated = false;

    public static CombatGearCap getCap(ItemStack item) {
        GearCap cap = item.getCapability(GearProvider.CAP_GEAR).orElseGet(() -> null);

        if (cap instanceof CombatGearCap) return (CombatGearCap) cap;

        return null;
    }

    public CombatGearCap(){}

    public CombatGearCap(ItemStack gearStack) {
        super(gearStack);
        CompoundNBT gearTag = gearStack.getOrCreateTag();

        //here I will grab all of the starting spells and give them to the player if they don't have em
        List<AbstractSpellPart> spellParts = ArsNouveauAPI.getInstance().getDefaultStartingSpells();
        List<AbstractSpellPart> unlockedParts = SpellBook.getUnlockedSpells(gearTag);

        //Let's add all the starter spell parts
        for (AbstractSpellPart spellPart : spellParts){
            if (spellPart instanceof AbstractCastMethod) continue;

            if (!unlockedParts.contains(spellPart)){
                SpellBook.unlockSpell(gearTag, spellPart.getTag());
            }
        }
    }

    @Override
    public boolean getActivated() {
        return activated;
    }

    @Override
    public void setActivated(boolean flag) {
        activated = flag;
    }

    String mode = "mode";
    String recipe = "recipe";
    String name = "_name";

    @Override
    protected CompoundNBT saveTag(CompoundNBT mainNBT, CompoundNBT tagNBT, CompoundNBT capNBT) {
        //What spell mode you have selected
        if (tagNBT.contains(mode)) {
            capNBT.putInt(mode, tagNBT.getInt(mode));
        }

        //Goes through the current spells
        for (int a = 1; a < 3 + 1; a++){
            if (tagNBT.contains((a)+recipe)){
                //Spell itself
                capNBT.putString(((a)+recipe), tagNBT.getString(((a)+recipe)));
                tagNBT.remove(((a)+recipe));
                //Spell name
                capNBT.putString(((a)+name), tagNBT.getString(((a)+name)));
                tagNBT.remove(((a)+name));
            }
        }
        //next up is cooldowns
        if (tagNBT.contains(COMBAT_GEAR + COOLDOWN)) {
            CompoundNBT coolDownNBT = tagNBT.getCompound(COMBAT_GEAR + COOLDOWN);
            CompoundNBT capCoolDowns = new CompoundNBT();
            if (capNBT.contains(COMBAT_GEAR + COOLDOWN)) capCoolDowns = capNBT.getCompound(COMBAT_GEAR + COOLDOWN);
            for (int a = 1; a < 3 + 1; a++) {
                if (coolDownNBT.contains("" + a)) {
                    //Cooldowns
                    capCoolDowns.putFloat("" + a, coolDownNBT.getFloat("" + a));
                    coolDownNBT.putFloat("" + a, 0);
                }
            }
            capNBT.put(COMBAT_GEAR + COOLDOWN, capCoolDowns);
        }

        LOGGER.error("(COMBAT) HEY AM I SAVING THOSE SPELLS? " + capNBT.getString("1recipe"));

        return super.saveTag(mainNBT, tagNBT, capNBT);
    }

    @Override
    protected CompoundNBT loadTag(CompoundNBT mainNBT, CompoundNBT tagNBT, CompoundNBT capNBT) {
        //What spell mode you have selected
        if (capNBT.contains(mode)){
            tagNBT.putInt(mode, capNBT.getInt(mode));
        }

        //Goes through the current spells
        for (int a = 1; a < 3 + 1; a++){
            if (capNBT.contains((a)+recipe)){
                //Spell itself
                tagNBT.putString(((a)+recipe), capNBT.getString(((a)+recipe)));
                //Spell name
                tagNBT.putString(((a)+name), capNBT.getString(((a)+name)));
            }
        }

        //next up is cooldowns
        if (capNBT.contains(COMBAT_GEAR + COOLDOWN)) {
            CompoundNBT capCoolDowns = capNBT.getCompound(COMBAT_GEAR + COOLDOWN);
            CompoundNBT tagCooldowns = new CompoundNBT();
            for (int a = 1; a < 3 + 1; a++) {
                if (capCoolDowns.contains("" + a)) {
                    //Cooldowns
                    tagCooldowns.putFloat("" + a, capCoolDowns.getFloat("" + a));
                }
                else {
                    tagCooldowns.putFloat("" + a, 0);
                }
            }
            tagNBT.put(COMBAT_GEAR + COOLDOWN, tagCooldowns);
        }

        return super.loadTag(mainNBT, tagNBT, capNBT);
    }

    @Override
    public void cycleItem(ItemStack gearStack, PlayerEntity player) {
        setActivated(false);
        super.cycleItem(gearStack, player);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT cNBT = super.serializeNBT();
        cNBT.putBoolean(ACTIVATED, activated);
        return cNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);

        activated = nbt.getBoolean(ACTIVATED);
    }

    public static class CombatGearNBTStorage implements Capability.IStorage<CombatGearCap>{

        @Nullable
        @Override
        public INBT writeNBT(Capability<CombatGearCap> capability, CombatGearCap instance, Direction side) {
            return instance.serializeNBT();
        }

        @Override
        public void readNBT(Capability<CombatGearCap> capability, CombatGearCap instance, Direction side, INBT nbt) {
            CompoundNBT mainNbt = (CompoundNBT) nbt;

            instance.deserializeNBT(mainNbt);
        }
    }
}
