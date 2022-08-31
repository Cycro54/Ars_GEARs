package invoker54.arsgears.capability.gear;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.combatgear.CombatGearCap;
import net.minecraft.item.ItemStack;
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

public class GearProvider implements ICapabilitySerializable<INBT> {

    public static final ResourceLocation CAP_GEAR_LOC = new ResourceLocation(ArsGears.MOD_ID, "cap_gear");
    public static final byte COMPOUND_NBT_ID = new CompoundNBT().getId();

    public GearProvider(boolean utility, ItemStack gearStack){
        GearCap = utility ? new GearCap(gearStack) : new CombatGearCap(gearStack);
    }

    //region Capability setup
    //This is where all of the ArsGears capability data is
    @CapabilityInject(GearCap.class)
    public static Capability<GearCap> CAP_GEAR = null;

    private final static String CAP_GEAR_NBT = "CAP_GEAR_NBT";

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {


        if (CAP_GEAR == capability) {
            return LazyOptional.of(() -> GearCap).cast();
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
        INBT GearNBT = CAP_GEAR.writeNBT(GearCap, null);
        nbtData.put(CAP_GEAR_NBT, GearNBT);
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
        CAP_GEAR.readNBT(GearCap, null, nbtData.getCompound(CAP_GEAR_NBT));
    }

    //This is where the current capability is stored to read and write
    private GearCap GearCap;
}
