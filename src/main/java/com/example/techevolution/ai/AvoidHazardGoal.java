package com.example.techevolution.ai;

import com.example.techevolution.entity.EvolvedMob;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumSet;

/** 智能阶段解锁：检测脚下/前方危险方块（岩浆/火/仙人掌）并远离。 */
public class AvoidHazardGoal extends Goal {
    private final EvolvedMob mob;
    private int cooldown = 0;

    public AvoidHazardGoal(EvolvedMob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return isOnHazard() && cooldown <= 0;
    }

    @Override
    public void start() {
        cooldown = 40;
        double dx = mob.getX() + (mob.getRandom().nextDouble() - 0.5) * 8.0;
        double dz = mob.getZ() + (mob.getRandom().nextDouble() - 0.5) * 8.0;
        mob.getNavigation().moveTo(dx, mob.getY(), dz, 1.25);
    }

    @Override
    public boolean canContinueToUse() {
        return cooldown > 0;
    }

    @Override
    public void tick() {
        if (cooldown > 0) {
            cooldown--;
            if (cooldown <= 0) mob.getNavigation().stop();
        }
    }

    private boolean isOnHazard() {
        BlockPos pos = mob.blockPosition();
        for (int i = 0; i <= 1; i++) {
            BlockState st = mob.level().getBlockState(pos.below(i));
            if (st.is(Blocks.LAVA) || st.is(Blocks.FIRE) || st.is(Blocks.SOUL_FIRE)
                    || st.getBlock() instanceof CactusBlock) {
                return true;
            }
        }
        BlockPos front = pos.offset(
                (int) Math.signum(mob.getForward().x),
                0,
                (int) Math.signum(mob.getForward().z));
        BlockState fs = mob.level().getBlockState(front);
        return fs.is(Blocks.LAVA) || fs.is(Blocks.FIRE) || fs.getBlock() instanceof CactusBlock;
    }
}
