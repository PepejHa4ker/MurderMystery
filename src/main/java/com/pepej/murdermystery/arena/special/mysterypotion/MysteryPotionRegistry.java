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

package com.pepej.murdermystery.arena.special.mysterypotion;

import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.handlers.ChatManager;
import com.pepej.murdermystery.utils.config.ConfigUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Plajer
 * <p>
 * Created at 15.10.2018
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MysteryPotionRegistry {

  private static final List<MysteryPotion> mysteryPotions = new ArrayList<>();
  private static Random rand;

  public static void init(MurderMystery plugin) {
    rand = new Random();
    FileConfiguration config = ConfigUtils.getConfig(plugin, "specialblocks");
    String path = "Special-Blocks.Cauldron-Potions";
    for (String key : config.getConfigurationSection(path).getKeys(false)) {
      //amplifiers are counted from 0 so -1
      PotionEffect effect = new PotionEffect(PotionEffectType.getByName(config.getString(path + "." + key + ".Type").toUpperCase()),
        config.getInt(path + "." + key + ".Duration") * 20, config.getInt(path + "." + key + ".Amplifier") - 1, false, false);
      mysteryPotions.add(new MysteryPotion(ChatManager.colorRawMessage(config.getString(path + "." + key + ".Name")),
        ChatManager.colorRawMessage(config.getString(path + "." + key + ".Subtitle")), effect));
    }
  }

  public static MysteryPotion getRandomPotion() {
    return mysteryPotions.get(rand.nextInt(mysteryPotions.size()));
  }

  public static List<MysteryPotion> getMysteryPotions() {
    return mysteryPotions;
  }
}
