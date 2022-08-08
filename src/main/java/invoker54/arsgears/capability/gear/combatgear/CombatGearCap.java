package invoker54.arsgears.capability.gear.combatgear;

import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.utilgear.UtilGearProvider;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.advancements.criterion.ThrownItemPickedUpByEntityTrigger;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.task.SwimTask;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class CombatGearCap extends GearCap implements ICombatGear {

    private final String ACTIVATED = "ACTIVATED";
    private boolean activated = false;

    public CombatGearCap(){
        //This is setting all of the initial spell modes so that each one will start on the correct initial mode
        itemTags[0].putInt("mode", 1);
        itemTags[1].putInt("mode", 4);
        itemTags[2].putInt("mode", 7);
    }

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
    protected CompoundNBT saveTag(CompoundNBT stackTag) {
        CompoundNBT capTag = super.saveTag(stackTag);

        if (stackTag.contains("mode")){
            capTag.putInt("mode", stackTag.getInt("mode"));
        }

        return capTag;
    }

    @Override
    protected CompoundNBT readTag(CompoundNBT stackTag) {
        //This is the capNBT
       CompoundNBT nbt = super.readTag(stackTag);

       //Now we will be reading the selected spell mode for the current item
        if (nbt.contains("mode")){
            stackTag.putInt("mode", nbt.getInt("mode"));
        }

        return nbt;
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
