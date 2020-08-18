package pl.plajer.murdermystery.plugin.bootstrap;

import net.milkbowl.vault.economy.Economy;
import pl.plajer.murdermystery.ConfigPreferences;
import pl.plajer.murdermystery.HookManager;
import pl.plajer.murdermystery.handlers.BungeeManager;
import pl.plajer.murdermystery.handlers.CorpseHandler;
import pl.plajer.murdermystery.handlers.rewards.RewardsFactory;
import pl.plajer.murdermystery.handlers.sign.SignManager;
import pl.plajer.murdermystery.logging.ILogger;
import pl.plajer.murdermystery.plugin.scheduler.SchedulerAdapter;
import pl.plajer.murdermystery.user.UserManager;
import pl.plajer.murdermystery.utils.database.MysqlDatabase;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public interface MurderMysteryBootstrap {

    ILogger getPluginLogger();

    int getPlayerCount();

    boolean isPlayerOnline(UUID uniqueId);

    String getVersion();

    ExecutorService getExecutorService();

    SchedulerAdapter getScheduler();

    ScheduledExecutorService getScheduledExecutorService();

    BungeeManager getBungeeManager();

    RewardsFactory getRewardsHandler();

    MysqlDatabase getDatabase();

    SignManager getSignManager();

    CorpseHandler getCorpseHandler();

    ConfigPreferences getConfigPreferences();

    HookManager getHookManager();

    UserManager getUserManager();

    Economy getEconomy();

}
