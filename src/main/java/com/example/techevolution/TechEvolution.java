package com.example.techevolution;

import com.example.techevolution.command.SpawnCommand;
import com.example.techevolution.config.ModConfig;
import com.example.techevolution.difficulty.DifficultyManager;
import com.example.techevolution.entity.ModEntities;
import com.example.techevolution.entity.EvolvedMobRenderer;
import com.example.techevolution.spawn.SpawnHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TechEvolution.MODID)
public class TechEvolution {
    public static final String MODID = "techevolution";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public TechEvolution(IEventBus modEventBus, ModContainer modContainer) {
        ModConfig.init();
        ModEntities.register(modEventBus);

        modEventBus.addListener(EntityRenderersEvent.RegisterRenderers.class, EvolvedMobRenderer::register);

        // 1.21.1 中注册实体属性的正确事件是 EntityAttributeCreationEvent
        modEventBus.addListener(EntityAttributeCreationEvent.class,
                event -> event.put(ModEntities.EVOLVED_MOB.get(), EvolvedMob.createAttributes().build()));

        NeoForge.EVENT_BUS.register(new DifficultyManager());
        NeoForge.EVENT_BUS.register(new SpawnHandler());
        NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, SpawnCommand::onRegister);

        LOGGER.info("[TechEvolution] 模组已加载：科技进化怪潮启动。");
    }
}