package net.crygon.gearrestrictions;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

public class ArmourEquipHandler {

    public static void register() {
        // Register the event for use actions (clicking, equipping, etc.)
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient) {
                checkArmor(player);
            }
            return new TypedActionResult<>(ActionResult.PASS, player.getStackInHand(hand));
        });

        // Register the tick event to check for armor changes in the inventory
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                checkArmor(player);
            }
        });
    }

    public static boolean isArmorSlot(EquipmentSlot slot) {
        return slot == EquipmentSlot.CHEST ||
                slot == EquipmentSlot.FEET ||
                slot == EquipmentSlot.HEAD ||
                slot == EquipmentSlot.LEGS;
    }
    // Check if the armor being equipped is stronger than iron
    private static void checkArmor(PlayerEntity player) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            // Only check armor slots (head, chest, legs, boots)
            if (isArmorSlot(slot)) {
                ItemStack armorPiece = player.getEquippedStack(slot);

                if (armorPiece.getItem() instanceof ArmorItem armorItem) {

                    // Check if the armor is made of stronger material than iron
                    if (isStrongerThanIron(armorItem)) {
                        // Send message to the player and return armor to the previous slot
                        player.sendMessage(Text.literal("You cannot wear armor stronger than iron!"), true);

                        // Find an empty slot or the original slot to put the armor back in
                        boolean itemReturned = returnArmorToOriginalSlot(player, armorPiece);

                        if (!itemReturned) {
                            // If no original slot was found, just insert into inventory
                            player.getInventory().insertStack(armorPiece);
                        }

                        // Remove the armor from the equipped slot
                        player.equipStack(slot, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    // Check if the armor is stronger than iron (diamond, netherite, etc.)
    private static boolean isStrongerThanIron(ArmorItem armorItem) {
        return armorItem.getMaterial() == ArmorMaterials.DIAMOND || armorItem.getMaterial() == ArmorMaterials.NETHERITE;
    }

    // Attempt to return the armor to its original inventory slot
    private static boolean returnArmorToOriginalSlot(PlayerEntity player, ItemStack armorPiece) {
        // Iterate through player's inventory to find where the armor piece was before
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack itemStack = player.getInventory().getStack(i);

            // If we find an empty slot, return the armor there
            if (itemStack.isEmpty()) {
                player.getInventory().setStack(i, armorPiece);
                return true;
            }
        }
        return false;
    }
}