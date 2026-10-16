package com.example.techevolution.command;

import com.example.techevolution.difficulty.DifficultyManager;
import com.example.techevolution.entity.EvolvedMob;
import com.example.techevolution.entity.ModEntities;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.IntegerArgumentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySpawnReason;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public class SpawnCommand {

    public static void onRegister(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("techevolution")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.literal("spawn")
                                .executes(ctx -> spawn(ctx.getSource().getPlayerOrException(), 0))
                                .then(Commands.argument("stage", IntegerArgumentType.integer(0, 3))
                                        .executes(ctx -> spawn(ctx.getSource().getPlayerOrException(),
                                                IntegerArgumentType.getInteger(ctx, "stage")))))
                        .then(Commands.literal("info")
                                .executes(ctx -> {
                                    info(ctx.getSource());
                                    return 1;
                                }))
        );
    }

    private static int spawn(ServerPlayer player, int stage) {
        if (player == null) return 0;
        var mob = ModEntities.EVOLVED_MOB.get().create(player.level(), EntitySpawnReason.COMMAND);
        if (mob == null) return 0;
        mob.moveTo(player.getX() + 2.0, player.getY(), player.getZ(), 0.0F, 0.0F);
        if (stage > 0) mob.forceStage(stage);
        player.level().addFreshEntity(mob);
        player.sendSystemMessage(Component.literal("已召唤进化怪物（阶段 " + stage + "）："
                + com.example.techevolution.difficulty.DifficultyStage.fromIndex(stage).cnName));
        return 1;
    }

    private static void info(net.minecraft.commands.CommandSourceStack src) {
        double d = DifficultyManager.getDifficulty();
        String msg = String.format("科技进化怪潮 | 难度=%.1f 阶段=%s 科技方块=%d",
                d, DifficultyManager.getStage().cnName, DifficultyManager.getTechBlockCount());
        src.sendSuccess(() -> Component.literal(msg), false);
    }
}
