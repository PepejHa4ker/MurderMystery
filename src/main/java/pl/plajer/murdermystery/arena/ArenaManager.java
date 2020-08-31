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
   *   * GNU General Public License for more details.
   *
   * You should have received a copy of the GNU General Public License
   * along with this program.  If not, see <http://www.gnu.org/licenses/>.
   */

  package pl.plajer.murdermystery.arena;

  import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pl.plajer.murdermystery.ConfigPreferences;
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.api.StatsStorage;
import pl.plajer.murdermystery.api.events.game.MMGameJoinAttemptEvent;
import pl.plajer.murdermystery.api.events.game.MMGameLeaveAttemptEvent;
import pl.plajer.murdermystery.api.events.game.MMGameStopEvent;
import pl.plajer.murdermystery.arena.role.Role;
import pl.plajer.murdermystery.events.ChatEvents;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.handlers.PermissionsManager;
import pl.plajer.murdermystery.handlers.items.SpecialItemManager;
import pl.plajer.murdermystery.handlers.language.LanguageManager;
import pl.plajer.murdermystery.handlers.rewards.Reward;
import pl.plajer.murdermystery.user.User;
import pl.plajer.murdermystery.utils.compat.XMaterial;
import pl.plajer.murdermystery.utils.config.ConfigUtils;
import pl.plajer.murdermystery.utils.donate.DonatType;
import pl.plajer.murdermystery.utils.donate.DonaterUtils;
import pl.plajer.murdermystery.utils.items.ItemBuilder;
import pl.plajer.murdermystery.utils.items.ItemPosition;
import pl.plajer.murdermystery.utils.misc.MiscUtils;
import pl.plajer.murdermystery.utils.serialization.InventorySerializer;

import java.util.*;

  /**
   * @author Plajer
   * <p>
   * Created at 13.05.2018
   */

  @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
  public final class ArenaManager {

      private static final MurderMystery plugin = JavaPlugin.getPlugin(MurderMystery.class);

      private ArenaManager() {
      }


      public static void notifyPlayersNotInArena(Arena arena, String message) {
          for(final Player player : Bukkit.getOnlinePlayers()) {
              if (arena.getPlayers().contains(player)) continue;
              player.sendMessage(ChatManager.colorRawMessage(message));
          }
      }


      public static void sendArenaMessages(Arena arena, Collection<String> messages) {
          for(final Player player : arena.getPlayers()) {
              for(final String message : messages) {
                  player.sendMessage(ChatManager.colorRawMessage(message));
              }
          }
      }

      public static void sendArenaMessages(Arena arena, String... messages) {
         ArenaManager.sendArenaMessages(arena, Arrays.asList(messages));
      }

      /**
       * Attempts player to join arena.
       * Calls MMGameJoinAttemptEvent.
       * Can be cancelled only via above-mentioned event
       *
       * @param player player to join
       * @see MMGameJoinAttemptEvent
       */

      public static void joinAttempt(Player player, Arena arena) {
          MMGameJoinAttemptEvent gameJoinAttemptEvent = new MMGameJoinAttemptEvent(player, arena);
          Bukkit.getPluginManager().callEvent(gameJoinAttemptEvent);
          if (!arena.isReady()) {
              player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Arena-Not-Configured"));
              return;
          }
          if (gameJoinAttemptEvent.isCancelled()) {
              player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Join-Cancelled-Via-API"));
              return;
          }
          if (ArenaRegistry.isInArena(player)) {
              player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Already-Playing"));
              return;
          }

          if (!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
              if (!player.hasPermission(PermissionsManager.getJoinPerm().replace("<arena>", "*"))
                      || !player.hasPermission(PermissionsManager.getJoinPerm().replace("<arena>", arena.getId()))) {
                  player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Join-No-Permission").replace("%permission%",
                          PermissionsManager.getJoinPerm().replace("<arena>", arena.getId())));
                  return;
              }
          }
          if (arena.getArenaState() == ArenaState.RESTARTING) {
              return;
          }
          if (arena.getPlayers().size() >= arena.getMaximumPlayers() && arena.getArenaState() == ArenaState.STARTING) {
              if (!player.hasPermission(PermissionsManager.getJoinFullGames())) {
                  player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Full-Game-No-Permission"));
                  return;
              }
              boolean foundSlot = false;
              for (Player loopPlayer : arena.getPlayers()) {
                  if (loopPlayer.hasPermission(PermissionsManager.getJoinFullGames())) {
                      continue;
                  }
                  ArenaManager.leaveAttempt(loopPlayer, arena);
                  loopPlayer.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.You-Were-Kicked-For-Premium-Slot"));
                  ChatManager.broadcast(arena, ChatManager.formatMessage(arena, ChatManager.colorMessage("In-Game.Messages.Lobby-Messages.Kicked-For-Premium-Slot"), loopPlayer));
                  foundSlot = true;
                  break;
              }
              if (!foundSlot) {
                  player.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.No-Slots-For-Premium"));
                  return;
              }
          }
          User user = plugin.getUserManager().getUser(player);
          arena.getScoreboardManager().createScoreboard(user);
          if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
              InventorySerializer.saveInventoryToFile(plugin, player);
          }

          int murderIncrease = player.getEffectivePermissions().stream().filter(permAttach -> permAttach.getPermission().startsWith("murdermystery.role.murderer."))
                  .mapToInt(pai -> Integer.parseInt(pai.getPermission().substring(28 /* remove the permission node to obtain the number*/))).max().orElse(0);
          int detectiveIncrease = player.getEffectivePermissions().stream().filter(permAttach -> permAttach.getPermission().startsWith("murdermystery.role.detective."))
                  .mapToInt(pai -> Integer.parseInt(pai.getPermission().substring(29 /* remove the permission node to obtain the number*/))).max().orElse(0);
          user.addStat(StatsStorage.StatisticType.CONTRIBUTION_MURDERER, murderIncrease);
          user.addStat(StatsStorage.StatisticType.CONTRIBUTION_DETECTIVE, detectiveIncrease);

          if (player.hasPermission("murder.vip")) {
              user.setType(DonatType.VIP);
          } else if (player.hasPermission("murder.prem")) {
              user.setType(DonatType.PREM);
          } else if (player.hasPermission("murder.grand")) {
              user.setType(DonatType.GRAND);
          } else {
              user.setType(DonatType.DEF);
          }
          player.sendTitle("§cДобро пожаловать", "§cна §6" + arena.getFormattedArenaName(), 10, 30, 10);
          arena.addPlayer(player);
          player.setLevel(0);
          player.setExp(1);
          player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
          player.setFoodLevel(20);
          if ((arena.getArenaState() == ArenaState.IN_GAME || arena.getArenaState() == ArenaState.ENDING)) {
              arena.teleportToStartLocation(player);
              player.sendMessage(ChatManager.colorMessage("In-Game.You-Are-Spectator"));
              player.getInventory().clear();

              player.getInventory().setItem(0, new ItemBuilder(XMaterial.COMPASS.parseItem()).name(ChatManager.colorMessage("In-Game.Spectator.Spectator-Item-Name")).build());
              player.getInventory().setItem(4, new ItemBuilder(XMaterial.COMPARATOR.parseItem()).name(ChatManager.colorMessage("In-Game.Spectator.Settings-Menu.Item-Name")).build());
              player.getInventory().setItem(8, SpecialItemManager.getSpecialItem("Leave").getItemStack());

              for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                  player.removePotionEffect(potionEffect.getType());
              }

              player.setGameMode(GameMode.SURVIVAL);
              player.setAllowFlight(true);
              player.setFlying(true);
              user.setSpectator(true);
              for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
                  if (!stat.isPersistent()) {
                      user.setStat(stat, 0);
                  }
              }
              player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
              ArenaUtils.hidePlayer(player, arena);

              for (Player spectator : arena.getPlayers()) {
                  if (plugin.getUserManager().getUser(spectator).isSpectator()) {
                      player.hidePlayer(spectator);
                  } else {
                      player.showPlayer(spectator);
                  }
              }
              ArenaUtils.hidePlayersOutsideTheGame(player, arena);
              return;
          }
          arena.teleportToLobby(player);
          player.getInventory().setArmorContents(new ItemStack[]{new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR), new ItemStack(Material.AIR)});
          player.setFlying(false);
          player.setAllowFlight(false);
          player.getInventory().clear();
          arena.doBarAction(Arena.BarAction.ADD, player);
          if (!plugin.getUserManager().getUser(player).isSpectator()) {
              ChatManager.broadcastAction(arena, player, ChatManager.ActionType.JOIN);
          }
          if (arena.getArenaState() == ArenaState.STARTING || arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS) {
              player.getInventory().setItem(Objects.requireNonNull(SpecialItemManager.getSpecialItem("Leave")).getSlot(),SpecialItemManager.getSpecialItem("Leave").getItemStack());
              player.getInventory().setItem(Objects.requireNonNull(SpecialItemManager.getSpecialItem("Perks")).getSlot(), SpecialItemManager.getSpecialItem("Perks").getItemStack());
              FileConfiguration donat = ConfigUtils.getConfig(plugin, "donaters");
              if (player.hasPermission(donat.getString("Can-Forcestart-Game-Permission"))) {
                  ItemStack toAdd = SpecialItemManager.getSpecialItem("Start").getItemStack();
                  ItemMeta meta = toAdd.getItemMeta();
                  List<String> lore = meta.getLore();
                  lore.set(0, lore.get(0).replace("{player}", "" + arena.getMinimumPlayers()));
                  meta.setLore(lore);
                  toAdd.setItemMeta(meta);

                  player.getInventory().setItem(SpecialItemManager.getSpecialItem("Start").getSlot(), toAdd);
              }
              player.getInventory().setItem(Objects.requireNonNull(SpecialItemManager.getSpecialItem("Menu")).getSlot(), Objects.requireNonNull(SpecialItemManager.getSpecialItem("Menu")).getItemStack());
          }
          player.updateInventory();
          for (Player arenaPlayer : arena.getPlayers()) {
              ArenaUtils.showPlayer(arenaPlayer, arena);
          }
          arena.showPlayers();
          ArenaUtils.updateNameTagsVisibility(player);
      }

      /**
       * Attempts player to leave arena.
       * Calls MMGameLeaveAttcls
       * <p>
       * emptEvent event.
       *
       * @param player player to join
       * @see MMGameLeaveAttemptEvent
       */
      public static void leaveAttempt(Player player, Arena arena) {

          MMGameLeaveAttemptEvent event = new MMGameLeaveAttemptEvent(player, arena);
          Bukkit.getPluginManager().callEvent(event);
          User user = plugin.getUserManager().getUser(player);
          user.setShots(0);
          ChatEvents.getSaid().removeIf(id -> ChatEvents.getSaid().contains(id));
          int murderDecrease = player.getEffectivePermissions().stream().filter(permAttach -> permAttach.getPermission().startsWith("murdermystery.role.murderer."))
                  .mapToInt(pai -> Integer.parseInt(pai.getPermission().substring(28 /* remove the permission node to obtain the number*/))).max().orElse(0);
          int detectiveDecrease = player.getEffectivePermissions().stream().filter(permAttach -> permAttach.getPermission().startsWith("murdermystery.role.detective."))
                  .mapToInt(pai -> Integer.parseInt(pai.getPermission().substring(29 /* remove the permission node to obtain the number*/))).max().orElse(0);
          user.addStat(StatsStorage.StatisticType.CONTRIBUTION_MURDERER, -murderDecrease);
          if (user.getStat(StatsStorage.StatisticType.CONTRIBUTION_MURDERER) <= 0) {
              user.setStat(StatsStorage.StatisticType.CONTRIBUTION_MURDERER, 1);
          }
          user.addStat(StatsStorage.StatisticType.CONTRIBUTION_DETECTIVE, -detectiveDecrease);
          if (user.getStat(StatsStorage.StatisticType.CONTRIBUTION_DETECTIVE) <= 0) {
              user.setStat(StatsStorage.StatisticType.CONTRIBUTION_DETECTIVE, 1);
          }

          user.getCachedPerks().clear();
          arena.getScoreboardManager().removeScoreboard(user);
          //-1 cause we didn't remove player yet
          if (arena.getArenaState() == ArenaState.IN_GAME && !user.isSpectator()) {
              if (arena.getPlayersLeft().size() - 1 > 1) {
                  if (Role.isRole(Role.MURDERER, player)) {
                      List<Player> players = new ArrayList<>();
                      for (Player gamePlayer : arena.getPlayersLeft()) {
                          if (Role.isRole(Role.ANY_DETECTIVE, gamePlayer) || Role.isRole(Role.MURDERER, gamePlayer)) {
                              continue;
                          }
                          players.add(gamePlayer);
                      }
                      arena.removeFromMurdererList(player);
                      Player newMurderer = players.get(new Random().nextInt(players.size()));
                      arena.setCharacter(Arena.CharacterType.MURDERER, newMurderer);
                      arena.addToMurdererList(newMurderer);
                      String title = ChatManager.colorMessage("In-Game.Messages.Previous-Role-Left-Title", player).replace("%role%",
                              ChatManager.colorMessage("Scoreboard.Roles.Murderer", player));
                      String subtitle = ChatManager.colorMessage("In-Game.Messages.Previous-Role-Left-Subtitle", player).replace("%role%",
                              ChatManager.colorMessage("Scoreboard.Roles.Murderer", player));
                      for (Player gamePlayer : arena.getPlayers()) {
                          gamePlayer.sendTitle(title, subtitle, 5, 40, 5);
                      }
                      newMurderer.sendTitle(ChatManager.colorMessage("In-Game.Messages.Role-Set.Murderer-Title", player),
                              ChatManager.colorMessage("In-Game.Messages.Role-Set.Murderer-Subtitle", player), 5, 40, 5);
                      ItemPosition.setItem(newMurderer, ItemPosition.MURDERER_SWORD, plugin.getConfigPreferences().getMurdererSword());
                      user.setStat(StatsStorage.StatisticType.CONTRIBUTION_MURDERER, 1);
                  } else if (Role.isRole(Role.ANY_DETECTIVE, player) && arena.lastAliveDetective()) {
                      arena.setDetectiveDead(true);
                      if (Role.isRole(Role.FAKE_DETECTIVE, player)) {
                          arena.setCharacter(Arena.CharacterType.FAKE_DETECTIVE, null);
                      } else {
                          user.setStat(StatsStorage.StatisticType.CONTRIBUTION_DETECTIVE, 1);
                      }
                      ArenaUtils.dropBowAndAnnounce(arena, player);
                  }
                  plugin.getCorpseHandler().spawnCorpse(player, arena);
              } else {
                  ArenaManager.stopGame(false, arena);
              }
          }
          player.getInventory().clear();
          player.getInventory().setArmorContents(null);
          arena.removePlayer(player);
          arena.teleportToEndLocation(player);
          if (!user.isSpectator()) {
              ChatManager.broadcastAction(arena, player, ChatManager.ActionType.LEAVE);
          }
          player.setGlowing(false);
          user.setSpectator(false);
          user.removeScoreboard();
          arena.doBarAction(Arena.BarAction.REMOVE, player);
          player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
          player.setFoodLevel(20);
          player.setLevel(0);
          player.setExp(0);
          player.setFlying(false);
          player.setAllowFlight(false);
          for (PotionEffect effect : player.getActivePotionEffects()) {
              player.removePotionEffect(effect.getType());
          }
          player.setWalkSpeed(0.2f);
          player.setFireTicks(0);
          if (arena.getArenaState() != ArenaState.WAITING_FOR_PLAYERS && arena.getArenaState() != ArenaState.STARTING && arena.getPlayers().size() == 0) {
              arena.setArenaState(ArenaState.ENDING);
              arena.setTimer(0);
          }

          player.setGameMode(GameMode.SURVIVAL);
          for (Player players : plugin.getServer().getOnlinePlayers()) {
              if (ArenaRegistry.getArena(players) == null) {
                  players.showPlayer(player);
              }
              player.showPlayer(players);
          }
          arena.teleportToEndLocation(player);
          if (!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)
                  && plugin.getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
              InventorySerializer.loadInventory(plugin, player);
          }
          for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
              if (!stat.isPersistent()) {
                  user.setStat(stat, 0);
              }

          }

      }

      /**
       * Stops current arena. Calls MMGameStopEvent event
       *
       * @param quickStop should arena be stopped immediately? (use only in important cases)
       * @see MMGameStopEvent
       */
      public static void stopGame(boolean quickStop, Arena arena) {

          MMGameStopEvent gameStopEvent = new MMGameStopEvent(arena);
          Bukkit.getPluginManager().callEvent(gameStopEvent);
          arena.setArenaState(ArenaState.ENDING);
          if (quickStop) {
              arena.setTimer(2);
              ChatManager.broadcast(arena, ChatManager.colorRawMessage("&cThe game has been force stopped by command"));
          } else {
              arena.setTimer(10);
          }
          List<String> summaryMessages = LanguageManager.getLanguageList("In-Game.Messages.Game-End-Messages.Summary-Message");
          arena.getScoreboardManager().stopAllScoreboards();
          Random rand = new Random();

          boolean murderWon = arena.getPlayersLeft().size() == arena.aliveMurderer();
          for (final Player player : arena.getPlayers()) {
              User user = plugin.getUserManager().getUser(player);
              int def = user.getStat(StatsStorage.StatisticType.LOCAL_SCORE);
              int vip = Math.round(def * DonaterUtils.getMultiplier(DonatType.VIP));
              int prem = Math.round(def * DonaterUtils.getMultiplier(DonatType.PREM));
              int grand = Math.round(def * DonaterUtils.getMultiplier(DonatType.PREM));
              TextComponent _vip = new TextComponent("§cВам было выдано §6" + (def + vip) + " §cочков");
              TextComponent _prem = new TextComponent("§cВам было выдано §6" + (def + prem) + " §cочков");
              TextComponent _grand = new TextComponent("§cВам было выдано §6" + (def + grand) + " §cочков");
              _vip.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(" §6(" + vip + " дополнительно)").create()));
              _prem.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(" §6(" + prem + " дополнительно)").create()));
              _grand.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(" §6(" + grand + " дополнительно)").create()));

              switch (user.getType()) {
                  case VIP:
                      player.spigot().sendMessage(_vip);
                      user.addStat(StatsStorage.StatisticType.HIGHEST_SCORE, def + vip);
                      break;
                  case PREM:
                      player.spigot().sendMessage(_prem);
                      user.addStat(StatsStorage.StatisticType.HIGHEST_SCORE, def + prem);
                  case GRAND:
                      player.spigot().sendMessage(_grand);
                      user.addStat(StatsStorage.StatisticType.HIGHEST_SCORE, def + grand);
                      break;
                  case DEF:
                      user.addStat(StatsStorage.StatisticType.HIGHEST_SCORE, def);
                      player.sendMessage("§cВам было выдано §6" + def + " §cочков");
                      user.addStat(StatsStorage.StatisticType.HIGHEST_SCORE, def);
                      break;
              }


              if (Role.isRole(Role.FAKE_DETECTIVE, player) || Role.isRole(Role.INNOCENT, player)) {
                  user.setStat(StatsStorage.StatisticType.CONTRIBUTION_MURDERER, rand.nextInt(4) + 1);
                  user.setStat(StatsStorage.StatisticType.CONTRIBUTION_DETECTIVE, rand.nextInt(4) + 1);
              }
              if (murderWon) {
                  if (Role.isRole(Role.MURDERER, player)) {
                      user.addStat(StatsStorage.StatisticType.WINS, 1);
                      plugin.getRewardsHandler().performReward(player, Reward.RewardType.WIN);
                  } else {
                      user.addStat(StatsStorage.StatisticType.LOSES, 1);
                      plugin.getRewardsHandler().performReward(player, Reward.RewardType.LOSE);
                  }
              } else {
                  if (!Role.isRole(Role.MURDERER, player)) {
                      user.addStat(StatsStorage.StatisticType.WINS, 1);
                      plugin.getRewardsHandler().performReward(player, Reward.RewardType.WIN);
                  } else {
                      user.addStat(StatsStorage.StatisticType.LOSES, 1);
                      plugin.getRewardsHandler().performReward(player, Reward.RewardType.LOSE);
                  }
              }
              player.getInventory().clear();
              player.getInventory().setItem(Objects.requireNonNull(SpecialItemManager.getSpecialItem("Leave")).getSlot(), Objects.requireNonNull(SpecialItemManager.getSpecialItem("Leave")).getItemStack());
              if (!quickStop) {
                  for (String msg : summaryMessages) {
                      MiscUtils.sendCenteredMessage(player, formatSummaryPlaceholders(msg, arena, player));
                  }
              }
              user.removeScoreboard();
              if (!quickStop && plugin.getConfig().getBoolean("Firework-When-Game-Ends", true)) {
                  new BukkitRunnable() {
                      int i = 0;

                      @Override
                      public void run() {
                          if (i == 4 || !arena.getPlayers().contains(player)) {
                              this.cancel();
                          }
                          MiscUtils.spawnRandomFirework(player.getLocation());
                          i++;
                      }
                  }.runTaskTimer(plugin, 30, 30);
              }
          }
      }

      private static String formatSummaryPlaceholders(String msg, Arena arena, Player player) {
          String formatted = msg;
          StringBuilder murders = new StringBuilder();
          StringBuilder detectives = new StringBuilder();
          int murdererKills = 0;
          for (Player p : arena.getMurdererList()) {
              murders.append(p.getName()).append(" (").append(plugin.getUserManager().getUser(p).getStat(StatsStorage.StatisticType.LOCAL_KILLS)).append("), ");
              murdererKills = murdererKills + plugin.getUserManager().getUser(p).getStat(StatsStorage.StatisticType.LOCAL_KILLS);
          }
          murders.deleteCharAt(murders.length() - 2);

          for (Player p : arena.getDetectiveList()) {
              detectives.append(p.getName()).append(", ");
          }
          detectives.deleteCharAt(detectives.length() - 2);
          if (arena.getPlayersLeft().size() == arena.aliveMurderer()) {
              formatted = StringUtils.replace(formatted, "%winner%", ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Winners.Murderer"));
          } else {
              formatted = StringUtils.replace(formatted, "%winner%", ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Winners.Players"));
          }
          if (arena.isDetectiveDead()) {
              formatted = StringUtils.replace(formatted, "%detective%", ChatColor.STRIKETHROUGH + detectives.toString());
          } else {
              formatted = StringUtils.replace(formatted, "%detective%", detectives.toString());
          }
          if (arena.lastAliveMurderer()) {
              formatted = StringUtils.replace(formatted, "%murderer%", murders.toString());
          } else {
              formatted = StringUtils.replace(formatted, "%murderer%", ChatColor.STRIKETHROUGH + murders.toString());
          }
          formatted = StringUtils.replace(formatted, "%murderer_kills%",
                  String.valueOf(murdererKills));
          formatted = StringUtils.replace(formatted, "%hero%", arena.isCharacterSet(Arena.CharacterType.HERO)
                  ? arena.getCharacter(Arena.CharacterType.HERO).getName() : ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Winners.Nobody"));
          if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
              formatted = PlaceholderAPI.setPlaceholders(player, formatted);
          }
          return formatted;
      }

  }
