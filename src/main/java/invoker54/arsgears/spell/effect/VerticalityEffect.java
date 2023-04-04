package invoker54.arsgears.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentPierce;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VerticalityEffect extends AbstractEffect {
    public static VerticalityEffect INSTANCE = new VerticalityEffect();
    private static final Logger LOGGER = LogManager.getLogger();

    public VerticalityEffect() {
        super("verticality", "Verticality");
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return livingEntityHitSuccess(rayTraceResult);
    }

    public void castEffect(World world, @org.jetbrains.annotations.Nullable LivingEntity shooter, LivingEntity hitEntity, SpellStats spellStats, SpellResolver resolver){
        //Amplify will cause the spell to search further
        //Dampen will cause the spell to be reversed
        //The spell will stop at the first solid block found
        //augment pierce causes spell to go through the block hit (if there is a block there)
        boolean hasSensitive = spellStats.hasBuff(AugmentSensitive.INSTANCE);
        //Causes the spell to go trhough blocks if it hits a block
        int pierceAmount = spellStats.getBuffCount(AugmentPierce.INSTANCE);
        boolean hasDampen = spellStats.hasBuff(AugmentDampen.INSTANCE);
        float direction = (hasDampen ? 1 : -1);
        float range = (2 * spellStats.getBuffCount(AugmentAmplify.INSTANCE));
        // LOGGER.debug("WHATS MY HEIGHT? " + hitEntity.getBbHeight());
        // LOGGER.debug("WHATS MY HEIGHT ROUNDED? " + Math.ceil(hitEntity.getBbHeight()));
//        Vector3d origVector = (hasDampen ? hitEntity.position().add(0, Math.ceil(hitEntity.getBbHeight()), 0) : hitEntity.position());
        Vector3d origVector = hitEntity.position().add(0, (hasDampen ? Math.ceil(hitEntity.getBbHeight()) : 0), 0);
        float addition = hasDampen ? 0.5F : -0.5F;
        Vector3d destVector = origVector.add(0, direction * range + addition, 0);

        BlockRayTraceResult result = world.clip(new RayTraceContext(origVector, destVector, RayTraceContext.BlockMode.OUTLINE,
                    (hasSensitive ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE), hitEntity));

        if (result.getType() == RayTraceResult.Type.MISS){
            if (hasDampen) {
                BlockPos pos = hitEntity.blockPosition().offset(0, hitEntity.getBbHeight(), 0);
                result = new BlockRayTraceResult(hitEntity.position().add(0, hitEntity.getBbHeight(), 0), Direction.DOWN, pos.above(), true);
            }
            else {
                result = new BlockRayTraceResult(hitEntity.position(), Direction.DOWN, hitEntity.blockPosition().below(), true);
            }
        }

//        else {
//            if (hasDampen) {
//                result = new BlockRayTraceResult(hitEntity.position(), Direction.DOWN,
//                        hitEntity.blockPosition().offset(0, Math.ceil(hitEntity.getBbHeight()),0), true);
//            }
//            else {
//                result = new BlockRayTraceResult(hitEntity.position(), Direction.DOWN, hitEntity.blockPosition().below(), true);
//            }
//        }
        resolver.onResolveEffect(world, shooter, result);

        for (int a = 0; a < pierceAmount; a++) {
            result = result.withPosition(result.getBlockPos().offset(0, (a + 1) * direction, 0));
            resolver.onResolveEffect(world, shooter, result);
        }
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
        castEffect(shooter.getCommandSenderWorld(), shooter, hitEntity, spellStats, resolver);
    }

    @Override
    public int getManaCost() {
        return 20;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentSensitive.INSTANCE,
                AugmentDampen.INSTANCE, AugmentPierce.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "A spell that targets the place above or below the entity hit.";
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.STONE_PRESSURE_PLATE;
    }
}