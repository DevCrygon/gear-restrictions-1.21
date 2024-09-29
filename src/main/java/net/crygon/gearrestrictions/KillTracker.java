package net.crygon.gearrestrictions;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.UUID;

public class KillTracker {
    // Store the kill counts for each player (identified by UUID)
    private static final HashMap<UUID, Integer> playerKillCounts = new HashMap<>();

    public static void register() {
        // Register entity death event to track when a player kills an entity
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
            if (damageSource.getSource() instanceof PlayerEntity killer) {
                if (killer instanceof ServerPlayerEntity) {
                    increaseKillCount((ServerPlayerEntity) killer);
                }
            }
        });
    }

    // Increase the player's kill count
    private static void increaseKillCount(ServerPlayerEntity player) {
        UUID playerUUID = player.getUuid();
        int currentKills = playerKillCounts.getOrDefault(playerUUID, 0);
        playerKillCounts.put(playerUUID, currentKills + 1); // Increment kill count
        player.sendMessage(Text.literal("Your kill count is now: " + (currentKills + 1)), true);

        // Update scoreboard
        Scoreboard scoreboard = player.getServerWorld().getScoreboard();
        ScoreboardObjective objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.valueOf("killCount"));
        scoreboard.getOrCreateScore(player, objective).setScore(currentKills + 1);
    }

    // Get the player's kill count (used for armor checks)
    public static int getKillCount(ServerPlayerEntity player) {
        return playerKillCounts.getOrDefault(player.getUuid(), 0);
    }
}
