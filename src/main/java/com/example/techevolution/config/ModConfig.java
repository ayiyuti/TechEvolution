package com.example.techevolution.config;

public class ModConfig {
    public static int scanIntervalTicks = 200;
    public static double timePerDay = 1.0;
    public static double techPerBlocks = 100.0;
    public static double maxDifficulty = 100.0;
    public static double larvaeMax = 20.0;
    public static double matureMax = 50.0;
    public static double smartMax = 80.0;
    public static int scanChunkRadius = 2;
    
    public static double[] healthMul = {1.0, 1.5, 2.0, 3.0};
    public static double[] damageMul = {1.0, 1.5, 2.0, 3.0};
    public static double[] speedMul = {1.0, 1.1, 1.2, 1.3};

    public static boolean naturalSpawn = true;
    public static int spawnIntervalTicks = 600;
    public static double spawnBaseChance = 0.1;

    public static void init() {
        // 如果有实际配置文件，请在这里加载
    }
}