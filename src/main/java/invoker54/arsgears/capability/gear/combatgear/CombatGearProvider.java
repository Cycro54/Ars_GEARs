package invoker54.arsgears.capability.gear.combatgear;

import invoker54.arsgears.ArsGears;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CombatGearProvider implements ICapabilitySerializable<INBT> {

    public static final ResourceLocation CAP_COMBAT_GEAR_LOC = new ResourceLocation(ArsGears.MOD_ID, "cap_combat_gear");
    public static final byte COMPOUND_NBT_ID = new CompoundNBT().getId();

    public CombatGearProvider(){
        combatGearCap = new CombatGearCap();
    }

    //region Capability setup
    //This is where all of the ArsGears capability data is
    @CapabilityInject(CombatGearCap.class)
    public static Capability<CombatGearCap> CAP_COMBAT_GEAR = null;

    private final static String CAP_COMBAT_GEAR_NBT = "CAP_COMBAT_GEAR_NBT";

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {


        if (CAP_COMBAT_GEAR == capability) {
            return LazyOptional.of(() -> combatGearCap).cast();
            // why are we using a lambda?  Because LazyOptional.of() expects a NonNullSupplier interface.  The lambda automatically
            //   conforms itself to that interface.  This save me having to define an inner class implementing NonNullSupplier.
            // The explicit cast to LazyOptional<T> is required because our CAPABILITY_ELEMENTAL_FIRE can't be typed.  Our code has
            //   checked that the requested capability matches, so the explict cast is safe (unless you have mixed them up)
        }

        return LazyOptional.empty();


        //return LazyOptional.empty();
        // Note that if you are implementing getCapability in a derived class which implements ICapabilityProvider
        // eg you have added a new MyEntity which has the method MyEntity::getCapability instead of using AttachCapabilitiesEvent to attach a
        // separate class, then you should call
        // return super.getCapability(capability, facing);
        //   instead of
        // return LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        CompoundNBT nbtData = new CompoundNBT();
        INBT combatGearNBT = CAP_COMBAT_GEAR.writeNBT(combatGearCap, null);
        nbtData.put(CAP_COMBAT_GEAR_NBT, combatGearNBT);
        return  nbtData;
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        if (nbt.getId() != COMPOUND_NBT_ID) {
            //System.out.println("Unexpected NBT type:"+nbt);
            return;  // leave as default in case of error
        }
        //System.out.println("I ran for deserializing");
        CompoundNBT nbtData = (CompoundNBT) nbt;
        CAP_COMBAT_GEAR.readNBT(combatGearCap, null, nbtData.getCompound(CAP_COMBAT_GEAR_NBT));
    }

    //This is where the current capability is stored to read and write
    private CombatGearCap combatGearCap;
}
