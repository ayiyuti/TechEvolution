package com.example.techevolution.entity;

import com.example.techevolution.TechEvolution;
import com.example.techevolution.ai.AvoidHazardGoal;
import com.example.techevolution.ai.MeleeAttackGoalEx;
import com.example.techevolution.ai.SabotageGoal;
import com.example.techevolution.config.ModConfig;
import com.example.techevolution.difficulty.DifficultyManager;
import com.example.techevolution.difficulty.DifficultyStage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

// 这里改为继承 Zombie，才能和渲染器的 ZombieModel 匹配
public class EvolvedMob extends Zombie {
    private int stage = 0;
    private boolean initialized = false;
    private int evoCheck = 0;
    private int sabotageAdded = -1;

    public EvolvedMob(EntityType<? extends EvolvedMob> type, Level level) {
        super(type, level);
        this.xpReward = 5;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes(); 
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(2, new MeleeAttackGoalEx(this, 1.0, false));
        this.goalSelector.addGoal(4, new RandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) return;

        if (!initialized) {
            this.stage = DifficultyManager.getStageIndex();
            applyStage(this.stage);
            this.initialized = true;
        }

        if (++evoCheck >= 1200) {
            evoCheck = 0;
            int global = DifficultyManager.getStageIndex();
            if (global > this.stage) {
                this.stage = global;
                applyStage(this.stage);
                TechEvolution.LOGGER.debug("[TechEvolution] 一个进化怪物进化到阶段 {}", this.stage);
            }
        }
    }

    private void applyStage(int s) {
        if (s < 0 || s > 3) s = 0;
        double hm = safe(ModConfig.healthMul, s, 1.0);
        double dm = safe(ModConfig.damageMul, s, 1.0);
        double sm = safe(ModConfig.speedMul, s, 1.0);

        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(20.0 * hm);
        this.setHealth(this.getMaxHealth());
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(3.0 * dm);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.23 * sm);

        if (s >= DifficultyStage.SMART.index && sabotageAdded < DifficultyStage.SMART.index) {
            this.goalSelector.addGoal(3, new AvoidHazardGoal(this));
            sabotageAdded = DifficultyStage.SMART.index;
        }
        if (s >= DifficultyStage.SABOTEUR.index && sabotageAdded < DifficultyStage.SABOTEUR.index) {
            this.goalSelector.addGoal(1, new SabotageGoal(this));
            sabotageAdded = DifficultyStage.SABOTEUR.index;
        }
    }

    private static double safe(double[] arr, int idx, double def) {
        return (arr != null && idx < arr.length) ? arr[idx] : def;
    }

    public void forceStage(int s) {
        this.stage = s;
        this.initialized = true;
        applyStage(s);
    }

    @Override
    public Component getName() {
        return Component.literal(DifficultyStage.fromIndex(this.stage).cnName);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("te_stage", this.stage);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.stage = tag.getInt("te_stage");
        this.initialized = true;
        if (!this.level().isClientSide) applyStage(this.stage);
    }
}