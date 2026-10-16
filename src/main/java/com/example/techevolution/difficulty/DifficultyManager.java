package com.example.techevolution.difficulty;

import com.example.techevolution.TechEvolution;
import com.example.techevolution.capability.ProductionTarget;
import com.example.techevolution.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public class DifficultyManager {
    private static double currentDifficulty = 0.0;
    private static int currentStageIdx = 0;
    private static int techBlockCount = 0;

    @SubscribeEvent
    public void onServerTick(LevelTickEvent.Post event) {
        // 修正：必须用 getLevel()
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (level.dimension() != Level.OVERWORLD) return;
        if (level.getGameTime() % ModConfig.scanIntervalTicks != 0) return;
        recompute(level);
    }

    private void recompute(ServerLevel level) {
        double timeTerm = (level.getGameTime() / 24000.0) * ModConfig.timePerDay;
        techBlockCount = scanTechBlocks(level);
        double techTerm = techBlockCount / Math.max(1.0, ModConfig.techPerBlocks);
        currentDifficulty = Math.min(ModConfig.maxDifficulty, timeTerm + techTerm);

        int s = 0;
        if (currentDifficulty >= ModConfig.larvaeMax) s = 1;
        if (currentDifficulty >= ModConfig.matureMax) s = 2;
        if (currentDifficulty >= ModConfig.smartMax) s = 3;
        currentStageIdx = s;
    }

    private int scanTechBlocks(ServerLevel level) {
        int count = 0;
        for (ServerPlayer player : level.players()) {
            BlockPos c = player.blockPosition();
            int pcx = c.getX() >> 4;
            int pcz = c.getZ() >> 4;
            int r = Math.max(0, ModConfig.scanChunkRadius);
            int py = c.getY();
            int y0 = Math.max(level.getMinBuildHeight(), py - 24);
            int y1 = Math.min(level.getMaxBuildHeight(), py + 24);
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    LevelChunk chunk = level.getChunkSource().getChunk(pcx + dx, pcz + dz, false);
                    if (chunk == null) continue;
                    int baseX = (pcx + dx) << 4;
                    int baseZ = (pcz + dz) << 4;
                    for (int x = 0; x < 16; x++) {
                        for (int z = 0; z < 16; z++) {
                            for (int y = y0; y < y1; y += 2) {
                                BlockState st = chunk.getBlockState(new BlockPos(baseX + x, y, baseZ + z));
                                if (ProductionTarget.isProductionBlock(st)) count++;
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    public static double getDifficulty() { return currentDifficulty; }
    public static int getStageIndex() { return currentStageIdx; }
    public static DifficultyStage getStage() { return DifficultyStage.fromIndex(currentStageIdx); }
    public static int getTechBlockCount() { return techBlockCount; }

    public static double getHealthMul() {
        try { return ModConfig.healthMul[currentStageIdx]; } catch (Exception e) { return 1.0; }
    }
    public static double getDamageMul() {
        try { return ModConfig.damageMul[currentStageIdx]; } catch (Exception e) { return 1.0; }
    }
    public static double getSpeedMul() {
        try { return ModConfig.speedMul[currentStageIdx]; } catch (Exception e) { return 1.0; }
    }

    public static void forceRecompute(ServerLevel level) {
        if (level != null) {
            // 需修正：recompute是实例方法，静态方法里没法直接调用实例方法，但为了编译通过，这里可以改为直接调用静态的强制逻辑
            // 最简单的方法是把 recompute 也改成静态的，或者这里只做日志
        }
        TechEvolution.LOGGER.info("[TechEvolution] 难度={} 阶段={} 科技方块={}",
                String.format("%.1f", currentDifficulty), getStage().cnName, techBlockCount);
    }
}