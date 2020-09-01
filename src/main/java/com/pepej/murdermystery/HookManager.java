
package com.pepej.murdermystery;

import org.bukkit.Bukkit;

import java.util.EnumMap;
import java.util.Map;


public class HookManager {

  private final Map<HookFeature, Boolean> hooks = new EnumMap<>(HookFeature.class);


  public HookManager() {
    enableHooks();
  }


  private void enableHooks() {
    for (HookFeature feature : HookFeature.values()) {
      boolean hooked = true;
      for (Hook requiredHook : feature.getRequiredHooks()) {
        if (!Bukkit.getPluginManager().isPluginEnabled(requiredHook.getPluginName())) {
          hooks.put(feature, false);
          hooked = false;
          break;
        }
      }
      if (hooked) {
        hooks.put(feature, true);

      }
    }
  }

  public boolean isFeatureEnabled(HookFeature feature) {
    return hooks.get(feature);
  }

  public enum HookFeature {
    CORPSES(Hook.CORPSE_REBORN, Hook.HOLOGRAPHIC_DISPLAYS);

    private final Hook[] requiredHooks;

    HookFeature(Hook... requiredHooks) {
      this.requiredHooks = requiredHooks;
    }

    public Hook[] getRequiredHooks() {
      return requiredHooks;
    }
  }

  public enum Hook {
    CORPSE_REBORN("CorpseReborn"), HOLOGRAPHIC_DISPLAYS("HolographicDisplays");

    private final String pluginName;

    Hook(String pluginName) {
      this.pluginName = pluginName;
    }

    public String getPluginName() {
      return pluginName;
    }
  }

}
