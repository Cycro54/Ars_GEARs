package invoker54.arsgears.capability.init;

import invoker54.arsgears.ArsGears;
import invoker54.arsgears.entity.ModBobberEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = ArsGears.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityInit {
    private static final ArrayList<EntityType<?>> entityTypes = new ArrayList<>();
    public static <T extends Entity> EntityType<T> addEntity (EntityType.Builder<T> builder, String resourceLocation){
        EntityType<T> entityType = builder.build(resourceLocation);
        entityType.setRegistryName(resourceLocation);
        entityTypes.add(entityType);
        return entityType;
    }

    public static final EntityType<ModBobberEntity> FISHING_BOBBER = addEntity(EntityType.
            Builder.<ModBobberEntity>createNothing(EntityClassification.MISC)
            .noSave()
            .noSummon()
            .sized(0.25F, 0.25F)
            .clientTrackingRange(4)
            .updateInterval(5), "fish_bobber.json");

    @SubscribeEvent
    public static void registerEntities(RegistryEvent.Register<EntityType<?>> event){
        IForgeRegistry<EntityType<?>> registry = event.getRegistry();

        for (EntityType<?> entityType : entityTypes){
            registry.register(entityType);
        }
    }

}
