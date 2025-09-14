package com.mininglist.thestarrymininglist.event;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;

//#if MC>=12003
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreAccess;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardScore;
//#else
//$$ import net.minecraft.scoreboard.ScoreboardPlayerScore;
//#endif

import static com.mininglist.thestarrymininglist.TheStarryMiningList.mScoreboard;
import static com.mininglist.thestarrymininglist.TheStarryMiningList.mScoreboardObj;

public class HookPlayerBreakBlockEvent {
    public static void hook() {
        PlayerBlockBreakEvents.AFTER.register(((world,
                                                player,
                                                pos,
                                                state,
                                                blockEntity) -> {
            ScoreAccess access = mScoreboard.getOrCreateScore(player, mScoreboardObj);
            int score = access.getScore();
            score++;
            access.setScore(score);
        }));
    }
}
