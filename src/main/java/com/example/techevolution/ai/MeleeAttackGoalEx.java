package com.example.techevolution.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.monster.Monster;

/** 强化近战：与原版一致，但独立成类便于后续调整攻击节奏/范围。 */
public class MeleeAttackGoalEx extends MeleeAttackGoal {
    public MeleeAttackGoalEx(Monster mob, double speed, boolean pauseWhenIdle) {
        super(mob, speed, pauseWhenIdle);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.mob.getTarget();
        if (target == null) return false;
        if (!target.isAlive()) return false;
        return super.canContinueToUse();
    }
}
