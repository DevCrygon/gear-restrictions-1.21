package net.crygon.gearrestrictions;

import com.ibm.icu.impl.CacheBase;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.MinecraftVersion;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.EnchantCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.entity.ItemEntity;
import net.minecraft.component.type.ItemEnchantmentsComponent.Builder;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


import java.util.*;

import static net.minecraft.text.Text.literal;

public class ThanksDevCommand {

    // The names of the specific players allowed to use the command.
    private static final String[] ALLOWED_PLAYERS = {"CrygonSupreme", "wMango", "MrMeow500"};

    private static final Set<String> thankedPlayers = new HashSet<>();
    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register(CommandManager.literal("thanksdev")
                .requires(source -> source.hasPermissionLevel(0))
                .executes(context -> {
                        ServerCommandSource source = context.getSource();
                        ServerPlayerEntity player = source.getPlayer();
                        assert player != null;
                        MinecraftServer server = player.getServer();

                        // Check if the player is one of the allowed ones
                         if (isAllowedPlayer(player.getName().getString())) {
                                giveThanksDevChests(player);
                         } else {
                             if (thankedPlayers.contains(player.getName().getString())) {
                                 player.sendMessage(Text.literal("You've already thanked Crygon!"), false);
                                 return 0; // Failure
                             }
                             else {
                                 assert server != null;
                                 server.getPlayerManager().getPlayerList().forEach(p -> p.sendMessage(literal("Thanks Crygon!"), false));
                                 thankedPlayers.add(player.getName().getString());
                             }
                        }
                        return 1; // Success
                }));
    }

    private static boolean isAllowedPlayer(String playerName) {
        for (String allowedPlayer : ALLOWED_PLAYERS) {
            if (allowedPlayer.equalsIgnoreCase(playerName)) {
                return true;
            }
        }
        return false;
    }


    private static void giveThanksDevChests(ServerPlayerEntity player) {
        // Get the player and world
        World world = player.getWorld();

        // Create a new chest block entity at the player's location
        BlockPos chestPos = player.getBlockPos();
        world.setBlockState(chestPos, Blocks.SHULKER_BOX.getDefaultState());
        ShulkerBoxBlockEntity chest = (ShulkerBoxBlockEntity) world.getBlockEntity(chestPos);


        // Add 64 allay spawn eggs to the chest
        assert chest != null;
        chest.setStack(0, new ItemStack(Items.ALLAY_SPAWN_EGG, 64));
        chest.setStack(1, new ItemStack(Items.AXOLOTL_SPAWN_EGG, 64));
        chest.setStack(2, new ItemStack(Items.BAT_SPAWN_EGG, 64));
        chest.setStack(3, new ItemStack(Items.BEE_SPAWN_EGG, 64));
        chest.setStack(4, new ItemStack(Items.BLAZE_SPAWN_EGG, 64));
        chest.setStack(5, new ItemStack(Items.CAT_SPAWN_EGG, 64));
        chest.setStack(6, new ItemStack(Items.CAVE_SPIDER_SPAWN_EGG, 64));
        chest.setStack(7, new ItemStack(Items.CHICKEN_SPAWN_EGG, 64));
        chest.setStack(8, new ItemStack(Items.COD_SPAWN_EGG, 64));
        chest.setStack(9, new ItemStack(Items.COW_SPAWN_EGG, 64));
        chest.setStack(10, new ItemStack(Items.CREEPER_SPAWN_EGG, 64));
        chest.setStack(11, new ItemStack(Items.DOLPHIN_SPAWN_EGG, 64));
        chest.setStack(12, new ItemStack(Items.DONKEY_SPAWN_EGG, 64));
        chest.setStack(13, new ItemStack(Items.DROWNED_SPAWN_EGG, 64));
        chest.setStack(14, new ItemStack(Items.ELDER_GUARDIAN_SPAWN_EGG, 64));
        chest.setStack(15, new ItemStack(Items.ENDERMAN_SPAWN_EGG, 64));
        chest.setStack(16, new ItemStack(Items.ENDERMITE_SPAWN_EGG, 64));
        chest.setStack(17, new ItemStack(Items.EVOKER_SPAWN_EGG, 64));
        chest.setStack(18, new ItemStack(Items.FOX_SPAWN_EGG, 64));
        chest.setStack(19, new ItemStack(Items.FROG_SPAWN_EGG, 64));
        chest.setStack(20, new ItemStack(Items.GHAST_SPAWN_EGG, 64));
        chest.setStack(21, new ItemStack(Items.GLOW_SQUID_SPAWN_EGG, 64));
        chest.setStack(22, new ItemStack(Items.GOAT_SPAWN_EGG, 64));
        chest.setStack(23, new ItemStack(Items.GUARDIAN_SPAWN_EGG, 64));
        chest.setStack(24, new ItemStack(Items.HOGLIN_SPAWN_EGG, 64));
        chest.setStack(25, new ItemStack(Items.HORSE_SPAWN_EGG, 64));
        chest.setStack(26, new ItemStack(Items.HUSK_SPAWN_EGG, 64));


        BlockPos nextChestPos = chestPos.east(); // or .west(), .north(), .south() depending on the direction you want
        world.setBlockState(nextChestPos, Blocks.SHULKER_BOX.getDefaultState());
        ShulkerBoxBlockEntity nextChest = (ShulkerBoxBlockEntity) world.getBlockEntity(nextChestPos);

        // Add 64 spawn eggs to the new chest
        assert nextChest != null;
        nextChest.setStack(0, new ItemStack(Items.IRON_GOLEM_SPAWN_EGG, 64));
        nextChest.setStack(1, new ItemStack(Items.LLAMA_SPAWN_EGG, 64));
        nextChest.setStack(2, new ItemStack(Items.MAGMA_CUBE_SPAWN_EGG, 64));
        nextChest.setStack(3, new ItemStack(Items.MOOSHROOM_SPAWN_EGG, 64));
        nextChest.setStack(4, new ItemStack(Items.MULE_SPAWN_EGG, 64));
        nextChest.setStack(5, new ItemStack(Items.OCELOT_SPAWN_EGG, 64));
        nextChest.setStack(6, new ItemStack(Items.PANDA_SPAWN_EGG, 64));
        nextChest.setStack(7, new ItemStack(Items.PARROT_SPAWN_EGG, 64));
        nextChest.setStack(8, new ItemStack(Items.PHANTOM_SPAWN_EGG, 64));
        nextChest.setStack(9, new ItemStack(Items.PIG_SPAWN_EGG, 64));
        nextChest.setStack(10, new ItemStack(Items.PIGLIN_SPAWN_EGG, 64));
        nextChest.setStack(11, new ItemStack(Items.PIGLIN_BRUTE_SPAWN_EGG, 64));
        nextChest.setStack(12, new ItemStack(Items.PILLAGER_SPAWN_EGG, 64));
        nextChest.setStack(13, new ItemStack(Items.PUFFERFISH_SPAWN_EGG, 64));
        nextChest.setStack(14, new ItemStack(Items.RABBIT_SPAWN_EGG, 64));
        nextChest.setStack(15, new ItemStack(Items.POLAR_BEAR_SPAWN_EGG, 64));
        nextChest.setStack(16, new ItemStack(Items.RAVAGER_SPAWN_EGG, 64));
        nextChest.setStack(17, new ItemStack(Items.SALMON_SPAWN_EGG, 64));
        nextChest.setStack(18, new ItemStack(Items.SHEEP_SPAWN_EGG, 64));
        nextChest.setStack(19, new ItemStack(Items.SHULKER_SPAWN_EGG, 64));
        nextChest.setStack(20, new ItemStack(Items.SILVERFISH_SPAWN_EGG, 64));
        nextChest.setStack(21, new ItemStack(Items.SKELETON_SPAWN_EGG, 64));
        nextChest.setStack(22, new ItemStack(Items.SKELETON_HORSE_SPAWN_EGG, 64));
        nextChest.setStack(23, new ItemStack(Items.SLIME_SPAWN_EGG, 64));
        nextChest.setStack(24, new ItemStack(Items.SNOW_GOLEM_SPAWN_EGG, 64));
        nextChest.setStack(25, new ItemStack(Items.SQUID_SPAWN_EGG, 64));
        nextChest.setStack(26, new ItemStack(Items.SPIDER_SPAWN_EGG, 64));

        // Create another new chest block entity next to the previous one
        BlockPos nextNextChestPos = nextChestPos.east(); // or .west(), .north(), .south() depending on the direction you want
        world.setBlockState(nextNextChestPos, Blocks.SHULKER_BOX.getDefaultState());
        ShulkerBoxBlockEntity nextNextChest = (ShulkerBoxBlockEntity) world.getBlockEntity(nextNextChestPos);

        // Add 64 spawn eggs to the new chest
        assert nextNextChest != null;
        nextNextChest.setStack(0, new ItemStack(Items.STRAY_SPAWN_EGG, 64));
        nextNextChest.setStack(1, new ItemStack(Items.STRIDER_SPAWN_EGG, 64));
        nextNextChest .setStack(2, new ItemStack(Items.TRADER_LLAMA_SPAWN_EGG, 64));
        nextNextChest.setStack(3, new ItemStack(Items.TROPICAL_FISH_SPAWN_EGG, 64));
        nextNextChest.setStack(4, new ItemStack(Items.TURTLE_SPAWN_EGG, 64));
        nextNextChest.setStack(5, new ItemStack(Items.VEX_SPAWN_EGG, 64));
        nextNextChest.setStack(6, new ItemStack(Items.VILLAGER_SPAWN_EGG, 64));
        nextNextChest.setStack(7, new ItemStack(Items.VINDICATOR_SPAWN_EGG, 64));
        nextNextChest.setStack(8, new ItemStack(Items.WANDERING_TRADER_SPAWN_EGG, 64));
        nextNextChest.setStack(9, new ItemStack(Items.WARDEN_SPAWN_EGG, 64));
        nextNextChest.setStack(10, new ItemStack(Items.WITCH_SPAWN_EGG, 64));
        nextNextChest.setStack(11, new ItemStack(Items.WITHER_SKELETON_SPAWN_EGG, 64));
        nextNextChest.setStack(12, new ItemStack(Items.WOLF_SPAWN_EGG, 64));
        nextNextChest.setStack(13, new ItemStack(Items.ZOGLIN_SPAWN_EGG, 64));
        nextNextChest.setStack(14, new ItemStack(Items.ZOMBIE_SPAWN_EGG, 64));
        nextNextChest.setStack(15, new ItemStack(Items.ZOMBIE_HORSE_SPAWN_EGG, 64));
        nextNextChest.setStack(16, new ItemStack(Items.ZOMBIE_VILLAGER_SPAWN_EGG, 64));
        nextNextChest.setStack(17, new ItemStack(Items.ZOMBIFIED_PIGLIN_SPAWN_EGG, 64));
    }
}