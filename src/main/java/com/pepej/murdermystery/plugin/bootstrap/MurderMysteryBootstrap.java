package com.pepej.murdermystery.plugin.bootstrap;

import com.pepej.murdermystery.ConfigPreferences;
import com.pepej.murdermystery.HookManager;
import com.pepej.murdermystery.handlers.BungeeManager;
import com.pepej.murdermystery.handlers.CorpseHandler;
import com.pepej.murdermystery.handlers.rewards.RewardsFactory;
import com.pepej.murdermystery.handlers.sign.SignManager;
import com.pepej.murdermystery.logging.ILogger;
import com.pepej.murdermystery.plugin.scheduler.SchedulerAdapter;
import com.pepej.murdermystery.user.UserManager;
import com.pepej.murdermystery.utils.database.MysqlDatabase;
import net.milkbowl.vault.economy.Economy;

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
