package com.example.techevolution.ai;

import com.example.techevolution.capability.ProductionTarget;
import com.example.techevolution.config.ModConfig;
import com.example.techevolution.entity.EvolvedMob;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

/**
 * 破坏者阶段的核心 AI：第一目标是玩家的产线。
 * 寻找最近的物流/机器/能源方块 -> 寻路 -> 带进度破坏。
 * 若玩家贴脸则让位给近战；若附近没有产线则自动让位，恢复普通攻击 AI（不会发呆）。
 */
public class SabotageGoal extends Goal {
    private final EvolvedMob mob;
    private BlockPos target = null;
    private int searchCd = 0;
    private int breakProgress = 0;
    private int canUseCd = 0;
    private boolean cachedHasTarget = false;

    public SabotageGoal(EvolvedMob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!ModConfig.sabotageEnabled) return false;
        LivingEntity t = mob.getTarget();
        if (t != null && mob.distanceTo(t) < 3.0) return false; // 玩家贴脸 -> 让位攻击
        if (canUseCd > 0) {
            canUseCd--;
            return cachedHasTarget;
        }
        canUseCd = 40;
        cachedHasTarget = findNearest() != null; // 缓存“附近有无产线”，避免每 tick 全扫描
        return cachedHasTarget;
    }

    @Override
    public void start() {
        target = findNearest();
        breakProgress = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return target != null;
    }

    @Override
    public void stop() {
        target = null;
        breakProgress = 0;
    }

    @Override
    public void tick() {
        if (searchCd > 0) searchCd--;

        // 校验目标方块是否仍是产线
        if (target != null) {
            BlockState st = mob.level().getBlockState(target);
            if (!ProductionTarget.isProductionBlock(st)) {
                target = null;
                return;
            }
        }

        // 周期性寻找最近产线方块
        if (target == null) {
            if (searchCd <= 0) {
                target = findNearest();
                searchCd = 30;
            }
            if (target == null) return;
        }

        int distSq = mob.blockPosition().distSqr(target);
        if (distSq <= 4) {
            // 相邻：累积破坏进度
            breakProgress++;
            if (breakProgress >= ModConfig.sabotageBreakTicks) {
                mob.level().destroyBlock(target, ModConfig.sabotageDrops);
                target = null;
                breakProgress = 0;
                searchCd = ModConfig.sabotageCooldownTicks;
            }
        } else {
            // 走向目标
            double speed = 1.15;
            mob.getNavigation().moveTo(target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, speed);
        }
    }

    private BlockPos findNearest() {
        BlockPos center = mob.blockPosition();
        int r = ModConfig.sabotageSearchRadius;
        int yr = ModConfig.sabotageYRange;
        BlockPos best = null;
        int bestD = Integer.MAX_VALUE;
        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -yr; dy <= yr; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    BlockPos p = center.offset(dx, dy, dz);
                    if (!mob.level().hasChunkAt(p)) continue;
                    BlockState st = mob.level().getBlockState(p);
                    if (ProductionTarget.isProductionBlock(st)) {
                        int d = center.distSqr(p);
                        if (d < bestD) { bestD = d; best = p; }
                    }
                }
            }
        }
        return best;
    }
}
