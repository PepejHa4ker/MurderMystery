package com.pepej.murdermystery;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.pepej.murdermystery.api.StatsStorage;
import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.arena.ArenaEvents;
import com.pepej.murdermystery.arena.ArenaRegistry;
import com.pepej.murdermystery.arena.ArenaUtils;
import com.pepej.murdermystery.arena.special.SpecialBlockEvents;
import com.pepej.murdermystery.arena.special.mysterypotion.MysteryPotionRegistry;
import com.pepej.murdermystery.arena.special.pray.PrayerRegistry;
import com.pepej.murdermystery.commands.arguments.ArgumentsRegistry;
import com.pepej.murdermystery.events.*;
import com.pepej.murdermystery.events.spectator.SpectatorEvents;
import com.pepej.murdermystery.events.spectator.SpectatorItemEvents;
import com.pepej.murdermystery.handlers.*;
import com.pepej.murdermystery.handlers.items.SpecialItem;
import com.pepej.murdermystery.handlers.language.LanguageManager;
import com.pepej.murdermystery.handlers.rewards.RewardsFactory;
import com.pepej.murdermystery.handlers.scheduler.Scheduler;
import com.pepej.murdermystery.handlers.sign.SignManager;
import com.pepej.murdermystery.logging.ILogger;
import com.pepej.murdermystery.logging.LoggerImpl;
import com.pepej.murdermystery.perk.PerkRegister;
import com.pepej.murdermystery.plugin.bootstrap.MurderMysteryBootstrap;
import com.pepej.murdermystery.plugin.scheduler.SchedulerAdapter;
import com.pepej.murdermystery.user.RankManager;
import com.pepej.murdermystery.user.User;
import com.pepej.murdermystery.user.UserManager;
import com.pepej.murdermystery.user.data.MysqlManager;
import com.pepej.murdermystery.utils.config.ConfigUtils;
import com.pepej.murdermystery.utils.database.MysqlDatabase;
import com.pepej.murdermystery.utils.serialization.InventorySerializer;
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;


public class MurderMystery extends JavaPlugin implements MurderMysteryBootstrap {

    private static MurderMystery instance;

    private SchedulerAdapter schedulerAdapter;

    private String version;

    private boolean forceDisable = false;

    private ExecutorService executorService;

    private ScheduledExecutorService scheduledExecutorService;

    private BungeeManager bungeeManager;

    private RewardsFactory rewardsHandler;

    private MysqlDatabase database;

    private SignManager signManager;

    private CorpseHandler corpseHandler;

    private ConfigPreferences configPreferences;

    private HookManager hookManager;

    private UserManager userManager;

    private Economy economy;

    private ILogger logger = null;

    @Override
    public ILogger getPluginLogger() {
        if (this.logger == null) {
            throw new IllegalStateException("Logger has not been initialised yet");
        }
        return this.logger;
    }

    @Override
    public int getPlayerCount() {
        return this.getServer().getOnlinePlayers().size();
    }

    @Override
    public boolean isPlayerOnline(UUID uniqueId) {
        return this.getServer()
                   .getOnlinePlayers()
                   .stream()
                   .map(Player::getUniqueId)
                   .collect(Collectors.toList())
                   .contains(uniqueId);
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public ExecutorService getExecutorService() {
        return this.executorService;
    }

    @Override
    public SchedulerAdapter getScheduler() {
        return schedulerAdapter;
    }

    @Override
    public ScheduledExecutorService getScheduledExecutorService() {
        return this.scheduledExecutorService;
    }

    @Override
    public BungeeManager getBungeeManager() {
        return this.bungeeManager;
    }

    @Override
    public RewardsFactory getRewardsHandler() {
        return this.rewardsHandler;
    }

    @Override
    public MysqlDatabase getDatabase() {
        return this.database;
    }

    @Override
    public SignManager getSignManager() {
        return this.signManager;
    }

    @Override
    public CorpseHandler getCorpseHandler() {
        return this.corpseHandler;
    }

    @Override
    public ConfigPreferences getConfigPreferences() {
        return this.configPreferences;
    }

    @Override
    public HookManager getHookManager() {
        return this.hookManager;
    }

    @Override
    public UserManager getUserManager() {
        return this.userManager;
    }

    @Override
    public Economy getEconomy() {
        return this.economy;
    }

    @Override
    public void onLoad() {
        this.schedulerAdapter = new MurderMysterySchedulerAdapter(this);
        this.logger = new LoggerImpl(this.getLogger());
        this.executorService = Scheduler.createExecutorService();
        this.scheduledExecutorService = Scheduler.createScheduledExecutorService();
    }

    @Override
    public void onEnable() {
        instance = this;
        if (!validateIfPluginShouldStart()) {
            return;
        }
        setupEconomy();
        saveDefaultConfig();
        configPreferences = new ConfigPreferences(this);
        setupFiles();
        initializeClasses();


        //start hook manager later in order to allow soft-dependencies to fully load
        Bukkit.getScheduler().runTaskLater(this, () -> hookManager = new HookManager(), 20L * 5);
        if (configPreferences.getOption(ConfigPreferences.Option.NAMETAGS_HIDDEN)) {
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ArenaUtils.updateNameTagsVisibility(player);
                }
            }, 60, 140);
        }
    }

    private boolean validateIfPluginShouldStart() {
        version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        if (!version.equalsIgnoreCase("v1_12_R1")) {
            MurderMystery.getInstance().getPluginLogger().severe("Your server version is not supported by Murder Mystery!");
            MurderMystery.getInstance().getPluginLogger().severe("Sadly, we must shut off. Maybe you consider changing your server version?");
            forceDisable = true;
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        try {
            Class.forName("org.spigotmc.SpigotConfig");
        } catch (Exception e) {
            MurderMystery.getInstance().getPluginLogger().severe("Your server software is not supported by Murder Mystery!");
            MurderMystery.getInstance().getPluginLogger().severe( "We support only Spigot and Spigot forks only! Shutting off...");
            forceDisable = true;
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        return true;
    }


    @Override
    public void onDisable() {
        if (forceDisable) {
            return;
        }

        saveAllUserStatistics();
        if (hookManager != null && hookManager.isFeatureEnabled(HookManager.HookFeature.CORPSES)) {
            for (Hologram hologram : HologramsAPI.getHolograms(this)) {
                hologram.delete();
            }
        }
        if (configPreferences.getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
            this.getDatabase().shutdownConnPool();
        }

        for (Arena arena : ArenaRegistry.getArenas()) {
            arena.getScoreboardManager().stopAllScoreboards();
            for (Player player : arena.getPlayers()) {
                arena.doBarAction(Arena.BarAction.REMOVE, player);
                arena.teleportToEndLocation(player);
                if (configPreferences.getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
                    InventorySerializer.loadInventory(this, player);
                } else {
                    player.getInventory().clear();
                    player.getInventory().setArmorContents(null);
                    for (PotionEffect pe : player.getActivePotionEffects()) {
                        player.removePotionEffect(pe.getType());
                    }
                    player.setWalkSpeed(0.2f);
                }
            }
            arena.teleportAllToEndLocation();
            arena.cleanUpArena();
        }
    }

    private void initializeClasses() {
        ScoreboardLib.setPluginInstance(this);
        if (getConfig().getBoolean("BungeeActivated", false)) {
            bungeeManager = new BungeeManager(this);
        }
        if (configPreferences.getOption(ConfigPreferences.Option.DATABASE_ENABLED)) {
            FileConfiguration config = ConfigUtils.getConfig(this, "mysql");
            database = new MysqlDatabase(config.getString("user"), config.getString("password"), config.getString("address"));
        }
        new ArgumentsRegistry(this);
        userManager = new UserManager(this);
        SpecialItem.loadAll();
        new PerkRegister().initPerks();
        PermissionsManager.init();
        RankManager.setupRanks();
        LanguageManager.init(this);
        new ArenaEvents();
        new SpectatorEvents(this);
        new QuitEvent(this);
        new JoinEvent(this);
        new ChatEvents(this);
        registerSoftDependenciesAndServices();
        User.cooldownHandlerTask();
        ArenaRegistry.registerArenas();
        new Events(this);
        new LobbyEvent(this);
        new SpectatorItemEvents(this);
        rewardsHandler = new RewardsFactory(this);
        signManager = new SignManager(this);
        corpseHandler = new CorpseHandler(this);
        new BowTrailsHandler(this);
        MysteryPotionRegistry.init(this);
        PrayerRegistry.init(this);
        new SpecialBlockEvents(this);
    }

    private void registerSoftDependenciesAndServices() {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderManager().register();
        }
    }

    private void setupFiles() {
        for (String fileName : Arrays.asList("arenas", "bungee", "rewards", "stats", "lobbyitems", "mysql", "specialblocks", "filter", "ranks", "donaters")) {
            File file = new File(getDataFolder() + File.separator + fileName + ".yml");
            if (!file.exists()) {
                saveResource(fileName + ".yml", false);
            }
        }
    }

    private void saveAllUserStatistics() {
        for (Player player : getServer().getOnlinePlayers()) {
            User user = userManager.getUser(player);

            //copy of userManager#saveStatistic but without async database call that's not allowed in onDisable method.
            for (StatsStorage.StatisticType stat : StatsStorage.StatisticType.values()) {
                if (!stat.isPersistent()) {
                    continue;
                }
                if (userManager.getDatabase() instanceof MysqlManager) {
                    ((MysqlManager) userManager.getDatabase()).getDatabase().executeUpdate("UPDATE playerstats SET " + stat.getName() + "=" + user.getStat(stat) + " WHERE UUID='" + user.getPlayer().getUniqueId().toString() + "';");
                    continue;
                }
                userManager.getDatabase().saveStatistic(user, stat);
            }
        }
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            economy = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        }
    }

    public static MurderMystery getInstance() {
        return instance;
    }
}
