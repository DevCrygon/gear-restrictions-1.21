package net.crygon.gearrestrictions;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Formatting;

import java.util.Collection;
import java.util.Objects;

public class ArmourEquipHandler {

    private static ScoreboardObjective killObjective;

    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient && player instanceof ServerPlayerEntity serverPlayer) {
                checkArmour(serverPlayer);
            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                checkArmour(player);
            }
        });
    }

    private static void checkArmour(ServerPlayerEntity player) {
        int killCount = getKillCountFromScoreboard(player);

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack armorPiece = player.getEquippedStack(slot);
            if (armorPiece.getItem() instanceof ArmorItem armorItem) {
                if (!canEquipArmor(killCount, armorItem, player)) {
                    player.equipStack(slot, ItemStack.EMPTY); // Remove the armor
                    player.getInventory().insertStack(armorPiece);
                    player.sendMessage(Text.literal("You haven't unlocked this armor yet!").formatted(Formatting.DARK_RED), true);
                }
            }
        }
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

    private static boolean canEquipArmor(int killCount, ArmorItem armorItem, ServerPlayerEntity player) {
        // Check if the player has a bypass
        String[] bypassPlayers = {"CrygonSupreme", "wMango", "MrMeow500"};
        for (String bypassPlayer : bypassPlayers) {
            if (player.getName().equals(bypassPlayer)) {
                return true; // Bypass armor restrictions for these players
            }
        }

        // Armor restrictions based on kill count

        // 0 kills: Iron armor is always allowed
        if (armorItem.equals(Items.IRON_BOOTS) || armorItem.equals(Items.IRON_CHESTPLATE) ||
                armorItem.equals(Items.IRON_LEGGINGS) || armorItem.equals(Items.IRON_HELMET)) {
            return true;
        }

        // Diamond and Netherite armor unlock progression based on kill count
        return switch (killCount) {
            case 1 -> armorItem.equals(Items.DIAMOND_BOOTS);
            case 2 -> armorItem.equals(Items.DIAMOND_BOOTS) || armorItem.equals(Items.DIAMOND_HELMET);
            case 3 -> armorItem.equals(Items.DIAMOND_BOOTS) || armorItem.equals(Items.DIAMOND_HELMET) || armorItem.equals(Items.DIAMOND_LEGGINGS);
            case 4, 5 -> armorItem.equals(Items.DIAMOND_CHESTPLATE) || armorItem.equals(Items.DIAMOND_BOOTS) || armorItem.equals(Items.DIAMOND_LEGGINGS) || armorItem.equals(Items.DIAMOND_HELMET);
            case 6 -> armorItem.equals(Items.DIAMOND_CHESTPLATE) || armorItem.equals(Items.DIAMOND_BOOTS) || armorItem.equals(Items.DIAMOND_LEGGINGS) || armorItem.equals(Items.DIAMOND_HELMET) || armorItem.equals(Items.NETHERITE_BOOTS);
            case 7, 8 -> armorItem.equals(Items.DIAMOND_CHESTPLATE) || armorItem.equals(Items.DIAMOND_BOOTS) || armorItem.equals(Items.DIAMOND_LEGGINGS) || armorItem.equals(Items.DIAMOND_HELMET) || armorItem.equals(Items.NETHERITE_BOOTS) || armorItem.equals(Items.NETHERITE_HELMET);
            case 9, 10 -> armorItem.equals(Items.DIAMOND_CHESTPLATE) || armorItem.equals(Items.DIAMOND_BOOTS) || armorItem.equals(Items.DIAMOND_LEGGINGS) || armorItem.equals(Items.DIAMOND_HELMET) || armorItem.equals(Items.NETHERITE_BOOTS) || armorItem.equals(Items.NETHERITE_HELMET) || armorItem.equals(Items.NETHERITE_LEGGINGS);
            default -> killCount >= 11 && (armorItem.equals(Items.DIAMOND_CHESTPLATE) || armorItem.equals(Items.DIAMOND_BOOTS) || armorItem.equals(Items.DIAMOND_LEGGINGS) || armorItem.equals(Items.DIAMOND_HELMET) || armorItem.equals(Items.NETHERITE_CHESTPLATE) || armorItem.equals(Items.NETHERITE_BOOTS) || armorItem.equals(Items.NETHERITE_LEGGINGS) || armorItem.equals(Items.NETHERITE_HELMET));
        };
    }
}
