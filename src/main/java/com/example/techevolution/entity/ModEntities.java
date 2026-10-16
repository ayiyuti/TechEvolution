package com.example.techevolution.entity;

import com.example.techevolution.TechEvolution;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = 
            DeferredRegister.create(Registries.ENTITY_TYPE, TechEvolution.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<EvolvedMob>> EVOLVED_MOB =
            ENTITY_TYPES.register("evolved_mob", () -> EntityType.Builder.of(EvolvedMob::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .build(TechEvolution.MODID + ":evolved_mob"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}