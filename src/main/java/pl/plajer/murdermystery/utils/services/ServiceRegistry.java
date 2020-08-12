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
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package pl.plajer.murdermystery.utils.services;

import lombok.experimental.UtilityClass;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.murdermystery.utils.services.locale.LocaleService;

import java.util.logging.Level;

/**
 * Class for registering new services
 */
@UtilityClass
public class ServiceRegistry {

  private JavaPlugin registeredService;
  private boolean serviceEnabled;
  private long serviceCooldown = 0;
  private LocaleService localeService;

  public void registerService(JavaPlugin plugin) {
    if (registeredService != null && registeredService.equals(plugin)) {
      return;
    }
    serviceEnabled = true;
    registeredService = plugin;
    plugin.getLogger().log(Level.INFO, "Hooked with ServiceRegistry! Initialized services properly!");
    localeService = new LocaleService(plugin);
  }

  public JavaPlugin getRegisteredService() {
    return registeredService;
  }

  public long getServiceCooldown() {
    return serviceCooldown;
  }

  public void setServiceCooldown(long serviceCooldown) {
    ServiceRegistry.serviceCooldown = serviceCooldown;
  }

  public LocaleService getLocaleService(JavaPlugin plugin) {
    if (!serviceEnabled || registeredService == null || !registeredService.equals(plugin)) {
      return null;
    }
    return localeService;
  }

  public boolean isServiceEnabled() {
    return serviceEnabled;
  }
}
