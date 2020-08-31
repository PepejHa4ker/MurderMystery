

package pl.plajer.murdermystery.handlers.language;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.locale.Locale;
import pl.plajer.murdermystery.locale.LocaleRegistry;
import pl.plajer.murdermystery.utils.config.ConfigUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class LanguageManager {

  private static MurderMystery plugin;
  private static Locale pluginLocale;
  private static final Properties properties = new Properties();
  private static FileConfiguration languageConfig;

  private LanguageManager() {
  }

  public static void init(MurderMystery plugin) {
    LanguageManager.plugin = plugin;
    if (!new File(LanguageManager.plugin.getDataFolder() + File.separator + "language.yml").exists()) {
      LanguageManager.plugin.saveResource("language.yml", false);
    }
    new LanguageMigrator(plugin);
    languageConfig = ConfigUtils.getConfig(plugin, "language");
    registerLocales();
    setupLocale();
  }

  private static void registerLocales() {
    LocaleRegistry.registerLocale(new Locale("English", "English", "en_GB", "Plajer", Arrays.asList("default", "english", "en")));

  }

  private static void loadProperties() {

    pluginLocale = LocaleRegistry.getByName("English");
  }

  private static void setupLocale() {
    String localeName = plugin.getConfig().getString("locale", "default").toLowerCase();
    for (Locale locale : LocaleRegistry.getRegisteredLocales()) {
      if (locale.getPrefix().equalsIgnoreCase(localeName)) {
        pluginLocale = locale;
        break;
      }
      for (String alias : locale.getAliases()) {
        if (alias.equals(localeName)) {
          pluginLocale = locale;
          break;
        }
      }
    }
    if (pluginLocale == null) {
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Murder Mystery] Plugin locale is invalid! Using default one...");
      pluginLocale = LocaleRegistry.getByName("English");
    }
    Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Murder Mystery] Loaded locale " + pluginLocale.getName() + " (" + pluginLocale.getOriginalName() + " ID: "
      + pluginLocale.getPrefix() + ") by " + pluginLocale.getAuthor());
    loadProperties();
  }

  public static boolean isDefaultLanguageUsed() {
    return pluginLocale.getName().equals("English");
  }

  public static String getLanguageMessage(String path) {
    if (isDefaultLanguageUsed()) {
      return getString(path);
    }
    String prop = properties.getProperty(path);
    if (prop == null) {
      return getString(path);
    }
    return prop;
  }

  public static List<String> getLanguageList(String path) {
    if (isDefaultLanguageUsed()) {
      return getStrings(path);
    }
    String prop = properties.getProperty(path);
    if (prop == null) {
      //check normal language if nothing found in specific language
      return getStrings(path);
    }
    return Arrays.asList(ChatManager.colorMessage(path).split(";"));
  }


  private static List<String> getStrings(String path) {
    //check normal language if nothing found in specific language
    if (!languageConfig.isSet(path)) {
      //send normal english message - User can change this translation on his own
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Murder Mystery] Game message not found in your locale! Added it to your language.yml");
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Murder Mystery] Path: " + path + " | Language not found. Report it to the author on Discord!");
    }
    List<String> list = languageConfig.getStringList(path);
    list = list.stream().map(string -> ChatColor.translateAlternateColorCodes('&', string)).collect(Collectors.toList());
    return list;
  }


  private static String getString(String path) {
    //check normal language if nothing found in specific language
    if (!languageConfig.isSet(path)) {
      //send normal english message - User can change this translation on his own
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Murder Mystery] Game message not found in your locale! Added it to your language.yml");
      Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Murder Mystery] Path: " + path + " | Language not found. Report it to the author on Discord!");
    }
    return languageConfig.getString(path);
  }

  public static void reloadConfig() {
    languageConfig = ConfigUtils.getConfig(plugin, "language");
  }

  public static Locale getPluginLocale() {
    return pluginLocale;
  }
}
