package com.pepej.murdermystery.handlers;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.pepej.murdermystery.HookManager;
import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.arena.ArenaRegistry;
import com.pepej.murdermystery.arena.corpse.Corpse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.golde.bukkit.corpsereborn.CorpseAPI.CorpseAPI;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseClickEvent;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseSpawnEvent;
import org.golde.bukkit.corpsereborn.nms.Corpses;

import java.util.HashMap;
import java.util.Map;


public class CorpseHandler implements Listener {

  private final MurderMystery plugin;
  private Corpses.CorpseData lastSpawnedCorpse;
  private final Map<String, String> registeredLastWords = new HashMap<>();

  public CorpseHandler(MurderMystery plugin) {
    this.plugin = plugin;
    registerLastWord("murdermystery.lastwords.meme", ChatManager.colorMessage("In-Game.Messages.Last-Words.Meme"));
    registerLastWord("murdermystery.lastwords.rage", ChatManager.colorMessage("In-Game.Messages.Last-Words.Rage"));
    registerLastWord("murdermystery.lastwords.pro", ChatManager.colorMessage("In-Game.Messages.Last-Words.Pro"));
    registerLastWord("default", ChatManager.colorMessage("In-Game.Messages.Last-Words.Default"));
    //run bit later than hook manager to ensure it's not null
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      if (plugin.getHookManager() != null && plugin.getHookManager().isFeatureEnabled(HookManager.HookFeature.CORPSES)) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
      }
    }, 25L * 5);
  }

  public void registerLastWord(String permission, String lastWord) {
    registeredLastWords.put(permission, lastWord);
  }

  public void spawnCorpse(Player p, Arena arena) {
    if (plugin.getHookManager() != null && !plugin.getHookManager().isFeatureEnabled(HookManager.HookFeature.CORPSES)) {
      return;
    }
    Hologram hologram = HologramsAPI.createHologram(plugin, p.getLocation().clone().add(0, 1.7, 0));
    hologram.appendTextLine(ChatManager.colorMessage("In-Game.Messages.Corpse-Last-Words", p).replace("%player%", p.getName()));
    boolean found = false;
    for (String perm : registeredLastWords.keySet()) {
      if (p.hasPermission(perm)) {
        hologram.appendTextLine(registeredLastWords.get(perm));
        found = true;
        break;
      }
    }
    if (!found) {
      hologram.appendTextLine(registeredLastWords.get("default"));
    }
    Corpses.CorpseData corpse = CorpseAPI.spawnCorpse(p, p.getLocation());
    lastSpawnedCorpse = corpse;
    arena.addCorpse(new Corpse(hologram, corpse));
    Bukkit.getScheduler().runTaskLater(plugin, () -> {
      hologram.delete();
      Bukkit.getScheduler().runTaskLater(plugin, corpse::destroyCorpseFromEveryone, 20 * 20);
    }, 15 * 20);
  }

  @EventHandler
  public void onCorpseSpawn(CorpseSpawnEvent e) {
    if (!plugin.getConfig().getBoolean("Override-Corpses-Spawn", true)) {
      return;
    }
    if (lastSpawnedCorpse == null) {
      return;
    }
    if (!e.getCorpse().equals(lastSpawnedCorpse)) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onCorpseClick(CorpseClickEvent e) {
    if (ArenaRegistry.isInArena(e.getClicker())) {
      e.setCancelled(true);
      e.getClicker().closeInventory();
    }
  }

}
