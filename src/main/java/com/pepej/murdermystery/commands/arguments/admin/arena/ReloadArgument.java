

package com.pepej.murdermystery.commands.arguments.admin.arena;

import com.pepej.murdermystery.ConfigPreferences;
import com.pepej.murdermystery.arena.Arena;
import com.pepej.murdermystery.arena.ArenaManager;
import com.pepej.murdermystery.arena.ArenaRegistry;
import com.pepej.murdermystery.commands.arguments.ArgumentsRegistry;
import com.pepej.murdermystery.commands.arguments.data.CommandArgument;
import com.pepej.murdermystery.commands.arguments.data.LabelData;
import com.pepej.murdermystery.commands.arguments.data.LabeledCommandArgument;
import com.pepej.murdermystery.handlers.ChatManager;
import com.pepej.murdermystery.handlers.language.LanguageManager;
import com.pepej.murdermystery.utils.serialization.InventorySerializer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Plajer
 * <p>
 * Created at 18.05.2019
 */

@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)
public class ReloadArgument {


  Set<CommandSender> confirmations = new HashSet<>();

  public ReloadArgument(ArgumentsRegistry registry) {
    registry.mapArgument("murdermysteryadmin", new LabeledCommandArgument("reload", "murdermystery.admin.reload", CommandArgument.ExecutorType.BOTH,
      new LabelData("/mma reload", "/mma reload", "&7Reload all game arenas and configurations\n&7&lArenas will be stopped!\n&6Permission: &7murdermystery.admin.reload")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        if (!confirmations.contains(sender)) {
          confirmations.add(sender);
          Bukkit.getScheduler().runTaskLater(registry.getPlugin(), () -> confirmations.remove(sender), 20 * 10);
          sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorRawMessage("&cВы уверенны что хотите выполнить это действие? Пропишите команду &6ещё раз в течении 10 сек. для продолжения"));
          return;
        }
        confirmations.remove(sender);

        registry.getPlugin().reloadConfig();
        LanguageManager.reloadConfig();

        for (Arena arena : ArenaRegistry.getArenas()) {
          for (Player player : arena.getPlayers()) {
            arena.doBarAction(Arena.BarAction.REMOVE, player);
            arena.teleportToEndLocation(player);
            if (registry.getPlugin().getConfigPreferences().getOption(ConfigPreferences.Option.INVENTORY_MANAGER_ENABLED)) {
              InventorySerializer.loadInventory(registry.getPlugin(), player);
            } else {
              player.getInventory().clear();
              player.getInventory().setArmorContents(null);
              for (PotionEffect pe : player.getActivePotionEffects()) {
                player.removePotionEffect(pe.getType());
              }
              player.setWalkSpeed(0.2f);
            }
          }
          ArenaManager.stopGame(true, arena);
        }
        ArenaRegistry.registerArenas();
        sender.sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("Commands.Admin-Commands.Success-Reload"));
      }
    });
  }

}
