package com.example.techevolution.difficulty;

/**
 * 怪物进化阶段。
 * 0 幼体 / 1 成熟 / 2 智能 / 3 破坏者
 */
public enum DifficultyStage {
    LARVA(0, "幼体", "Evolved Larva"),
    MATURE(1, "成熟体", "Evolved Stalker"),
    SMART(2, "智能体", "Evolved Weaver"),
    SABOTEUR(3, "破坏者", "Production Reaper");

    public final int index;
    public final String cnName;
    public final String enName;

    DifficultyStage(int index, String cnName, String enName) {
        this.index = index;
        this.cnName = cnName;
        this.enName = enName;
    }

    public static DifficultyStage fromIndex(int idx) {
        for (DifficultyStage s : values()) if (s.index == idx) return s;
        return LARVA;
    }

    public boolean isAtLeast(DifficultyStage other) {
        return this.index >= other.index;
    }
}
