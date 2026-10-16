package com.example.techevolution.difficulty;

public enum DifficultyStage {
    LARVAE(0, "幼虫"),
    MATURE(1, "成熟体"),
    SMART(2, "智能体"),
    SABOTEUR(3, "破坏者");

    public final int index;
    public final String cnName;

    DifficultyStage(int index, String cnName) {
        this.index = index;
        this.cnName = cnName;
    }

    public static DifficultyStage fromIndex(int idx) {
        for (DifficultyStage s : values()) {
            if (s.index == idx) return s;
        }
        return LARVAE;
    }
}