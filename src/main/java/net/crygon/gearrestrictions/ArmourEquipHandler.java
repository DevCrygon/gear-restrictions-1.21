package net.crygon.gearrestrictions;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TypedActionResult;

public class ArmourEquipHandler {

    public static void register() {
        // Register the event callback for armor equip checks
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient) {
                if (player instanceof ServerPlayerEntity) {
                    checkArmour((ServerPlayerEntity) player);
                }
            }
            return TypedActionResult.pass(ItemStack.EMPTY);
        });
    }

    // Check if the player is allowed to equip specific armor based on their kill count
    private static void checkArmour(ServerPlayerEntity player) {
        int kills = KillTracker.getKillCount(player); // Get the player's kill count

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.BODY.getType() ||
                    slot.getType() == EquipmentSlot.HEAD.getType()  ||
                    slot.getType() == EquipmentSlot.CHEST.getType()  ||
                    slot.getType() == EquipmentSlot.LEGS.getType() ||
                    slot.getType() == EquipmentSlot.FEET.getType()) {
                ArmorItem armorItem = getArmorFromSlot(player, slot);

                // Determine if the player is allowed to equip the item based on their kill count
                if (armorItem != null) {
                    if (!canEquipArmor(kills, armorItem)) {
                        player.sendMessage(Text.literal("You need more kills to equip this armor!"), true);
                        player.equipStack(slot, ItemStack.EMPTY); // Remove the armor
                    } else {
                        sendUnlockMessage(player, armorItem, kills);
                    }
                }
            }
        }
    }

    // Determine if the player can equip armor based on their kill count
    private static boolean canEquipArmor(int kills, ArmorItem armorItem) {
        // Diamond Boots (1 kill)
        if (armorItem == Items.DIAMOND_BOOTS && kills >= 1) return true;

        // Diamond Helmet (2 kills)
        if (armorItem == Items.DIAMOND_HELMET && kills >= 2) return true;

        // Diamond Leggings (3 kills)
        if (armorItem == Items.DIAMOND_LEGGINGS && kills >= 3) return true;

        // Full Diamond Armor (4+ kills)
        if (armorItem == Items.DIAMOND_CHESTPLATE && kills >= 4) return true;

        // Netherite Boots (6 kills)
        if (armorItem == Items.NETHERITE_BOOTS && kills >= 6) return true;

        // Netherite Helmet (7 kills)
        if (armorItem == Items.NETHERITE_HELMET && kills >= 7) return true;

        // Netherite Leggings (9 kills)
        if (armorItem == Items.NETHERITE_LEGGINGS && kills >= 9) return true;

        // Full Netherite Armor (11 kills)
        if (armorItem == Items.NETHERITE_CHESTPLATE && kills >= 11) return true;

        // Armor is too strong for the player's current kill count
        return false;
    }

    // Helper to get armor item from a player's armor slot
    private static ArmorItem getArmorFromSlot(PlayerEntity player, EquipmentSlot slot) {
        ItemStack equippedStack = player.getEquippedStack(slot);
        if (equippedStack.getItem() instanceof ArmorItem) {
            return (ArmorItem) equippedStack.getItem();
        }
        return null;
    }

    // Send an action bar message for unlocked armor
    private static void sendUnlockMessage(ServerPlayerEntity player, ArmorItem armorItem, int kills) {
        String unlockMessage = "You have unlocked: " + armorItem.getName(armorItem.getDefaultStack()).getString();
        player.sendMessage(Text.literal(unlockMessage).setStyle(Style.EMPTY.withBold(true)), false);
    }
}
