package invoker54.arsgears.entity;

import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.common.entity.EntityProjectileSpell;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketANEffect;
import invoker54.arsgears.init.EntityInit;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.FMLPlayMessages;
import net.minecraftforge.fml.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModOrbProjectileEntity extends EntityProjectileSpell{
    private static final Logger POGGER = LogManager.getLogger();
//    int ticksLeft;
    private static final DataParameter<Integer> OWNER_UUID;
    public static final DataParameter<Integer> OFFSET;
    public static final DataParameter<Integer> ACCELERATES;
    public static final DataParameter<Integer> AOE;
    public static final DataParameter<Integer> TOTAL;
    public int extraTime;

    //Your previous health amount
    public float lastHealth = 0;
    //Amount that you did lose
    public float healthLoss = 0;
    //Amount you have to lose
    public float threshhold = 0;
    //If the spell should affect the attacker
    public boolean affectOther = false;

    public ModOrbProjectileEntity(World worldIn, double x, double y, double z) {
        super(worldIn, x, y, z);
    }

    public ModOrbProjectileEntity(World worldIn, LivingEntity shooter) {
        super(worldIn, shooter);
    }

    public ModOrbProjectileEntity(World world, SpellResolver resolver) {
        super(world, resolver);
    }

    public ModOrbProjectileEntity(EntityType<ModOrbProjectileEntity> entityWardProjectileEntityType, World world) {
        super(entityWardProjectileEntityType, world);
    }

    //These orbs will NOT be affected by what they touch
    @Override
    protected void onHit(RayTraceResult result) {
        //super.onHit(result);
    }

    @Override
    public void tick() {
        POGGER.debug("TICKING!");
        this.age++;
        if(!level.isClientSide && this.age > 60 * 20 + 30 * 20 * extraTime){
            POGGER.debug("IM TOO OLD, DELETING MYSELF!");
            this.remove();
            return;
        }
        //if the spell resolver is null
        if(!level.isClientSide && spellResolver == null){
            POGGER.debug("I HAVE NO RESOLVER, DELETING MYSELF!");
            this.remove();
        }
        Entity wardedEntity = level.getEntity(getOwnerID());
//        this.remove();

        //If the wardedEntity is null
        if(!level.isClientSide && wardedEntity == null) {
            POGGER.debug("THE ENTITY I CIRCLE IS GONE, DELETING MYSELF!");
            this.remove();
            return;
        }
        if(!(wardedEntity instanceof LivingEntity)) {
            return;
        }
        double rotateSpeed = 7.0;
        double radiusMultiplier = 1.5 + 0.5*getAoe();

        this.setPos(wardedEntity.getX()- radiusMultiplier * Math.sin(tickCount/rotateSpeed + getOffset()),
                wardedEntity.getY() + 1,
                wardedEntity.getZ()- radiusMultiplier * Math.cos(tickCount/rotateSpeed + getOffset()));
//        this.setPos(wardedEntity.getX() - Math.sin(tickCount + getOffset()),
//                wardedEntity.getY() + 1,
//                wardedEntity.getZ() - Math.cos(tickCount + getOffset()));

        //Vector3d vector3d2 = this.position();
//        int nextTick = tickCount + 3;
//        Vector3d vector3d3 = new Vector3d(
//                wardedEntity.getX() - radiusMultiplier * Math.sin(nextTick/rotateSpeed + getOffset()),
//                wardedEntity.getY() + 1,
//                wardedEntity.getZ()- radiusMultiplier * Math.cos(nextTick/rotateSpeed + getOffset()));
//        Vector3d vector3d3 = new Vector3d(
//                wardedEntity.getX() - Math.sin(nextTick + getOffset()),
//                wardedEntity.getY() + 1,
//                wardedEntity.getZ() - Math.cos(nextTick + getOffset()));
//        RayTraceResult raytraceresult = this.level.clip(new RayTraceContext(vector3d2, vector3d3, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
        //This is for making sure the ray trace got something
        //region Entity Ray Trace (Don't need this anymore
//        if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS) {
//            vector3d3 = raytraceresult.getLocation();
//        }
//
//        EntityRayTraceResult entityraytraceresult = this.findHitEntity(vector3d2, vector3d3);
//        if (entityraytraceresult != null) {
//            raytraceresult = entityraytraceresult;
//        }
//
//        if (raytraceresult != null && raytraceresult instanceof EntityRayTraceResult) {
//            Entity entity = ((EntityRayTraceResult)raytraceresult).getEntity();
//            Entity entity1 = this.getOwner();
//            if (entity instanceof PlayerEntity && entity1 instanceof PlayerEntity && !((PlayerEntity)entity1).canHarmPlayer((PlayerEntity)entity)) {
//                raytraceresult = null;
//            }
//        }
//
//        if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS  && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
//            this.onHit(raytraceresult);
//            this.hasImpulse = true;
//        }
        //endregion

        //This is client side stuff, don't mess with it for now
        if(level.isClientSide && this.age > 2) {
            double deltaX = getX() - xOld;
            double deltaY = getY() - yOld;
            double deltaZ = getZ() - zOld;
            double dist = Math.ceil(Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 8);

            for (double j = 0; j < dist; j++) {
                double coeff = j / dist;

                level.addParticle(GlowParticleData.createData(getParticleColor()),
                        (float) (xo + deltaX * coeff),
                        (float) (yo + deltaY * coeff), (float)
                                (zo + deltaZ * coeff),
                        0.0125f * (random.nextFloat() - 0.5f),
                        0.0125f * (random.nextFloat() - 0.5f),
                        0.0125f * (random.nextFloat() - 0.5f));

            }
        }

        //This is for calculating threshold
        if (!level.isClientSide()){
            LivingEntity wardEntity = ((LivingEntity)wardedEntity);
            float currHealth = wardEntity.getHealth();

            if (wardEntity.getLastDamageSource() == null) return;
            if (!(wardEntity.getLastDamageSource().getEntity() instanceof LivingEntity)) return;
            LivingEntity attacker = (LivingEntity) wardEntity.getLastDamageSource().getEntity();

            if (currHealth < lastHealth){
                healthLoss += (lastHealth - currHealth);
                POGGER.debug("HEALTH LOSS IS " + (healthLoss));
            }

            if (healthLoss >= threshhold || wardEntity.isDeadOrDying()){
                this.onThreshold(attacker);
                this.hasImpulse = true;
            }

            lastHealth = currHealth;
        }
    }

    protected void onThreshold(LivingEntity attacker) {
        if (level.isClientSide)
            return;

        if (this.spellResolver != null) {
            EntityRayTraceResult entityRes;
            BlockPos pos;

            if (affectOther) {
                entityRes = new EntityRayTraceResult(attacker);
                pos = attacker.blockPosition();
            } else {
                Entity wardedEntity = attacker.level.getEntity(this.getOwnerID());
                if (wardedEntity == null) {
                    attemptRemoval();
                    return;
                }
                entityRes = new EntityRayTraceResult(wardedEntity);
                pos = wardedEntity.blockPosition();
            }

            //This is for the actual spell.
            this.spellResolver.onResolveEffect(level, attacker, entityRes);
            //This is for effects on the client
            Networking.sendToNearby(level, pos, new PacketANEffect(PacketANEffect.EffectType.BURST,
                    pos, getParticleColorWrapper()));
        }

        //Remove this regardless.
        attemptRemoval();
//        if(result.getType() == RayTraceResult.Type.ENTITY) {
//            if (((EntityRayTraceResult) result).getEntity().equals(this.getOwner())) return;
//            if(this.spellResolver != null) {
//                this.spellResolver.onResolveEffect(level, (LivingEntity) this.getOwner(), result);
//                Networking.sendToNearby(level, new BlockPos(result.getLocation()), new PacketANEffect(PacketANEffect.EffectType.BURST,
//                        new BlockPos(result.getLocation()),getParticleColorWrapper()));
//                attemptRemoval();
//            }
//        }
//        else if(numSensitive > 0 && result instanceof BlockRayTraceResult && !this.removed){
//            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)result;
//            if(this.spellResolver != null) {
//                this.spellResolver.onResolveEffect(this.level, (LivingEntity) this.getOwner(), blockraytraceresult);
//            }
//            Networking.sendToNearby(level, ((BlockRayTraceResult) result).getBlockPos(), new PacketANEffect(PacketANEffect.EffectType.BURST,
//                    new BlockPos(result.getLocation()).below(), getParticleColorWrapper()));
//            attemptRemoval();
//        }
    }

    public void setOffset(int offset) {
        this.entityData.set(OFFSET, offset);
    }
    public int getOffset() {
        int val = 15;
        return (Integer)this.entityData.get(OFFSET) * val;
    }

    public void setTotal(int total) {
        this.entityData.set(TOTAL, total);
    }
    public int getTotal() {
        return (Integer)this.entityData.get(TOTAL) > 0 ? (Integer)this.entityData.get(TOTAL) : 1;
    }

    public void setAccelerates(int accelerates) {
        this.entityData.set(ACCELERATES, accelerates);
    }
    public int getAccelerates() {
        return (Integer)this.entityData.get(ACCELERATES);
    }

    public void setAoe(int aoe) {
        this.entityData.set(AOE, aoe);
    }
    public int getAoe() {
        return (Integer)this.entityData.get(AOE);
    }

//    public int getTicksLeft() {
//        return this.ticksLeft;
//    }
//    public void setTicksLeft(int ticks) {
//        this.ticksLeft = ticks;
//    }

    public int getOwnerID() {
        return (Integer)this.getEntityData().get(OWNER_UUID);
    }
    public void setOwnerID(int uuid) {
        this.getEntityData().set(OWNER_UUID, uuid);
    }
    
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UUID, 0);
        this.entityData.define(OFFSET, 0);
        this.entityData.define(ACCELERATES, 0);
        this.entityData.define(AOE, 0);
        this.entityData.define(TOTAL, 0);
    }

    public void addAdditionalSaveData(CompoundNBT tag) {
        super.addAdditionalSaveData(tag);
//        tag.putInt("left", this.ticksLeft);
        tag.putInt("offset", this.getOffset());
        tag.putInt("aoe", this.getAoe());
        tag.putInt("accelerate", this.getAccelerates());
        tag.putInt("total", this.getTotal());
        tag.putInt("ownerID", this.getOwnerID());
    }

    public void readAdditionalSaveData(CompoundNBT tag) {
        super.readAdditionalSaveData(tag);
//        this.ticksLeft = tag.getInt("left");
        this.setOffset(tag.getInt("offset"));
        this.setAoe(tag.getInt("aoe"));
        this.setAccelerates(tag.getInt("accelerate"));
        this.setOwnerID(tag.getInt("ownerID"));
        this.setTotal(tag.getInt("total"));
    }

    public EntityType<?> getType() {
        return EntityInit.MODDED_ORBIT;
    }

    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public ModOrbProjectileEntity(FMLPlayMessages.SpawnEntity packet, World world) {
        super(EntityInit.MODDED_ORBIT, world);
    }

    static {
        OWNER_UUID = EntityDataManager.defineId(ModOrbProjectileEntity.class, DataSerializers.INT);
        OFFSET = EntityDataManager.defineId(ModOrbProjectileEntity.class, DataSerializers.INT);
        ACCELERATES = EntityDataManager.defineId(ModOrbProjectileEntity.class, DataSerializers.INT);
        AOE = EntityDataManager.defineId(ModOrbProjectileEntity.class, DataSerializers.INT);
        TOTAL = EntityDataManager.defineId(ModOrbProjectileEntity.class, DataSerializers.INT);
    }
}
