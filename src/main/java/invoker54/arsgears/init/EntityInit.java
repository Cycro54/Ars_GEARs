package invoker54.arsgears.init;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.entity.ModBobberEntity;
import invoker54.arsgears.entity.ModOrbProjectileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityInit {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ArrayList<EntityType<?>> entityTypes = new ArrayList<>();
    public static <T extends Entity> EntityType<T> addEntity (EntityType.Builder<T> builder, String resourceLocation){
        ResourceLocation location = new ResourceLocation(ArsGears.MOD_ID, resourceLocation);
        EntityType<T> entityType = builder.build(location.toString());
        entityType.setRegistryName(location);
        entityTypes.add(entityType);
        return entityType;
    }

    public static final EntityType<ModBobberEntity> FISHING_BOBBER = addEntity(EntityType.
            Builder.<ModBobberEntity>createNothing(EntityClassification.MISC)
            .noSave()
            .noSummon()
            .sized(0.25F, 0.25F)
            .clientTrackingRange(4)
            .updateInterval(5), "fish_bobber");

    public static final EntityType<ModOrbProjectileEntity> MODDED_ORBIT = addEntity(EntityType.
            Builder.<ModOrbProjectileEntity>of(ModOrbProjectileEntity::new, EntityClassification.MISC).sized(0.5f, 0.5f)
            .clientTrackingRange(20).updateInterval(20).setShouldReceiveVelocityUpdates(true).setCustomClientFactory(ModOrbProjectileEntity::new), "ward_entity");

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event){
        IForgeRegistry<EntityType<?>> registry = event.getRegistry();

        for (EntityType<?> entityType : entityTypes){
            registry.register(entityType);
            // LOGGER.debug("JUST REGISTERED ENTITY: " + entityType.getRegistryName());
        }
    }

}
