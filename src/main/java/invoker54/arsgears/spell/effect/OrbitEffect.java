package invoker54.arsgears.spell.effect;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.common.spell.method.MethodProjectile;
import invoker54.arsgears.entity.ModOrbProjectileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class OrbitEffect extends AbstractEffect {

    public static OrbitEffect INSTANCE = new OrbitEffect();
    private static final Logger LOGGER = LogManager.getLogger();

    private OrbitEffect() {
        super("modded_orbit", "Orbit");
    }

    private static float calculateThreshold(LivingEntity entityHit, List<AbstractAugment> augments){
        float threshold;
        float amp;
        float damp;
        //This is for percentage based threshold
        if (getBuffCount(augments, AugmentSensitive.class) == 0) {
            threshold = entityHit.getMaxHealth() * 0.25f;
            float difference = entityHit.getMaxHealth() * 0.05f;
            amp = getBuffCount(augments, AugmentAmplify.class) * difference;
            damp = getBuffCount(augments, AugmentDampen.class) * difference;
        }
        //This is for flat-value based threshold
        else {
            threshold = 8;
            amp = getBuffCount(augments, AugmentAmplify.class);
            damp = getBuffCount(augments, AugmentDampen.class);
        }

        threshold += (amp - damp) * 2;
        LOGGER.debug("THRESHOLD IS " + (threshold) +" HEARTS");
        return MathHelper.clamp(threshold, 1, entityHit.getMaxHealth() - 1);
    }

    public void summonProjectiles(World world, LivingEntity shooter, LivingEntity entityHit, SpellResolver resolver, List<AbstractAugment> augments){
//        int total = 3 + getBuffCount(augments, AugmentSplit.class);
        int total = 3;
        float threshold = calculateThreshold(entityHit, augments);

        if (entityHit == null) return;

        if (world.isClientSide) return;

        ModOrbProjectileEntity.clearList(entityHit.getId());

        for(int i = 0; i < total; i++){
            //Owner Id is the one that carries the orbs...
            ModOrbProjectileEntity wardProjectile = new ModOrbProjectileEntity(world, resolver, entityHit.getId());
            wardProjectile.lastHealth = entityHit.getHealth();
            wardProjectile.threshhold = threshold * (i + 1);
//            LOGGER.debug("THRESHOLD FOR PROJ " + (i + 1) + " IS " + (wardProjectile.threshhold) + " HEARTS!");
            wardProjectile.setOffset(i);
//            wardProjectile.setAccelerates(getBuffCount(augments, AugmentAccelerate.class));
//            wardProjectile.setAoe(getBuffCount(augments, AugmentAOE.class));
            //            wardProjectile.setTotal(total);
            wardProjectile.extraTime = getBuffCount(augments, AugmentExtendTime.class) - getBuffCount(augments, AugmentDurationDown.class);
            wardProjectile.extraTime *= 5;
            wardProjectile.setColor(resolver.spellContext.colors);
            wardProjectile.affectOther = getBuffCount(augments, AugmentAOE.class) != 0;
            world.addFreshEntity(wardProjectile);
//            LOGGER.debug("THE ORB HAS BEEN ADDED");
//            LOGGER.debug("Where is the shooter: X:" + shooter.getX() + " Y:" + shooter.getY() + " Z:" + shooter.getZ());
//            LOGGER.debug("WHERE TO FIND ENTITY: X:" + wardProjectile.getX() + " Y:" + wardProjectile.getY() + " Z:" + wardProjectile.getZ());
        }
    }

    //This effect ONLY works on entities. nothing else.
    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return livingEntityHitSuccess(rayTraceResult);
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @org.jetbrains.annotations.Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        spellContext.setCanceled(true);
        if(spellContext.getCurrentIndex() >= spellContext.getSpell().recipe.size()) return;
        if (!(rayTraceResult.getEntity() instanceof LivingEntity)) return;
        LivingEntity hitEntity = (LivingEntity) rayTraceResult.getEntity();

        Spell newSpell =  new Spell(new ArrayList<>(spellContext.getSpell().recipe.subList(spellContext.getCurrentIndex(), spellContext.getSpell().recipe.size())));
        SpellContext newContext = new SpellContext(newSpell, shooter).withColors(spellContext.colors);

        SpellResolver resolver = new SpellResolver(newContext);
        summonProjectiles(world, shooter, hitEntity, resolver, spellStats.getAugments());
    }

    @Override
    public int getManaCost() {
        return 50;
    }

    @Override
    public ISpellTier.Tier getTier() {
        return ISpellTier.Tier.THREE;
    }

    @Override
    public String getBookDescription() {
//        return "Summons three orbiting projectiles around the caster that will cast a spell on any entities it may hit. Additional projectiles, their speed, radius, and duration may be augmented. Sensitive will cause Orbit to hit blocks.";
        return "Summons three orbiting projectiles around the caster that will cast a spell on any entities it may hit. Additional projectiles, their speed, radius, and duration may be augmented. Sensitive will cause Orbit to hit blocks.";
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return ArsNouveauAPI.getInstance().getGlyphItem(MethodProjectile.INSTANCE);
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentExtendTime.INSTANCE, AugmentDampen.INSTANCE,
                AugmentAmplify.INSTANCE, AugmentSensitive.INSTANCE, AugmentAOE.INSTANCE);
    }

    //    @Nonnull
//    @Override
//    public Set<AbstractAugment> getCompatibleAugments() {
//        return augmentSetOf(AugmentAccelerate.INSTANCE, AugmentAOE.INSTANCE, AugmentPierce.INSTANCE, AugmentSplit.INSTANCE, AugmentExtendTime.INSTANCE,
//                AugmentDurationDown.INSTANCE, AugmentSensitive.INSTANCE);
//    }
}
