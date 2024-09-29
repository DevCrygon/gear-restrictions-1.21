package net.crygon.gearrestrictions;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

public class ArmourEquipHandler {
    public static void register() {
        // Register an event callback to check when a player equips armor
        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!world.isClient) {
                checkArmor(player);
            }
            return new TypedActionResult<>(ActionResult.PASS, player.getStackInHand(hand));
        });
    }

    private static void checkArmor(PlayerEntity player) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack armorPiece = player.getEquippedStack(slot);
            if (armorPiece.getItem() instanceof ArmorItem armorItem) {

                // Check if armor material is stronger than iron
                if (armorItem.getMaterial() == ArmorMaterials.DIAMOND ||
                        armorItem.getMaterial() == ArmorMaterials.NETHERITE) {

                    // Send message to the player and remove the armor
                    player.sendMessage(Text.literal("You cannot wear armor stronger than iron!"), true);
                    player.getInventory().insertStack(armorPiece);  // Return the armor to the inventory
                    player.equipStack(slot, ItemStack.EMPTY);       // Remove the armor
                }
            }
        }
    }
}