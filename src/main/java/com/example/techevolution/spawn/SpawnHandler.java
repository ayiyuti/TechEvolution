package com.example.techevolution.spawn;

import com.example.techevolution.config.ModConfig;
import com.example.techevolution.difficulty.DifficultyManager;
import com.example.techevolution.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public class SpawnHandler {
    private int timer = 0;

    @SubscribeEvent
    public void onTick(LevelTickEvent.Post event) {
        if (!ModConfig.naturalSpawn) return;
        
        // 修正：LevelTickEvent 的 level 字段是私有的，必须用 getLevel()
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (level.dimension() != Level.OVERWORLD) return;

        if (++timer < ModConfig.spawnIntervalTicks) return;
        timer = 0;

        if (level.isDay()) return;

        double diff = DifficultyManager.getDifficulty();
        double chance = ModConfig.spawnBaseChance * (0.3 + diff / 100.0 * 1.7);
        if (level.getRandom().nextDouble() > chance) return;

        for (ServerPlayer p : level.players()) {
            int n = 1 + level.getRandom().nextInt(3);
            for (int i = 0; i < n; i++) {
                double a = level.getRandom().nextDouble() * Math.PI * 2.0;
                double r = 8.0 + level.getRandom().nextDouble() * 12.0;
                BlockPos base = p.blockPosition().offset((int) (Math.cos(a) * r), 0, (int) (Math.sin(a) * r));
                BlockPos ground = findGround(level, base);
                if (ground != null) {
                    var mob = ModEntities.EVOLVED_MOB.get().create(level, EntitySpawnReason.NATURAL);
                    if (mob != null) {
                        mob.moveTo(ground.getX() + 0.5, ground.getY() + 1, ground.getZ() + 0.5, 0.0F, 0.0F);
                        level.addFreshEntity(mob);
                    }
                }
            }
            break; 
        }
    }

    private BlockPos findGround(ServerLevel level, BlockPos base) {
        int y = Math.min(level.getMaxBuildHeight() - 2, Math.max(level.getMinBuildHeight() + 1, base.getY()));
        BlockPos p = new BlockPos(base.getX(), y, base.getZ());
        for (int i = 0; i < 24; i++) {
            if (level.getBlockState(p).isAir() && level.getBlockState(p.below()).isSolid()) {
                return p;
            }
            p = p.above();
            if (p.getY() >= level.getMaxBuildHeight()) return null;
        }
        return null;
    }
}