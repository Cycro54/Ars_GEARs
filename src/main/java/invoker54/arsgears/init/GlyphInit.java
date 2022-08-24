package invoker54.arsgears.init;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import invoker54.arsgears.spell.effect.ModBreakEffect;
import invoker54.arsgears.spell.effect.ModUnderfootEffect;
import invoker54.arsgears.spell.effect.OrbitEffect;

import java.util.ArrayList;
import java.util.List;


public class GlyphInit {
    public static List<AbstractSpellPart> registeredSpells = new ArrayList<>();

    public static void registerGlyphs() {
        register(ModBreakEffect.INSTANCE);
        register(OrbitEffect.INSTANCE);
        register(ModUnderfootEffect.INSTANCE);
    }

    public static void register(AbstractSpellPart spellPart) {
        ArsNouveauAPI.getInstance().registerSpell(spellPart.getTag(), spellPart);
        registeredSpells.add(spellPart);
    }
}
