/*
 * MurderMystery - Find the murderer, kill him and survive!
 * Copyright (C) 2020  Plajer's Lair - maintained by Tigerpanzer_02, Plajer and contributors
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

package pl.plajer.murdermystery;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import me.tigerhix.lib.scoreboard.ScoreboardLib;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import pl.plajer.murdermystery.api.StatsStorage;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.arena.ArenaEvents;
import pl.plajer.murdermystery.arena.ArenaRegistry;
import pl.plajer.murdermystery.arena.ArenaUtils;
import pl.plajer.murdermystery.arena.special.SpecialBlockEvents;
import pl.plajer.murdermystery.arena.special.mysterypotion.MysteryPotionRegistry;
import pl.plajer.murdermystery.arena.special.pray.PrayerRegistry;
import pl.plajer.murdermystery.commands.arguments.ArgumentsRegistry;
import pl.plajer.murdermystery.events.*;
import pl.plajer.murdermystery.events.spectator.SpectatorEvents;
import pl.plajer.murdermystery.events.spectator.SpectatorItemEvents;
import pl.plajer.murdermystery.handlers.*;
import pl.plajer.murdermystery.handlers.items.SpecialItem;
import pl.plajer.murdermystery.handlers.language.LanguageManager;
import pl.plajer.murdermystery.handlers.party.PartyHandler;
import pl.plajer.murdermystery.handlers.party.PartySupportInitializer;
import pl.plajer.murdermystery.handlers.rewards.RewardsFactory;
import pl.plajer.murdermystery.handlers.scheduler.Scheduler;
import pl.plajer.murdermystery.handlers.sign.SignManager;
import pl.plajer.murdermystery.logging.LoggerImpl;
import pl.plajer.murdermystery.logging.PluginLogger;
import pl.plajer.murdermystery.perks.Perk;
import pl.plajer.murdermystery.plugin.bootstrap.MurderMysteryBootstrap;
import pl.plajer.murdermystery.plugin.scheduler.SchedulerAdapter;
import pl.plajer.murdermystery.user.RankManager;
import pl.plajer.murdermystery.user.User;
import pl.plajer.murdermystery.user.UserManager;
import pl.plajer.murdermystery.user.data.MysqlManager;
import pl.plajer.murdermystery.utils.MessageUtils;
import pl.plajer.murdermystery.utils.Utils;
import pl.plajer.murdermystery.utils.config.ConfigUtils;
import pl.plajer.murdermystery.utils.database.MysqlDatabase;
import pl.plajer.murdermystery.utils.serialization.InventorySerializer;
import pl.plajer.murdermystery.utils.services.ServiceRegistry;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;


@FieldDefaults(level = AccessLevel.PRIVATE)
public class MurderMystery extends JavaPlugin implements MurderMysteryBootstrap {

    @Getter
    private static MurderMystery instance;

    SchedulerAdapter schedulerAdapter;

    String version;

    boolean forceDisable = false;

    ExecutorService executorService;

    ScheduledExecutorService scheduledExecutorService;

    BungeeManager bungeeManager;

    RewardsFactory rewardsHandler;

    MysqlDatabase database;

    SignManager signManager;

    CorpseHandler corpseHandler;

    PartyHandler partyHandler;

    ConfigPreferences configPreferences;

    HookManager hookManager;

    UserManager userManager;

    Economy economy;

    PluginLogger logger = null;


    @Override
    public PluginLogger getPluginLogger() {
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
    public CorpseHandler getSorpseHandler() {
        return this.corpseHandler;
    }

    @Override
    public PartyHandler getPartyHandler() {
        return this.partyHandler;
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
    }

    @Override
    public void onEnable() {
        instance = this;
        if (!validateIfPluginShouldStart()) {
            return;
        }
        executorService = Scheduler.createExecutorService();
        scheduledExecutorService = Scheduler.createScheduledExecutorService();
        Perk.init();
        setupEconomy();
        RankManager.setupRanks();
        ServiceRegistry.registerService(this);
        LanguageManager.init(this);
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
        if (!(version.equalsIgnoreCase("v1_12_R1") || version.equalsIgnoreCase("v1_13_R1")
                || version.equalsIgnoreCase("v1_13_R2") || version.equalsIgnoreCase("v1_14_R1") || version.equalsIgnoreCase("v1_15_R1"))) {
            MessageUtils.thisVersionIsNotSupported();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Your server version is not supported by Murder Mystery!");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Sadly, we must shut off. Maybe you consider changing your server version?");
            forceDisable = true;
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        try {
            Class.forName("org.spigotmc.SpigotConfig");
        } catch (Exception e) {
            MessageUtils.thisVersionIsNotSupported();
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Your server software is not supported by Murder Mystery!");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "We support only Spigot and Spigot forks only! Shutting off...");
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
        Utils.init(this);
        SpecialItem.loadAll();
        PermissionsManager.init();
        new ArenaEvents(this);
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
        partyHandler = new PartySupportInitializer().initialize();
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

    public boolean is1_12_R1() {
        return version.equalsIgnoreCase("v1_12_R1");
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
}
