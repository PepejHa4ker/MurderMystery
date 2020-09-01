

package com.pepej.murdermystery.arena;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.ItemLine;
import com.pepej.murdermystery.ConfigPreferences;
import com.pepej.murdermystery.MurderMystery;
import com.pepej.murdermystery.api.StatsStorage;
import com.pepej.murdermystery.arena.role.Role;
import com.pepej.murdermystery.handlers.ChatManager;
import com.pepej.murdermystery.user.User;
import com.pepej.murdermystery.utils.items.ItemPosition;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ArenaUtils {

  public static void applyBow(User user) {
    if (!Role.isRole(Role.INNOCENT, user.getPlayer())) return;
    if (user.getStat(StatsStorage.StatisticType.LOCAL_GOLD) >= 10) {
      user.setStat(StatsStorage.StatisticType.LOCAL_GOLD, user.getStat(StatsStorage.StatisticType.LOCAL_GOLD) - 10);
      user.getPlayer().sendTitle(ChatManager.colorMessage("In-Game.Messages.Bow-Messages.Bow-Shot-For-Gold", user.getPlayer()), ChatManager.colorMessage("In-Game.Messages.Bow-Messages.Bow-Shot-Subtitle", user.getPlayer()), 5, 40, 5);
      ItemPosition.setItem(user.getPlayer(), ItemPosition.BOW, new ItemStack(Material.BOW, 1));
      ItemPosition.addItem(user.getPlayer(), ItemPosition.ARROWS, new ItemStack(Material.ARROW, MurderMystery.getInstance().getConfig().getInt("Detective-Default-Arrows", 3)));
      user.getPlayer().getInventory().setItem(ItemPosition.GOLD_INGOTS.getOtherRolesItemPosition(), new ItemStack(Material.GOLD_INGOT, -10));
    }
  }


  public static void onMurdererDeath(Arena arena) {
    for (Player player : arena.getPlayers()) {
      player.sendTitle(ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Titles.Win", player),
              ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Subtitles.Murderer-Stopped", player), 5, 40, 5);
      if (Role.isRole(Role.MURDERER, player)) {
        player.sendTitle(ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Titles.Lose", player), null, 5, 40, 5);
      }
      User loopUser = MurderMystery.getInstance().getUserManager().getUser(player);
      if (Role.isRole(Role.INNOCENT, player)) {
        ArenaUtils.addScore(loopUser, ArenaUtils.ScoreAction.SURVIVE_GAME, 0);
      } else if (Role.isRole(Role.ANY_DETECTIVE, player)) {
        ArenaUtils.addScore(loopUser, ArenaUtils.ScoreAction.WIN_GAME, 0);
        ArenaUtils.addScore(loopUser, ArenaUtils.ScoreAction.DETECTIVE_WIN_GAME, 0);
      }
    }
    for (Player murderer : arena.getMurdererList()) {
      murderer.sendTitle(ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Titles.Lose", murderer),
              ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Subtitles.Murderer-Stopped", murderer), 5, 40, 5);
    }
    Bukkit.getScheduler().runTaskLater(MurderMystery.getInstance(), () -> ArenaManager.stopGame(false, arena), 5);
  }

  public static void addScore(User user, ScoreAction action, int amount) {
    String msg = ChatManager.colorMessage("In-Game.Messages.Bonus-Score");
    msg = StringUtils.replace(msg, "%score%", String.valueOf(action.getPoints()));
    if (action == ScoreAction.GOLD_PICKUP && amount > 1) {
      int score = action.getPoints() * amount;
      if (user.getPlayer().hasPermission("murder.grand")) {
        float scoreGrand = score + (score * 0.75f);
        msg = StringUtils.replace(msg, "%score%", String.valueOf(scoreGrand));
        msg = StringUtils.replace(msg, "%action%", action.getAction());
        user.setStat(StatsStorage.StatisticType.LOCAL_SCORE, (int) (user.getStat(StatsStorage.StatisticType.LOCAL_SCORE) + scoreGrand));
      } else {
        msg = StringUtils.replace(msg, "%score%", String.valueOf(score));
        msg = StringUtils.replace(msg, "%action%", action.getAction());
        user.setStat(StatsStorage.StatisticType.LOCAL_SCORE, user.getStat(StatsStorage.StatisticType.LOCAL_SCORE) + score);
      }
      user.getPlayer().sendMessage(msg);
      return;
    }
    if (action == ScoreAction.DETECTIVE_WIN_GAME) {
      int innocents = 0;
      for (Player p : user.getArena().getPlayersLeft()) {
        if (Role.isRole(Role.INNOCENT, p)) {
          innocents++;
        }
      }
      user.setStat(StatsStorage.StatisticType.LOCAL_SCORE, user.getStat(StatsStorage.StatisticType.LOCAL_SCORE) + (100 * innocents));
      msg = StringUtils.replace(msg, "%score%", String.valueOf(100 * innocents));
      msg = StringUtils.replace(msg, "%action%", action.getAction().replace("%amount%", String.valueOf(innocents)));
      user.getPlayer().sendMessage(msg);
      return;
    }
    msg = StringUtils.replace(msg, "%score%", String.valueOf(action.getPoints()));
    if (action.getPoints() < 0) {
      msg = StringUtils.replace(msg, "+", "");
    }
    msg = StringUtils.replace(msg, "%action%", action.getAction());
    user.setStat(StatsStorage.StatisticType.LOCAL_SCORE, user.getStat(StatsStorage.StatisticType.LOCAL_SCORE) + action.getPoints());
    user.getPlayer().sendMessage(msg);
  }

  public static void updateInnocentLocator(Arena arena) {
    if (!arena.isMurdererLocatorReceived()) {
      ItemStack innocentLocator = new ItemStack(Material.COMPASS, 1);
      ItemMeta innocentMeta = innocentLocator.getItemMeta();
      innocentMeta.setDisplayName(ChatManager.colorMessage("In-Game.Innocent-Locator-Item-Name"));
      innocentLocator.setItemMeta(innocentMeta);
      for (Player p : arena.getPlayersLeft()) {
        if (arena.isMurderAlive(p)) {
          ItemPosition.setItem(p, ItemPosition.INNOCENTS_LOCATOR, innocentLocator);
        }
      }
      arena.setMurdererLocatorReceived(true);

      for (Player p : arena.getPlayersLeft()) {
        if (Role.isRole(Role.MURDERER, p)) {
          continue;
        }
        p.sendTitle(ChatManager.colorMessage("In-Game.Watch-Out-Title", p), ChatManager.colorMessage("In-Game.Watch-Out-Subtitle", p), 5, 40, 5);
      }
    }
    for (Player p : arena.getPlayersLeft()) {
      if (Role.isRole(Role.MURDERER, p)) {
        continue;
      }
      for (Player murder : arena.getMurdererList()) {
        if (arena.isMurderAlive(murder)) {
          murder.setCompassTarget(p.getLocation());
        }
      }
      break;
    }
  }

  private static void addBowLocator(Arena arena, Location loc) {
    ItemStack bowLocator = new ItemStack(Material.COMPASS, 1);
    ItemMeta bowMeta = bowLocator.getItemMeta();
    bowMeta.setDisplayName(ChatManager.colorMessage("In-Game.Bow-Locator-Item-Name"));
    bowLocator.setItemMeta(bowMeta);
    for (Player p : arena.getPlayersLeft()) {
      if (Role.isRole(Role.INNOCENT, p)) {
        ItemPosition.setItem(p, ItemPosition.BOW_LOCATOR, bowLocator);
        p.setCompassTarget(loc);
      }
    }
  }

  public static void dropBowAndAnnounce(Arena arena, Player victim) {
    if (arena.getPlayersLeft().size() > 1) {
      for (Player p : arena.getPlayers()) {
        p.sendTitle(ChatManager.colorMessage("In-Game.Messages.Bow-Messages.Bow-Dropped-Title", p), null, 5, 40, 5);
      }
      for (Player p : arena.getPlayersLeft()) {
        p.sendTitle(null, ChatManager.colorMessage("In-Game.Messages.Bow-Messages.Bow-Dropped-Subtitle", p), 5, 40, 5);
      }
    }

    Hologram hologram = HologramsAPI.createHologram(MurderMystery.getInstance(), victim.getLocation().clone().add(0, 0.6, 0));
    ItemLine itemLine = hologram.appendItemLine(new ItemStack(Material.BOW, 1));

    itemLine.setPickupHandler(player -> {
      if (MurderMystery.getInstance().getUserManager().getUser(player).isSpectator()) {
        return;
      }
      if (Role.isRole(Role.INNOCENT, player)) {
        player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 1F, 2F);
        hologram.delete();

        for (Player loopPlayer : arena.getPlayersLeft()) {
          if (Role.isRole(Role.INNOCENT, loopPlayer)) {
            ItemPosition.setItem(loopPlayer, ItemPosition.BOW_LOCATOR, new ItemStack(Material.AIR, 1));
          }
        }

        arena.setCharacter(Arena.CharacterType.FAKE_DETECTIVE, player);
        ItemPosition.setItem(player, ItemPosition.BOW, new ItemStack(Material.BOW, 1));
        ItemPosition.setItem(player, ItemPosition.INFINITE_ARROWS, new ItemStack(Material.ARROW, MurderMystery.getInstance().getConfig().getInt("Detective-Default-Arrows", 3)));
        ChatManager.broadcast(arena, ChatManager.colorMessage("In-Game.Messages.Bow-Messages.Pickup-Bow-Message", player));
      }
    });
    arena.setBowHologram(hologram);
    addBowLocator(arena, hologram.getLocation());
  }

  public static boolean areInSameArena(Player one, Player two) {
    if (ArenaRegistry.getArena(one) == null || ArenaRegistry.getArena(two) == null) {
      return false;
    }
    return ArenaRegistry.getArena(one).equals(ArenaRegistry.getArena(two));
  }

  public static void hidePlayer(Player p, Arena arena) {
    for (Player player : arena.getPlayers()) {
      player.hidePlayer(p);
    }
  }

  public static void showPlayer(Player p, Arena arena) {
    for (Player player : arena.getPlayers()) {
      player.showPlayer(p);
    }
  }


  public static void hidePlayersOutsideTheGame(Player player, Arena arena) {
    for (Player players : MurderMystery.getInstance().getServer().getOnlinePlayers()) {
      if (arena.getPlayers().contains(players)) {
        continue;
      }
      player.hidePlayer(players);
      players.hidePlayer(player);
    }
  }

  public static void updateNameTagsVisibility(final Player p) {
    if (!MurderMystery.getInstance().getConfigPreferences().getOption(ConfigPreferences.Option.NAMETAGS_HIDDEN)) {
      return;
    }
    for (Player players : MurderMystery.getInstance().getServer().getOnlinePlayers()) {
      Arena arena = ArenaRegistry.getArena(players);
      if (arena == null) {
        continue;
      }
      Scoreboard scoreboard = players.getScoreboard();
      if (scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
      }
      Team team = scoreboard.getTeam("MMHide");
      if (team == null) {
        team = scoreboard.registerNewTeam("MMHide");
      }
      team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
      if (arena.getArenaState() == ArenaState.IN_GAME) {
        team.addEntry(p.getName());
      } else if (arena.getArenaState() == ArenaState.STARTING || arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS) {
        team.removeEntry(p.getName());
      } else if (arena.getArenaState() == ArenaState.ENDING || arena.getArenaState() == ArenaState.RESTARTING) {
        team.removeEntry(p.getName());
      }
      players.setScoreboard(scoreboard);
    }
  }

  public enum ScoreAction {
    KILL_PLAYER(100, ChatManager.colorMessage("In-Game.Messages.Score-Actions.Kill-Player")), KILL_MURDERER(200, ChatManager.colorMessage("In-Game.Messages.Score-Actions.Kill-Murderer")),
    GOLD_PICKUP(15, ChatManager.colorMessage("In-Game.Messages.Score-Actions.Gold-Pickup")), SURVIVE_TIME(150, ChatManager.colorMessage("In-Game.Messages.Score-Actions.Survive")),
    SURVIVE_GAME(200, ChatManager.colorMessage("In-Game.Messages.Score-Actions.Survive-Till-End")), WIN_GAME(100, ChatManager.colorMessage("In-Game.Messages.Score-Actions.Win-Game")),
    DETECTIVE_WIN_GAME(0, ChatManager.colorMessage("In-Game.Messages.Score-Actions.Detective-Reward")), INNOCENT_KILL(-100, ChatManager.colorMessage("In-Game.Messages.Score-Actions.Innocent-Kill"));

    int points;
    String action;

    ScoreAction(int points, String action) {
      this.points = points;
      this.action = action;
    }

    public int getPoints() {
      return points;
    }

    public String getAction() {
      return action;
    }
  }

}
