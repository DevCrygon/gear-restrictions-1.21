package net.crygon.gearrestrictions;

import com.mojang.brigadier.ParseResults;
import it.unimi.dsi.fastutil.ints.Int2IntSortedMaps;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.scoreboard.*;
import net.minecraft.scoreboard.number.FixedNumberFormat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.server.command.ExecuteCommand;

import java.util.Collection;
import java.util.Objects;

public class GearRestrictions implements ModInitializer {

	@Override
	public void onInitialize() {
		KillTracker.register();
		ArmourEquipHandler.register();
		CommandRegistrationCallback.EVENT.register(ThanksDevCommand::register);
	}

	public static void createKillScoreboard(ServerPlayerEntity player, Boolean reset) {
		MinecraftServer server = player.getServer(); // Obtain server from player entity
		if (server == null) {
			System.out.println("Server is null, cannot create scoreboard.");
			return;
		}
		CommandManager commandManager = player.getServer().getCommandManager();
		ServerCommandSource commandSource = player.getServer().getCommandSource();

		if (reset) {
			commandManager.executeWithPrefix(commandSource, "scoreboard players reset " + player.getName() + " playerKillCount");
		}
		else {
			commandManager.executeWithPrefix(commandSource, "scoreboard objectives add playerKillCount playerKillCount");
		}




		}

	}
