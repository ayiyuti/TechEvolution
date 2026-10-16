package com.example.techevolution.capability;

import com.example.techevolution.config.ModConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

/**
 * 产线目标判定。
 * 通过「注册名 token 关键词匹配 + 配置额外关键词 - 忽略名单」识别物流/机器/能源类方块。
 */
public final class ProductionTarget {
    private static final Set<String> KEYWORDS = new HashSet<>();
    static {
        String[] ks = {
            "machine", "machines", "pipe", "pipes", "cable", "cables", "wire", "wires",
            "conduit", "conduits", "generator", "generators", "motor", "engines", "engine",
            "pump", "pumps", "assembler", "assemblers", "smelter", "smelters", "factory",
            "factories", "miner", "miners", "crusher", "crushers", "centrifuge", "compressor",
            "compressors", "transmitter", "transmitters", "acceptor", "exporter", "importer",
            "flux", "reactor", "reactors", "turbine", "turbines", "boiler", "dynamo", "cell",
            "battery", "batteries", "brewery", "mixer", "mixing", "grinder", "solar", "windmill",
            "steam", "kinetic", "gearbox", "belt", "belts", "funnel", "extractor", "inserter",
            "inserters", "transposer", "mechanical", "hydraulic", "vacuum", "cobblestone",
            "quarry", "drill", "drills", "laser", "lasers", "controller", "interface",
            "monitor", "autocraft", "pattern", "terminal", "encoder", "blockbreaker",
            "blockplacer", "deployer", "drive", "logicgate", "integrated", "programmable",
            "sequential", "redstone", "ae2", "create", "mek", "enderio", "pipez"
        };
        for (String k : ks) KEYWORDS.add(k);
    }

    public static boolean isProductionBlock(BlockState state) {
        if (state == null || state.isAir()) return false;
        ResourceLocation rl = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        if (rl == null) return false;
        String name = rl.toString();

        for (String ig : ModConfig.ignoreBlocks) {
            if (name.equals(ig) || name.endsWith(":" + ig)) return false;
        }

        String[] tokens = name.split("[:_]");
        for (String t : tokens) {
            if (KEYWORDS.contains(t)) return true;
        }
        for (String ek : ModConfig.extraKeywords) {
            if (!ek.isEmpty() && name.contains(ek)) return true;
        }
        return false;
    }
}
