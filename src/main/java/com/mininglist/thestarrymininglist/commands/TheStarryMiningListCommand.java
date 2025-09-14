package com.mininglist.thestarrymininglist.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.server.command.ServerCommandSource;
//#if MC>12001
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
//#endif
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import static com.mininglist.thestarrymininglist.TheStarryMiningList.mScoreboard;
import static com.mininglist.thestarrymininglist.TheStarryMiningList.mScoreboardObj;

public class TheStarryMiningListCommand {
    public static boolean isScoreboardVisible = true;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("theStarryMiningList")
                .then(argument("mode", BoolArgumentType.bool())
                        .executes(context -> {
                            String ret_msg;
                            if (context.getSource().hasPermissionLevel(1)) {
                                isScoreboardVisible = BoolArgumentType.getBool(context, "mode");
                                isVisible();
                                ret_msg = "已设置挖掘榜显示状态为: " + (isScoreboardVisible ? "开启" : "关闭");
                            } else {
                                ret_msg = "权限不足";
                            }
                            Objects.requireNonNull(context.getSource().getPlayer()).sendMessage(Text.of(ret_msg), true);
                            return 1;
                        }))
        );
    }
    public static void registerSingleCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("theStarryMiningDisplay")
                .then(argument("mode", BoolArgumentType.bool())
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            if (player == null)
                            {
                                return 1;
                            }

                            boolean is_display = BoolArgumentType.getBool(context, "mode");

                            setSinglePlayerDisplayState(context.getSource().getPlayer(), is_display);

                            String ret_msg = "你的计分板显示状态已设置为: " + (is_display ? "开启" : "关闭");
                            context.getSource().getPlayer().sendMessage(Text.of(ret_msg), true);
                            return 1;
                        }))
        );
    }


    public static void isVisible() {
        if (isScoreboardVisible) {
            mScoreboard.setObjectiveSlot(ScoreboardDisplaySlot.SIDEBAR, mScoreboardObj);

        } else {
            mScoreboard.setObjectiveSlot(ScoreboardDisplaySlot.SIDEBAR, null);
        }
    }

    public static void setSinglePlayerDisplayState(ServerPlayerEntity player, boolean state) {
        if (state) {
            player.networkHandler.sendPacket(new ScoreboardDisplayS2CPacket(ScoreboardDisplaySlot.SIDEBAR, mScoreboardObj));
        }
        else {
            player.networkHandler.sendPacket(new ScoreboardDisplayS2CPacket(ScoreboardDisplaySlot.SIDEBAR, null));
        }
    }
}
