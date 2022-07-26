package invoker54.arsgears.capability.player;

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

public class PlayerDataProvider implements ICapabilitySerializable<INBT> {

    public static final ResourceLocation CAP_PLAYER_DATA_LOC = new ResourceLocation(ArsGears.MOD_ID, "cap_player_data");
    public static final byte COMPOUND_NBT_ID = new CompoundNBT().getId();

    public PlayerDataProvider(){
        playerDataCap = new PlayerDataCap();
    }

    //region Capability setup
    //This is where all of the ArsGears capability data is
    @CapabilityInject(PlayerDataCap.class)
    public static Capability<PlayerDataCap> CAP_PLAYER_DATA = null;

    private final static String CAP_PLAYER_DATA_NBT = "CAP_PLAYER_DATA_NBT";

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {


        if (CAP_PLAYER_DATA == capability) {
            return LazyOptional.of(() -> playerDataCap).cast();
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
        INBT playerDataNBT = CAP_PLAYER_DATA.writeNBT(playerDataCap, null);
        nbtData.put(CAP_PLAYER_DATA_NBT, playerDataNBT);
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
        CAP_PLAYER_DATA.readNBT(playerDataCap, null, nbtData.getCompound(CAP_PLAYER_DATA_NBT));
    }

    //This is where the current capability is stored to read and write
    private PlayerDataCap playerDataCap;
}
