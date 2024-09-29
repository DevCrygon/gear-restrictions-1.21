package net.crygon.gearrestrictions;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.text.Text;

import static java.sql.Types.INTEGER;

public class GearRestrictions implements ModInitializer {
	@Override
	public void onInitialize() {
		// Register the kill tracker
		KillTracker.register();

		// Register the armor equip handler
		ArmourEquipHandler.register();

		// Create a scoreboard objective for kills
		createKillScoreboard();
	}

	private void createKillScoreboard() {
		assert MinecraftClient.getInstance().player != null;
		Scoreboard scoreboard = MinecraftClient.getInstance().player.getScoreboard();
		scoreboard.addObjective("killCount", ScoreboardCriterion.DUMMY, Text.literal("Kill Count"), ScoreboardCriterion.RenderType.INTEGER, true, null);
	}
}
