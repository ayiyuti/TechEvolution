package com.example.techevolution.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import net.neoforged.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 简易 TOML 配置（基于 NeoForge 自带的 nightconfig）。
 * 所有参数均可在游戏目录 config/techevolution.toml 中调整。
 */
public final class ModConfig {
    private static final Logger LOGGER = LogManager.getLogger("techevolution");
    private static Config config;

    // ===== 难度曲线 =====
    public static double timePerDay = 1.5;       // 每游戏日(24000 tick)增加的难度
    public static double techPerBlocks = 20.0;   // 每多少个科技方块增加 1 点难度
    public static int maxDifficulty = 100;       // 难度上限
    public static int scanIntervalTicks = 6000;  // 科技方块扫描间隔(默认5分钟)
    public static int scanChunkRadius = 2;       // 玩家周围扫描区块半径

    // ===== 阶段阈值(难度值) =====
    public static int larvaeMax = 10;   // <10 幼体
    public static int matureMax = 30;   // 10-30 成熟
    public static int smartMax = 60;    // 30-60 智能；>=60 破坏者

    // ===== 各阶段属性乘数 [幼体,成熟,智能,破坏者] =====
    public static double[] healthMul = {0.6, 1.0, 1.5, 2.2};
    public static double[] damageMul = {0.6, 1.0, 1.5, 2.6};
    public static double[] speedMul  = {0.9, 1.0, 1.1, 1.28};

    // ===== 破坏行为 =====
    public static boolean sabotageEnabled = true;
    public static int sabotageBreakTicks = 60;      // 破坏一个方块所需 tick(默认3秒)
    public static int sabotageSearchRadius = 16;   // 寻找产线方块的半径
    public static int sabotageYRange = 8;           // 上下搜索范围
    public static boolean sabotageDrops = true;     // 破坏时是否掉落物品
    public static int sabotageCooldownTicks = 8;    // 破坏后冷却

    // ===== 自然刷怪 =====
    public static boolean naturalSpawn = true;
    public static int spawnIntervalTicks = 200;     // 尝试刷怪间隔(默认10秒)
    public static double spawnBaseChance = 0.18;    // 基础刷怪概率(随难度倍增)

    // ===== 产线判定 =====
    public static List<String> extraKeywords = new ArrayList<>();
    public static List<String> ignoreBlocks = new ArrayList<>();

    public static void init() {
        Path p = FMLPaths.CONFIGDIR.get().resolve("techevolution.toml");
        config = Config.inMemory();
        if (Files.exists(p)) {
            try {
                config = new TomlParser().parse(Files.newInputStream(p));
            } catch (Exception e) {
                LOGGER.warn("[TechEvolution] 读取 techevolution.toml 失败，使用默认值", e);
                config = Config.inMemory();
            }
        }
        load();
        if (!Files.exists(p)) {
            save(p);
            LOGGER.info("[TechEvolution] 已生成默认配置文件 config/techevolution.toml");
        }
    }

    private static void load() {
        timePerDay = d("difficulty.timePerDay", timePerDay);
        techPerBlocks = d("difficulty.techPerBlocks", techPerBlocks);
        maxDifficulty = i("difficulty.maxDifficulty", maxDifficulty);
        scanIntervalTicks = i("difficulty.scanIntervalTicks", scanIntervalTicks);
        scanChunkRadius = i("difficulty.scanChunkRadius", scanChunkRadius);

        larvaeMax = i("stage.larvaeMax", larvaeMax);
        matureMax = i("stage.matureMax", matureMax);
        smartMax = i("stage.smartMax", smartMax);

        healthMul = arr("stage.healthMul", healthMul);
        damageMul = arr("stage.damageMul", damageMul);
        speedMul = arr("stage.speedMul", speedMul);

        sabotageEnabled = b("sabotage.enabled", sabotageEnabled);
        sabotageBreakTicks = i("sabotage.breakTicks", sabotageBreakTicks);
        sabotageSearchRadius = i("sabotage.searchRadius", sabotageSearchRadius);
        sabotageYRange = i("sabotage.yRange", sabotageYRange);
        sabotageDrops = b("sabotage.drops", sabotageDrops);
        sabotageCooldownTicks = i("sabotage.cooldownTicks", sabotageCooldownTicks);

        naturalSpawn = b("spawn.naturalSpawn", naturalSpawn);
        spawnIntervalTicks = i("spawn.intervalTicks", spawnIntervalTicks);
        spawnBaseChance = d("spawn.baseChance", spawnBaseChance);

        extraKeywords = list("target.extraKeywords", extraKeywords);
        ignoreBlocks = list("target.ignoreBlocks", ignoreBlocks);
    }

    private static void save(Path p) {
        try {
            config.set("difficulty.timePerDay", timePerDay);
            config.set("difficulty.techPerBlocks", techPerBlocks);
            config.set("difficulty.maxDifficulty", maxDifficulty);
            config.set("difficulty.scanIntervalTicks", scanIntervalTicks);
            config.set("difficulty.scanChunkRadius", scanChunkRadius);

            config.set("stage.larvaeMax", larvaeMax);
            config.set("stage.matureMax", matureMax);
            config.set("stage.smartMax", smartMax);
            config.set("stage.healthMul", toList(healthMul));
            config.set("stage.damageMul", toList(damageMul));
            config.set("stage.speedMul", toList(speedMul));

            config.set("sabotage.enabled", sabotageEnabled);
            config.set("sabotage.breakTicks", sabotageBreakTicks);
            config.set("sabotage.searchRadius", sabotageSearchRadius);
            config.set("sabotage.yRange", sabotageYRange);
            config.set("sabotage.drops", sabotageDrops);
            config.set("sabotage.cooldownTicks", sabotageCooldownTicks);

            config.set("spawn.naturalSpawn", naturalSpawn);
            config.set("spawn.intervalTicks", spawnIntervalTicks);
            config.set("spawn.baseChance", spawnBaseChance);

            config.set("target.extraKeywords", extraKeywords);
            config.set("target.ignoreBlocks", ignoreBlocks);

            new TomlWriter().write(config, Files.newOutputStream(p));
        } catch (Exception e) {
            LOGGER.warn("[TechEvolution] 写入配置文件失败", e);
        }
    }

    // ===== 安全读取 =====
    private static int i(String k, int def) {
        Object v = config.get(k);
        return v instanceof Number ? ((Number) v).intValue() : def;
    }
    private static double d(String k, double def) {
        Object v = config.get(k);
        return v instanceof Number ? ((Number) v).doubleValue() : def;
    }
    private static boolean b(String k, boolean def) {
        Object v = config.get(k);
        return v instanceof Boolean ? (Boolean) v : def;
    }
    private static List<String> list(String k, List<String> def) {
        Object v = config.get(k);
        if (v instanceof List) {
            List<String> r = new ArrayList<>();
            for (Object o : (List<?>) v) r.add(String.valueOf(o));
            return r;
        }
        return def;
    }
    private static double[] arr(String k, double[] def) {
        Object v = config.get(k);
        if (v instanceof List) {
            List<?> l = (List<?>) v;
            if (l.size() == def.length) {
                double[] r = new double[l.size()];
                for (int j = 0; j < l.size(); j++) r[j] = ((Number) l.get(j)).doubleValue();
                return r;
            }
        }
        return def;
    }
    private static List<Double> toList(double[] a) {
        List<Double> r = new ArrayList<>();
        for (double v : a) r.add(v);
        return r;
    }
}
