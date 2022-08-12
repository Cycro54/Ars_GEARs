package invoker54.arsgears.capability.gear.combatgear;

import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import com.sun.jna.platform.win32.WinBase;
import invoker54.arsgears.capability.gear.GearCap;
import invoker54.arsgears.capability.gear.utilgear.GearProvider;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.advancements.criterion.ThrownItemPickedUpByEntityTrigger;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.brain.task.SwimTask;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class CombatGearCap extends GearCap implements ICombatGear {
    private static final Logger LOGGER = LogManager.getLogger();

    private final String ACTIVATED = "ACTIVATED";
    private boolean activated = false;

    public boolean isSweep = false;

    public CombatGearCap(){
        super();
    }

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
    String recipe1 = "1recipe";
    String recipe2 = "2recipe";
    String recipe3 = "3recipe";
    
    @Override
    protected CompoundNBT saveTag(CompoundNBT stackTag) {
        CompoundNBT capTag = super.saveTag(stackTag);

        //What spell mode you have selected
        if (stackTag.contains(mode)) capTag.putInt(mode, stackTag.getInt(mode));

        //All the spell recipes from that gear cycle
        if (stackTag.contains(recipe1)) {
            capTag.putString(recipe1, stackTag.getString(recipe1));
        }
        if (stackTag.contains(recipe2)) {
            capTag.putString(recipe2, stackTag.getString(recipe2));
        }
        if (stackTag.contains(recipe3)){
            capTag.putString(recipe3, stackTag.getString(recipe3));
            stackTag.remove(recipe3);
        }

        return capTag;
    }

    @Override
    protected CompoundNBT readTag(CompoundNBT stackTag) {
        //This is the capNBT
       CompoundNBT capTag = super.readTag(stackTag);

        //What spell mode you have selected
        if (capTag.contains(mode)) stackTag.putInt(mode, capTag.getInt(mode));

        //All the spell recipes from that gear cycle
        if (capTag.contains(recipe1)) stackTag.putString(recipe1, capTag.getString(recipe1));
        if (capTag.contains(recipe2)) stackTag.putString(recipe2, capTag.getString(recipe2));
        if (capTag.contains(recipe3)) stackTag.putString(recipe3, capTag.getString(recipe3));

        return capTag;
    }

    @Override
    public void cycleItem(ItemStack gearStack, PlayerEntity player) {
        setActivated(false);
        super.cycleItem(gearStack, player);
        LOGGER.debug("IS IT ACTIVATED???? " + getActivated());
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
