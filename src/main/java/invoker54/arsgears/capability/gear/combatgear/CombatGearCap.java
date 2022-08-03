package invoker54.arsgears.capability.gear.combatgear;

import com.hollingsworth.arsnouveau.common.items.SpellBook;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.utilgear.UtilGearProvider;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.advancements.criterion.ThrownItemPickedUpByEntityTrigger;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class CombatGearCap extends GearCap implements ICombatGear {

    private final String ACTIVATED = "ACTIVATED";
    private final String SPELL_MODES = "SPELL_MODES";

    private boolean activated = false;

    public static CombatGearCap getCap(ItemStack item){
        return item.getCapability(CombatGearProvider.CAP_COMBAT_GEAR).orElseThrow(NullPointerException::new);
    }

    @Override
    public boolean getActivated() {
        return activated;
    }

    @Override
    public void setActivated(boolean flag) {
        activated = flag;
    }

    int swordSpell = 1;
    int bowSpell = 4;
    int mirrorSpell = 7;

    @Override
    public int getSpellMode() {
        switch (this.getSelectedItem()){
            default:
                return swordSpell;
            case 1:
                return bowSpell;
            case 2:
                return mirrorSpell;
        }
    }

    @Override
    public void changeSpell(int spellIndex) {

        switch (this.getSelectedItem()){
            default:
                swordSpell = spellIndex;
                break;
            case 1:
                bowSpell = spellIndex;
                break;
            case 2:
                mirrorSpell = spellIndex;
                break;
        }
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT cNBT = super.serializeNBT();
        cNBT.putBoolean(ACTIVATED, activated);
        cNBT.putIntArray(SPELL_MODES, IntStream.of(swordSpell, bowSpell, mirrorSpell).toArray());
        return cNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        super.deserializeNBT(nbt);

        activated = nbt.getBoolean(ACTIVATED);

        //Now for the spell modes
        if (nbt.contains(SPELL_MODES)) {
            int[] modes = nbt.getIntArray(SPELL_MODES);
            swordSpell = modes[0];
            bowSpell = modes[1];
            mirrorSpell = modes[2];
        }
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
