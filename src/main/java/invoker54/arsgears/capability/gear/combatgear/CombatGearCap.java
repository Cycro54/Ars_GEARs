package invoker54.arsgears.capability.gear.combatgear;

import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.utilgear.GearProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

public class CombatGearCap extends GearCap implements ICombatGear {
    private static final Logger LOGGER = LogManager.getLogger();

    private final String ACTIVATED = "ACTIVATED";
    private boolean activated = false;

    public boolean isSweep = false;

    public static CombatGearCap getCap(ItemStack item){
       return (CombatGearCap) item.getCapability(GearProvider.CAP_GEAR).orElseGet(() -> null);
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
