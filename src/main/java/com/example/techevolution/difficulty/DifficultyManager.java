package com.example.techevolution.difficulty;
// ... (保留原有的 import)
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public class DifficultyManager {
    // ... (保留原有变量)

    @SubscribeEvent
    public void onServerTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return; // 修改这里
        if (level.dimension() != Level.OVERWORLD) return;
        if (level.getGameTime() % ModConfig.scanIntervalTicks != 0) return;
        recompute(level);
    }
    // ... (保留原有方法)
}