package invoker54.arsgears.capability.gear.combatgear;

import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.utilgear.UtilGearProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class CombatGearCap extends GearCap implements ICombatGear {

    private final String ACTIVATED = "ACTIVATED";

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
