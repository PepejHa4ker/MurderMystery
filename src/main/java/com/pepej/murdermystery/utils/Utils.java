/*
 * MurderMystery - Find the murderer, kill him and survive!
 * Copyright (C) 2019  Plajer's Lair - maintained by Tigerpanzer_02, Plajer and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pepej.murdermystery.utils;

import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.arena.ArenaRegistry;
import com.pepej.murdermystery.arena.ArenaState;
import com.pepej.murdermystery.economy.PriceType;
import com.pepej.murdermystery.handlers.ChatManager;
import com.pepej.murdermystery.perk.PerkRegister;
import com.pepej.murdermystery.utils.strings.StringFormatUtils;
import lombok.experimental.UtilityClass;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Plajer
 * <p>
 * Created at 06.10.2018
 */
@UtilityClass
public class Utils {

    /**
     * Serialize int to use it in Inventories size
     * ex. you have 38 kits and it will serialize it to 45 (9*5)
     * because it is valid inventory size
     * next ex. you have 55 items and it will serialize it to 63 (9*7) not 54 because it's too less
     *
     * @param i integer to serialize
     * @return serialized number
     */
    public int serializeInt(Integer i) {
        if ((i % 9) == 0) {
            return i;
        } else {
            return (int) ((Math.ceil(i / 9) * 9) + 9);
        }
    }

    public void perkMatrixRegister(StaticPane pane, int radius, List<ItemStack> items) {
        for (int y = radius; y < pane.getHeight() - radius; y++) {
            for (int x = radius; x < 9 - radius; x++) {
                int id = (y - radius) * (9 - 2 * radius) + (x - radius);
                if (id < items.size()) {
                    pane.addItem(new GuiItem(items.get(id), e -> {
                        PriceType type;
                        switch (e.getClick()) {
                            case LEFT:
                                type = PriceType.COINS;
                                break;
                            case RIGHT:
                                type = PriceType.KARMA;
                                break;
                            default:
                                return;
                        }
                        PerkRegister.getCachedPerks()
                                    .stream()
                                    .filter(perk -> perk.getDisplayItem().equals(e.getCurrentItem().getType()))
                                    .findFirst()
                                    .ifPresent(perk -> {
                                        perk.tryBuy((Player) e.getWhoClicked(), type);
                                        e.getWhoClicked().closeInventory();
                                    });

                    }), x, y);
                } else {
                    return;
                }
            }
        }

    }

    /**
     * Checks whether itemstack is named (not null, has meta and display name)
     *
     * @param stack item stack to check
     * @return true if named, false otherwise
     */
    public boolean isNamed(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        return stack.hasItemMeta() && stack.getItemMeta().hasDisplayName();
    }

    public void applyActionBarCooldown(Player p, int seconds) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!ArenaRegistry.isInArena(p) || ArenaRegistry.getArena(p).getArenaState() != ArenaState.IN_GAME) {
                    this.cancel();
                }
                if (ticks >= seconds * 20) {
                    this.cancel();
                }
                String progress = StringFormatUtils.getProgressBar(ticks, 5 * 20, 10, "■", ChatColor.COLOR_CHAR + "a", ChatColor.COLOR_CHAR + "c");
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatManager.colorMessage("In-Game.Cooldown-Format", p)
                                                                                                           .replace("%progress%", progress).replace("%time%", String.valueOf((double) (100 - ticks) / 20))));
                ticks += 10;
            }
        }.runTaskTimer(MurderMystery.getInstance(), 0, 10);
    }

    public List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<>();
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    public Location getBlockCenter(Location location) {
        return location.add(0.5, 0, 0.5);
    }

    public boolean checkIsInGameInstance(Player player) {
        if (ArenaRegistry.getArena(player) == null) {
            player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Not-Playing", player));
            return false;
        }
        return true;
    }

    public boolean hasPermission(CommandSender sender, String perm) {
        if (sender.hasPermission(perm)) {
            return true;
        }
        sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.No-Permission"));
        return false;
    }

    public int getRandomNumber(int lowerBound, int upperBound) {
        return new Random().nextInt(upperBound - lowerBound) + lowerBound;
    }


}
