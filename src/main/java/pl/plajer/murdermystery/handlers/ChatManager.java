package pl.plajer.murdermystery.handlers;

import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.handlers.language.LanguageManager;
import pl.plajer.murdermystery.utils.strings.StringFormatUtils;

@UtilityClass
public class ChatManager {

  public final String PLUGIN_PREFIX = "§cMurderMystery §7> ";
  private final MurderMystery plugin = MurderMystery.getInstance();

  public void sendMessage(Player player, String message) {
    message = ChatManager.colorRawMessage(message);
    player.sendMessage(PLUGIN_PREFIX + message);
  }

  public String colorRawMessage(String message) {
    return ChatColor.translateAlternateColorCodes('&', message);
  }

  public String colorMessage(String message) {
      return ChatColor.translateAlternateColorCodes('&', LanguageManager.getLanguageMessage(message));
  }

  public String colorMessage(String message, Player player) {
    String returnString = LanguageManager.getLanguageMessage(message);
    if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      returnString = PlaceholderAPI.setPlaceholders(player, returnString);
    }
    return ChatColor.translateAlternateColorCodes('&', returnString);
  }

  public void broadcast(Arena arena, String message) {
    for (Player p : arena.getPlayers()) {
      p.sendMessage(PLUGIN_PREFIX + ChatManager.colorRawMessage(message));
    }
  }

  public String formatMessage(Arena arena, String message, int integer) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%NUMBER%", Integer.toString(integer));
    returnString = colorRawMessage(formatPlaceholders(returnString, arena));
    return returnString;
  }

  public String formatMessage(Arena arena, String message, Player player) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%PLAYER%", player.getName());
    returnString = colorRawMessage(formatPlaceholders(returnString, arena));
    if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
      returnString = PlaceholderAPI.setPlaceholders(player, returnString);
    }
    return returnString;
  }

  private String formatPlaceholders(String message, Arena arena) {
    String returnString = message;
    returnString = StringUtils.replace(returnString, "%ARENANAME%", arena.getMapName());
    returnString = StringUtils.replace(returnString, "%TIME%", Integer.toString(arena.getTimer()));
    returnString = StringUtils.replace(returnString, "%FORMATTEDTIME%", StringFormatUtils.formatIntoMMSS((arena.getTimer())));
    returnString = StringUtils.replace(returnString, "%PLAYERSIZE%", Integer.toString(arena.getPlayers().size()));
    returnString = StringUtils.replace(returnString, "%LEFTPLAYERS%", Integer.toString(arena.getPlayersLeft().size() - 1));
    returnString = StringUtils.replace(returnString, "%MAXPLAYERS%", Integer.toString(arena.getMaximumPlayers()));
    returnString = StringUtils.replace(returnString, "%MINPLAYERS%", Integer.toString(arena.getMinimumPlayers()));
    return returnString;
  }

  public void broadcastAction(Arena a, Player p, ActionType action) {
    String message;
    switch (action) {
      case JOIN:
        message = formatMessage(a, ChatManager.colorMessage("In-Game.Messages.Join"), p);
        break;
      case LEAVE:
        message = formatMessage(a, ChatManager.colorMessage("In-Game.Messages.Leave"), p);
        break;
      case DEATH:
        message = formatMessage(a, ChatManager.colorMessage("In-Game.Messages.Death"), p);
        break;
      default:
        return; //likely won't ever happen
    }
    for (Player player : a.getPlayers()) {
      player.sendMessage(PLUGIN_PREFIX + message);
    }
  }

  public enum ActionType {
    JOIN, LEAVE, DEATH
  }

}
