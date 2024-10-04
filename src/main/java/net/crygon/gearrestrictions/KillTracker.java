package net.crygon.gearrestrictions;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.*;
import net.minecraft.scoreboard.number.FixedNumberFormat;
import net.minecraft.scoreboard.number.NumberFormat;
import net.minecraft.scoreboard.number.NumberFormatType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.GameRules;

import java.awt.*;
import java.text.Format;
import java.util.*;
import java.util.List;

import static net.minecraft.text.Text.literal;
import static net.minecraft.text.Text.score;

public class KillTracker {

    public static Scoreboard scoreboard;

    private static ScoreboardObjective killObjective;

    // Constructor initializes the scoreboard and adds the "playerKillCount" objective
    public KillTracker(MinecraftServer server) {
        KillTracker.scoreboard = server.getScoreboard();
        Collection<ScoreboardObjective> objectives = scoreboard.getObjectives();

        for (ScoreboardObjective objective : objectives) {
            if (objective.getName().equals("playerKillCount")) {
                playerKillCountObjective = objective;
                break;
            }
        }
        if (playerKillCountObjective == null) {
            // Handle the case where the objective is not found
            System.out.println("Not Found bozo");
        }

    }

    static ScoreboardObjective playerKillCountObjective = new ScoreboardObjective(KillTracker.scoreboard, "playerKillCount", ScoreboardCriterion.PLAYER_KILL_COUNT, Text.literal("playerKillCount"), ScoreboardCriterion.RenderType.INTEGER, true, new FixedNumberFormat(Text.literal(".1")));


    public static void register() {
        // Track kills
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (damageSource.getSource() instanceof PlayerEntity killer && entity instanceof PlayerEntity) {
                if (killer instanceof ServerPlayerEntity) {
                    increaseKillCount((ServerPlayerEntity) killer);
                }
            }
        });

        // Reset kill count on player death
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (entity instanceof ServerPlayerEntity) {
                resetKillCount((ServerPlayerEntity) entity);
            }
        });
    }

    // Increase the player's kill count
    private static void increaseKillCount(ServerPlayerEntity player) {
        if (KillTracker.scoreboard != null) {
            // Increment the current kill count first
            int currentKills = getKillCountFromScoreboard(player);

            // Display messages in the action bar
            switch (currentKills) {
                case 1 -> player.sendMessage(literal("You have unlocked Diamond Boots!").setStyle(literal(" ").getStyle().withBold(true)), true);
                case 2 -> player.sendMessage(literal("You have unlocked Diamond Helmet!").setStyle(literal(" ").getStyle().withBold(true)), true);
                case 3 -> player.sendMessage(literal("You have unlocked Diamond Leggings!").setStyle(literal(" ").getStyle().withBold(true)), true);
                case 4 -> player.sendMessage(literal("You have unlocked Full Diamond Armor!").setStyle(literal(" ").getStyle().withBold(true)), true);
                case 6 -> player.sendMessage(literal("You have unlocked Netherite Boots!").setStyle(literal(" ").getStyle().withBold(true)), true);
                case 7 -> player.sendMessage(literal("You have unlocked Netherite Helmet!").setStyle(literal(" ").getStyle().withBold(true)), true);
                case 9 -> player.sendMessage(literal("You have unlocked Netherite Leggings!").setStyle(literal(" ").getStyle().withBold(true)), true);
                case 11 -> player.sendMessage(literal("You have unlocked Full Netherite Armor!").setStyle(literal(" ").getStyle().withBold(true)), true);
            }

            // Update the player's scoreboard
            GearRestrictions.createKillScoreboard(player, false);
        } else {
            GearRestrictions.createKillScoreboard(player, false);
        }
    }

    // Reset the player's kill count
    public static void resetKillCount(ServerPlayerEntity player) {
        if (KillTracker.scoreboard == null) {
            // If scoreboard is null, initialize it first
            KillTracker.scoreboard = Objects.requireNonNull(player.getServer()).getScoreboard();
            GearRestrictions.createKillScoreboard(player, true);
        }

        // Now that scoreboard is guaranteed to be non-null, we proceed
        ScoreAccess score = KillTracker.scoreboard.getOrCreateScore(player, playerKillCountObjective);
        score.setScore(0);
        player.sendMessage(literal("Your kill count has been reset!"), true);
    }
    private static int getKillCountFromScoreboard(ServerPlayerEntity player) {
        var scoreboard = Objects.requireNonNull(player.getServer()).getScoreboard();

        var objectives = scoreboard.getObjectives();
        for (ScoreboardObjective objective : objectives) {
            if (objective.getName().equals("playerKillCount")) {
                killObjective = objective;
                break;
            }
        }

        if (killObjective == null) {
            GearRestrictions.createKillScoreboard(player, false);
            return 0; // Return 0 if the objective isn't found
        }

        var score = player.getScoreboard().getOrCreateScore(player, killObjective);
        return score.getScore();
    }

}