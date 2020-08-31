package pl.plajer.murdermystery.handlers;


import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.murdermystery.MurderMystery;


public class PermissionsManager {

  private static final MurderMystery plugin = JavaPlugin.getPlugin(MurderMystery.class);
  private static String joinFullPerm = "murdermystery.fullgames";
  private static String joinPerm = "murdermystery.join.<arena>";

  public static void init() {
    setupPermissions();
  }

  public static String getJoinFullGames() {
    return joinFullPerm;
  }

  private static void setJoinFullGames(String joinFullGames) {
    PermissionsManager.joinFullPerm = joinFullGames;
  }

  public static String getJoinPerm() {
    return joinPerm;
  }

  private static void setJoinPerm(String joinPerm) {
    PermissionsManager.joinPerm = joinPerm;
  }

  private static void setupPermissions() {
    PermissionsManager.setJoinFullGames(plugin.getConfig().getString("Basic-Permissions.Full-Games-Permission", "murdermystery.fullgames"));
    PermissionsManager.setJoinPerm(plugin.getConfig().getString("Basic-Permissions.Join-Permission", "murdermystery.join.<arena>"));
  }

}
