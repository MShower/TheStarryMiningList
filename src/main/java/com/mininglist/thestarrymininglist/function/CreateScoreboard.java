package com.mininglist.thestarrymininglist.function;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

//#if MC>=12002
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
//#endif

import net.minecraft.scoreboard.ScoreboardCriterion;
//#if MC<11904
//$$ import net.minecraft.text.LiteralText;
//#else
import net.minecraft.text.Text;
//#endif

import net.minecraft.world.World;

import java.util.Objects;

import static com.mininglist.thestarrymininglist.TheStarryMiningList.mScoreboard;
import static com.mininglist.thestarrymininglist.TheStarryMiningList.mScoreboardObj;

public class CreateScoreboard {
    public static void create(final String name, final String display_name) {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
                    mScoreboard = Objects.requireNonNull(server.getWorld(World.OVERWORLD)).getScoreboard();
                    mScoreboardObj = mScoreboard.getNullableObjective(name);
                    if (mScoreboardObj == null) {
                        mScoreboardObj = mScoreboard.addObjective(
                                name,
                                ScoreboardCriterion.DUMMY,
                                Text.literal(display_name),
                                ScoreboardCriterion.RenderType.INTEGER,
                                true,
                                null);
                        mScoreboard.setObjectiveSlot(ScoreboardDisplaySlot.SIDEBAR, null);
                    }
                    else {
                        mScoreboardObj.setDisplayName(Text.literal(display_name));
                    }
                }
        );
    }
}
