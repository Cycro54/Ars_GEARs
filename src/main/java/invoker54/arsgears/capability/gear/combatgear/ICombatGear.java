package invoker54.arsgears.capability.gear.combatgear;

public interface ICombatGear {
    boolean getActivated();
    void setActivated(boolean flag);

    int getSpellMode();
    void changeSpell(int spellIndex);
}
