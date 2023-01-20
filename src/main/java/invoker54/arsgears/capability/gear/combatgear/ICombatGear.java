package invoker54.arsgears.capability.gear.combatgear;

import net.minecraft.entity.player.PlayerEntity;

public interface ICombatGear {
    boolean getActivated();
    void setActivated(boolean flag, PlayerEntity player);

//    int getSpellMode();
//    void changeSpell(int spellIndex);
}
