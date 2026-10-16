package com.example.techevolution.entity;

import com.example.techevolution.TechEvolution;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TechEvolution.MODID);

    public static final RegistryObject<EntityType<EvolvedMob>> EVOLVED_MOB =
            ENTITIES.register("evolved_mob", () -> EntityType.Builder.of(EvolvedMob::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10)
                    .updateInterval(2)
                    .build("techevolution:evolved_mob"));

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
