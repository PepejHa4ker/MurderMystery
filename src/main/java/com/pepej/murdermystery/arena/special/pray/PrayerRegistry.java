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

package com.pepej.murdermystery.arena.special.pray;

import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.api.StatsStorage;
import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.arena.ArenaRegistry;
import com.pepej.murdermystery.arena.ArenaState;
import com.pepej.murdermystery.handlers.ChatManager;
import com.pepej.murdermystery.handlers.language.LanguageManager;
import com.pepej.murdermystery.user.User;
import com.pepej.murdermystery.utils.effects.PotionEffectBuilder;
import com.pepej.murdermystery.utils.items.ItemPosition;
import com.pepej.murdermystery.utils.misc.MiscUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author Plajer
 * <p>
 * Created at 16.10.2018
 */
@FieldDefaults(level= AccessLevel.PRIVATE)
public class PrayerRegistry {

  private static MurderMystery plugin;
  private static final List<Prayer> prayers = new ArrayList<>();
  private static Random rand;

  private PrayerRegistry() {
  }

  public static void init(MurderMystery plugin) {
    PrayerRegistry.plugin = plugin;
    //good prayers
    prayers.add(new Prayer(Prayer.PrayerType.DETECTIVE_REVELATION, true, ChatManager.colorMessage("In-Game.Messages.Special-Blocks.Praises.Gifts.Detective-Revelation")));
    prayers.add(new Prayer(Prayer.PrayerType.SINGLE_COMPENSATION, true, ChatManager.colorMessage("In-Game.Messages.Special-Blocks.Praises.Gifts.Single-Compensation")));
    prayers.add(new Prayer(Prayer.PrayerType.BOW_TIME, true, ChatManager.colorMessage("In-Game.Messages.Special-Blocks.Praises.Gifts.Bow-Time")));

    //bad prayers
    prayers.add(new Prayer(Prayer.PrayerType.SLOWNESS_CURSE, false, ChatManager.colorMessage("In-Game.Messages.Special-Blocks.Praises.Curses.Slowness-Curse")));
    prayers.add(new Prayer(Prayer.PrayerType.BLINDNESS_CURSE, false, ChatManager.colorMessage("In-Game.Messages.Special-Blocks.Praises.Curses.Blindness-Curse")));
    prayers.add(new Prayer(Prayer.PrayerType.INCOMING_DEATH, false, ChatManager.colorMessage("In-Game.Messages.Special-Blocks.Praises.Curses.Incoming-Death")));
    rand = new Random();
  }

  public static Prayer getRandomPray() {
    return prayers.get(rand.nextInt(prayers.size()));
  }

  public static List<Prayer> getPrayers() {
    return prayers;
  }

  public static void applyRandomPrayer(User user) {
    Prayer prayer = getRandomPray();
    user.setStat(StatsStorage.StatisticType.LOCAL_CURRENT_PRAY, prayer.getPrayerType().ordinal());
    Player player = user.getPlayer();
    Arena arena = ArenaRegistry.getArena(user.getPlayer());
    List<String> prayMessage = LanguageManager.getLanguageList("In-Game.Messages.Special-Blocks.Praises.Message");
    if (prayer.isGoodPray()) {
      prayMessage = prayMessage
              .stream()
              .map(msg -> msg.replace("%feeling%", ChatManager.colorMessage("In-Game.Messages.Special-Blocks.Praises.Feelings.Blessed", player)))
              .collect(Collectors.toList());
    } else {
      prayMessage = prayMessage
              .stream()
              .map(msg -> msg.replace("%feeling%", ChatManager.colorMessage("In-Game.Messages.Special-Blocks.Praises.Feelings.Cursed", player)))
              .collect(Collectors.toList());
    }
    prayMessage = prayMessage
            .stream()
            .map(msg -> msg
                    .replace(
                            "%praise%",
                            prayer.getPrayerDescription()
                    )
            )
            .collect(Collectors.toList());
    switch (prayer.getPrayerType()) {
      case BLINDNESS_CURSE:
        player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.BLINDNESS)
                .setDuration(30)
                .setAmplifier(0)
                .setAmbient(false)
                .setVisible(false)
                .build());
        break;
      case BOW_TIME:
        ItemPosition.setItem(player, ItemPosition.BOW, new ItemStack(Material.BOW, 1));
        ItemPosition.addItem(player, ItemPosition.ARROWS, new ItemStack(Material.ARROW, plugin.getConfig().getInt("Detective-Prayer-Arrows", 2)));
        break;
      case DETECTIVE_REVELATION:
        String murderer;
        murderer = arena.getCharacter(Arena.CharacterType.MURDERER) != null ? arena.getCharacter(Arena.CharacterType.MURDERER).getName() : "";
        prayMessage = prayMessage.stream().map(msg -> msg.replace("%murderer%", murderer)).collect(Collectors.toList());
        break;
      case INCOMING_DEATH:
        new BukkitRunnable() {
          int time = 30;

          @Override
          public void run() {
            if (arena == null || arena.getArenaState() != ArenaState.IN_GAME || !arena.getPlayersLeft().contains(player)) {
              this.cancel();
              return;
            }
            time--;
            if (time == 0) {
              player.damage(1000);
              this.cancel();
            }
          }
        }.runTaskTimer(plugin, 20, 20);
        break;
      case SINGLE_COMPENSATION:
        ItemPosition.addItem(player, ItemPosition.GOLD_INGOTS, new ItemStack(Material.GOLD_INGOT, 5));
        user.addStat(StatsStorage.StatisticType.LOCAL_GOLD, 5);
        break;
      case SLOWNESS_CURSE:
        player.addPotionEffect(new PotionEffectBuilder(PotionEffectType.SLOW)
                .setVisible(false)
                .setAmplifier(0)
                .setDuration(30)
                .setAmbient(false)
                .build());

        break;
      default:
        break;
    }
    for (String msg : prayMessage) {
      MiscUtils.sendCenteredMessage(player, msg);
    }
  }
}
