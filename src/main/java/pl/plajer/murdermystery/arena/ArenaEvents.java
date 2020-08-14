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

package pl.plajer.murdermystery.arena;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.val;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;
import pl.plajer.murdermystery.ConfigPreferences;
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.api.StatsStorage;
import pl.plajer.murdermystery.api.events.game.MMGameLeaveAttemptEvent;
import pl.plajer.murdermystery.arena.role.Role;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.handlers.gui.Confirmation;
import pl.plajer.murdermystery.handlers.items.SpecialItemManager;
import pl.plajer.murdermystery.handlers.rewards.Reward;
import pl.plajer.murdermystery.perks.ExtremeGoldPerk;
import pl.plajer.murdermystery.perks.Perk;
import pl.plajer.murdermystery.perks.SecondChancePerk;
import pl.plajer.murdermystery.perks.PovodokEbaniyPerk;
import pl.plajer.murdermystery.user.User;
import pl.plajer.murdermystery.utils.Utils;
import pl.plajer.murdermystery.utils.compat.XMaterial;
import pl.plajer.murdermystery.utils.config.ConfigUtils;
import pl.plajer.murdermystery.utils.items.ItemBuilder;
import pl.plajer.murdermystery.utils.items.ItemPosition;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Plajer
 * <p>
 * Created at 13.03.2018
 */
@SuppressWarnings("deprecation")
@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)

public class ArenaEvents implements Listener {

    private MurderMystery plugin;

    public ArenaEvents() {
        this.plugin = MurderMystery.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }


    @EventHandler
    public void onGoldGift(PlayerInteractEntityEvent e) {
        if (e.getPlayer().getInventory().getItemInMainHand().getType() != Material.GOLD_INGOT
                || !e.getPlayer().isSneaking()
                || !(e.getRightClicked() instanceof Player)
                || ArenaRegistry.getArena(e.getPlayer()) == null
        ) return;
        Player player = (Player) e.getRightClicked();
        User clicker = plugin.getUserManager().getUser(e.getPlayer());
        User clicked = plugin.getUserManager().getUser(player);
        if (!ArenaUtils.areInSameArena(e.getPlayer(), player) || ArenaRegistry.getArena(e.getPlayer()).getArenaState() != ArenaState.IN_GAME || clicked.isSpectator())
            return;
        Gui g = new Gui(plugin, 1, "Укажите кол-во золота");
        StaticPane pane = new StaticPane(9, 1);
        g.addPane(pane);
        AtomicInteger gold = new AtomicInteger(clicker.getStat(StatsStorage.StatisticType.LOCAL_GOLD));
        pane.fillWith(new ItemBuilder(Material.STAINED_GLASS_PANE)
                .name("&1")
                .color((short) 13)
                .build(), ev -> ev.setCancelled(true));
        pane.addItem(new GuiItem(new ItemBuilder(Material.GOLD_INGOT)
                .name("&eУкажите количество золота")
                .lore("&cЛевый &6клик - добавить", "&cПравый &6клик - убавить")
                .amount(gold.get()).build(), event -> {
            event.setCancelled(true);
            if (event.getClick() == ClickType.LEFT && event.getCurrentItem() != null && event.getCurrentItem().getAmount() < clicker.getStat(StatsStorage.StatisticType.LOCAL_GOLD)) {
                event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() + 1);
                gold.getAndIncrement();
            }
            if (event.getClick() == ClickType.RIGHT && event.getCurrentItem() != null && event.getCurrentItem().getAmount() > 1) {
                event.getCurrentItem().setAmount(event.getCurrentItem().getAmount() - 1);
                gold.getAndDecrement();
            }
        }), 4, 0);
        pane.addItem(new GuiItem(new ItemBuilder(Material.EMERALD)
                .name("§6Отдать")
                .build(), ev -> {
            Player p = (Player) ev.getWhoClicked();
            ev.getWhoClicked().closeInventory();
            new Confirmation(plugin,
                    "§6Вы действительно хотите передать §c" + gold.get() + " §6игроку §a" + player.getName() + "?")
                    .onDecline((pl, event) -> pl.closeInventory())
                    .onTopClick((pl, event) -> event.setCancelled(true))
                    .onAccept((pl, event) -> {
                        player.sendMessage("§6Игрок §a" + pl.getName() + " §6передал Вам §c" + gold + " золота");
                        pl.sendMessage("§6Вы успешно передали §c" + gold.get() + " §6золота игроку §a" + player.getName());
                        pl.closeInventory();
                        clicker.addStat(StatsStorage.StatisticType.LOCAL_GOLD, -gold.get());
                        clicked.addStat(StatsStorage.StatisticType.LOCAL_GOLD, gold.get());
                        ItemPosition.addItem(pl, ItemPosition.GOLD_INGOTS, new ItemStack(Material.GOLD_INGOT, -gold.get()));
                        ItemPosition.addItem(player, ItemPosition.GOLD_INGOTS, new ItemStack(Material.GOLD_INGOT, gold.get()));
                        if (Role.isRole(Role.INNOCENT, player)) {
                            if (clicked.getStat(StatsStorage.StatisticType.LOCAL_GOLD) >= 10) {
                                clicked.setStat(StatsStorage.StatisticType.LOCAL_GOLD, clicked.getStat(StatsStorage.StatisticType.LOCAL_GOLD) - 10);
                                player.sendTitle(ChatManager.colorMessage("In-Game.Messages.Bow-Messages.Bow-Shot-For-Gold", e.getPlayer()), ChatManager.colorMessage("In-Game.Messages.Bow-Messages.Bow-Shot-Subtitle", e.getPlayer()), 5, 40, 5);
                                ItemPosition.setItem(player, ItemPosition.BOW, new ItemStack(Material.BOW, 1));
                                ItemPosition.addItem(player, ItemPosition.ARROWS, new ItemStack(Material.ARROW, plugin.getConfig().getInt("Detective-Default-Arrows", 3)));
                                player.getInventory().setItem(/* same for all roles */ ItemPosition.GOLD_INGOTS.getOtherRolesItemPosition(), new ItemStack(Material.GOLD_INGOT, -10));
                            }
                        }

                    })
                    .build()
                    .show(p);
        }), 8, 0);
        g.show(e.getPlayer());
    }



    @EventHandler
    public void onArmorStandEject(EntityDismountEvent e) {
        if (!(e.getEntity() instanceof ArmorStand) || e.getEntity().getCustomName() == null
                || !e.getEntity().getCustomName().equals("MurderMysteryArmorStand")) {
            return;
        }
        if (!(e.getDismounted() instanceof Player)) {
            return;
        }
        if (e.getDismounted().isDead()) {
            e.getEntity().remove();
        }
        //we could use setCancelled here but for 1.12 support we cannot (no api)
        e.getDismounted().addPassenger(e.getEntity());
    }


    @EventHandler
    public void onFallDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        Player victim = (Player) e.getEntity();
        Arena arena = ArenaRegistry.getArena(victim);
        if (arena == null) {
            return;
        }
        if (e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            if (!plugin.getConfigPreferences().getOption(ConfigPreferences.Option.DISABLE_FALL_DAMAGE)) {
                if (e.getDamage() >= 20.0) {
                    //kill the player for suicidal death, else do not
                    victim.damage(1000);
                    victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_PLAYER_DEATH, 50f, 1f);
                }
            }
            e.setCancelled(true);
        }
        //kill the player and move to the spawn point
        if (e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
            victim.damage(1000);
            victim.teleport(arena.getPlayerSpawnPoints().get(0));
        }
    }

    @EventHandler
    public void onLeave(MMGameLeaveAttemptEvent event) {
        if (event.getArena().getArenaState() == ArenaState.IN_GAME || event.getArena().getArenaState() == ArenaState.ENDING) {
            for (final User user : plugin.getUserManager().getUsers(event.getArena())) {
                user.getPerks().clear();
            }
        }
    }

    private void killPlayer(Player player, Arena arena) {
        if (Perk.has(player, SecondChancePerk.class)) {
            val perk = Perk.getPerkByClass(SecondChancePerk.class);
            perk.handle(player, null, arena);
            if (perk.success()) {
               ArenaManager.sendArenaMessages(arena, "&cИгрок &a" + player.getName() + "&c удрал от маньяка!");
                return;
            }
        }
        player.damage(1000);
        player.teleport(arena.getPlayerSpawnPoints().get(0));
        for (Player p : arena.getPlayers()) {
            p.playSound(p.getLocation(), Sound.ENTITY_WITCH_HURT, 1f, 1f);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_DEATH, 1f, 1f);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (ArenaRegistry.getArena(player) == null) return;
        if (event.getClickedInventory() == player.getInventory()) event.setCancelled(true);
    }


    @EventHandler
    public void onBowShot(EntityShootBowEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }
        if (!Role.isRole(Role.ANY_DETECTIVE, (Player) e.getEntity())) {
            return;
        }
        User user = plugin.getUserManager().getUser((Player) e.getEntity());
        if (user.getCooldown("bow_shot") == 0) {
            user.setCooldown("bow_shot", plugin.getConfig().getInt("Detective-Bow-Cooldown", 5));
            Player player = (Player) e.getEntity();
            Utils.applyActionBarCooldown(player, plugin.getConfig().getInt("Detective-Bow-Cooldown", 5));
            e.getBow().setDurability((short) 0);
        } else {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onArrowPickup(PlayerPickupArrowEvent e) {
        if (ArenaRegistry.isInArena(e.getPlayer())) {
            Player p = e.getPlayer();
            FileConfiguration donatConfig = ConfigUtils.getConfig(plugin, "donaters");
            if (p.hasPermission(donatConfig.getString("Pickup-Arrows-Permission"))) {
                if (p.getInventory().contains(Material.BOW)) {
                    p.getInventory().addItem(new ItemStack(Material.ARROW));
                    p.sendMessage("§aВы подобрали стрелу");
                }
                e.getItem().remove();
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent e) {
        Arena arena = ArenaRegistry.getArena(e.getPlayer());
        if (arena == null) {
            return;
        }
        e.setCancelled(true);
        if (e.getItem().getItemStack().getType() != Material.GOLD_INGOT) {
            return;
        }
        User user = plugin.getUserManager().getUser(e.getPlayer());
        if (user.isSpectator() || arena.getArenaState() != ArenaState.IN_GAME) {
            return;
        }
        if (user.getStat(StatsStorage.StatisticType.LOCAL_CURRENT_PRAY) == /* magic number */ 3) {
            e.setCancelled(true);
            return;
        }
        e.getItem().remove();
        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_LAVA_POP, 1, 1);
        arena.getGoldSpawned().remove(e.getItem());
        ItemStack stack = new ItemStack(Material.GOLD_INGOT, e.getItem().getItemStack().getAmount());
        if (user.getStat(StatsStorage.StatisticType.LOCAL_CURRENT_PRAY) == /* magic number */ 4) {
            stack.setAmount(3 * e.getItem().getItemStack().getAmount());
        }
        if (Perk.has(user.getPlayer(), ExtremeGoldPerk.class)) {
            Perk.getPerkByClass(ExtremeGoldPerk.class).handle(user.getPlayer(), null, arena);
        }

        ItemPosition.addItem(e.getPlayer(), ItemPosition.GOLD_INGOTS, stack);
        user.addStat(StatsStorage.StatisticType.LOCAL_GOLD, e.getItem().getItemStack().getAmount());
        ArenaUtils.addScore(user, ArenaUtils.ScoreAction.GOLD_PICKUP, e.getItem().getItemStack().getAmount());
        e.getPlayer().sendMessage(ChatManager.colorMessage("In-Game.Messages.Picked-Up-Gold", e.getPlayer()));
        if (Role.isRole(Role.ANY_DETECTIVE, e.getPlayer())) {
            ItemPosition.addItem(e.getPlayer(), ItemPosition.ARROWS, new ItemStack(Material.ARROW, plugin.getConfig().getInt("Detective-Gold-Pick-Up-Arrows", 3)));
            return;
        }
        if (Role.isRole(Role.INNOCENT, e.getPlayer())) {
            if (user.getStat(StatsStorage.StatisticType.LOCAL_GOLD) >= 10) {
                user.setStat(StatsStorage.StatisticType.LOCAL_GOLD, user.getStat(StatsStorage.StatisticType.LOCAL_GOLD) - 10);
                e.getPlayer().sendTitle(ChatManager.colorMessage("In-Game.Messages.Bow-Messages.Bow-Shot-For-Gold", e.getPlayer()), ChatManager.colorMessage("In-Game.Messages.Bow-Messages.Bow-Shot-Subtitle", e.getPlayer()), 5, 40, 5);
                ItemPosition.setItem(e.getPlayer(), ItemPosition.BOW, new ItemStack(Material.BOW, 1));
                ItemPosition.addItem(e.getPlayer(), ItemPosition.ARROWS, new ItemStack(Material.ARROW, plugin.getConfig().getInt("Detective-Default-Arrows", 3)));
                e.getPlayer().getInventory().setItem(/* same for all roles */ ItemPosition.GOLD_INGOTS.getOtherRolesItemPosition(), new ItemStack(Material.GOLD_INGOT, -10));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMurdererDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) {
            return;
        }
        Player attacker = (Player) e.getDamager();
        Player victim = (Player) e.getEntity();
        if (!ArenaUtils.areInSameArena(attacker, victim)) {
            return;
        }
        //we are killing player via damage() method so event can be cancelled safely, will work for detective damage murderer and others

        e.setCancelled(true);

        if (attacker.getInventory().getItemInMainHand().getType() == Material.BLAZE_ROD) {
            User u = plugin.getUserManager().getUser(attacker);
            if (u.getShots() >= 3) {
                ItemPosition.setItem(attacker, ItemPosition.BLAZE_ROD, null);
            } else {
                victim.setVelocity((new Vector(victim.getLocation().getDirection().getX() * 5, 0, victim.getLocation().getDirection().getY() * 5)));
            }
            u.setShots(u.getShots() + 1);
        }
        if (attacker.getInventory().getItemInMainHand().isSimilar(PovodokEbaniyPerk.item)) {
            if (Perk.has(attacker, PovodokEbaniyPerk.class)) {
                Perk.getPerkByClass(PovodokEbaniyPerk.class).handle(attacker, victim, ArenaRegistry.getArena(attacker));
            }
        }


        //better check this for future even if anyone else cannot use sword
        if (!Role.isRole(Role.MURDERER, attacker)) {
            return;
        }

        //check if victim is murderer
        if (Role.isRole(Role.MURDERER, victim)) {
            return;
        }

        //just don't kill user if item isn't murderer sword
        if (attacker.getInventory().getItemInMainHand().getType() != plugin.getConfigPreferences().getMurdererSword().getType()) {
            return;
        }

        //check if sword has cooldown
        if (attacker.hasCooldown(plugin.getConfigPreferences().getMurdererSword().getType())) {
            return;
        }

        if (Role.isRole(Role.MURDERER, victim)) {
            plugin.getRewardsHandler().performReward(attacker, Reward.RewardType.MURDERER_KILL);
            plugin.getEconomy().depositPlayer(attacker, 100);
            attacker.sendMessage("§cВы получили §a100§c монет за убийство маньяка!");
        } else if (Role.isRole(Role.ANY_DETECTIVE, victim)) {
            plugin.getRewardsHandler().performReward(attacker, Reward.RewardType.DETECTIVE_KILL);
            plugin.getEconomy().depositPlayer(attacker, 75);
            attacker.sendMessage("§cВы получили §a75§c монет за убийство детектива!");
        }
        killPlayer(victim, ArenaRegistry.getArena(victim));
        User user = plugin.getUserManager().getUser(attacker);
        user.addStat(StatsStorage.StatisticType.KILLS, 1);
        user.addStat(StatsStorage.StatisticType.LOCAL_KILLS, 1);
        ArenaUtils.addScore(user, ArenaUtils.ScoreAction.KILL_PLAYER, 0);
        Arena arena = ArenaRegistry.getArena(attacker);
        if (Role.isRole(Role.ANY_DETECTIVE, victim) && arena.lastAliveDetective()) {
            //if already true, no effect is done :)
            arena.setDetectiveDead(true);
            if (Role.isRole(Role.FAKE_DETECTIVE, victim)) {
                arena.setCharacter(Arena.CharacterType.FAKE_DETECTIVE, null);
            }
            ArenaUtils.dropBowAndAnnounce(arena, victim);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onArrowDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Arrow && e.getEntity() instanceof Player)) {
            return;
        }
        if (!(((Arrow) e.getDamager()).getShooter() instanceof Player)) {
            return;
        }
        Player attacker = (Player) ((Arrow) e.getDamager()).getShooter();
        Player victim = (Player) e.getEntity();
        if (!ArenaUtils.areInSameArena(attacker, victim)) {
            return;
        }
        //we won't allow to suicide
        if (attacker.equals(victim)) {
            e.setCancelled(true);
            return;
        }
        //dont kill murderer on bow damage if attacker is murderer
        if (Role.isRole(Role.MURDERER, attacker) && Role.isRole(Role.MURDERER, victim)) {
            e.setCancelled(true);
            return;
        }
        Arena arena = ArenaRegistry.getArena(attacker);
        //we need to set it before the victim die, because of hero character
        if (Role.isRole(Role.MURDERER, victim)) {
            arena.setCharacter(Arena.CharacterType.HERO, attacker);
        }
        killPlayer(victim, arena);

        User user = plugin.getUserManager().getUser(attacker);

        user.addStat(StatsStorage.StatisticType.KILLS, 1);
        if (Role.isRole(Role.MURDERER, attacker)) {
            user.addStat(StatsStorage.StatisticType.LOCAL_KILLS, 1);
            ArenaUtils.addScore(user, ArenaUtils.ScoreAction.KILL_PLAYER, 0);
        }

        victim.sendTitle(ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Titles.Died", victim), null, 5, 40, 50);

        if (Role.isRole(Role.MURDERER, victim)) {
            ArenaUtils.addScore(plugin.getUserManager().getUser(attacker), ArenaUtils.ScoreAction.KILL_MURDERER, 0);
        } else if (Role.isRole(Role.INNOCENT, victim)) {
            if (Role.isRole(Role.MURDERER, attacker)) {
                victim.sendTitle(null, ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Subtitles.Murderer-Killed-You", victim), 5, 40, 5);
            } else {
                victim.sendTitle(null, ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Subtitles.Player-Killed-You", victim), 5, 40, 5);
            }

            //if else, murderer killed, so don't kill him :)
            if (Role.isRole(Role.ANY_DETECTIVE, attacker) || Role.isRole(Role.INNOCENT, attacker)) {
                attacker.sendTitle(ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Titles.Died", attacker),
                        ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Subtitles.Killed-Innocent", attacker), 5, 40, 5);
                killPlayer(attacker, arena);
                ArenaUtils.addScore(plugin.getUserManager().getUser(attacker), ArenaUtils.ScoreAction.INNOCENT_KILL, 0);
                plugin.getRewardsHandler().performReward(attacker, Reward.RewardType.DETECTIVE_KILL);
                if (Role.isRole(Role.ANY_DETECTIVE, attacker) && arena.lastAliveDetective()) {
                    arena.setDetectiveDead(true);
                    if (Role.isRole(Role.FAKE_DETECTIVE, attacker)) {
                        arena.setCharacter(Arena.CharacterType.FAKE_DETECTIVE, null);
                    }
                    ArenaUtils.dropBowAndAnnounce(arena, victim);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDie(PlayerDeathEvent e) {
        Arena arena = ArenaRegistry.getArena(e.getEntity());
        if (arena == null) {
            return;
        }
        e.setDeathMessage("");
        e.getEntity().setCollidable(false);
        e.getDrops().clear();
        e.setDroppedExp(0);
        plugin.getCorpseHandler().spawnCorpse(e.getEntity(), arena);
        e.getEntity().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 3 * 20, 0));
        Player player = e.getEntity();
        if (arena.getArenaState() == ArenaState.STARTING) {
            return;
        } else if (arena.getArenaState() == ArenaState.ENDING || arena.getArenaState() == ArenaState.RESTARTING) {
            player.getInventory().clear();
            player.setFlying(false);
            player.setAllowFlight(false);
            User user = plugin.getUserManager().getUser(player);
            user.setStat(StatsStorage.StatisticType.LOCAL_GOLD, 0);
            return;
        }
        if (Role.isRole(Role.MURDERER, player) && arena.lastAliveMurderer()) {
            ArenaUtils.onMurdererDeath(arena);
        }
        if (Role.isRole(Role.ANY_DETECTIVE, player) && arena.lastAliveDetective()) {
            arena.setDetectiveDead(true);
            if (Role.isRole(Role.FAKE_DETECTIVE, player)) {
                arena.setCharacter(Arena.CharacterType.FAKE_DETECTIVE, null);
            }
            ArenaUtils.dropBowAndAnnounce(arena, player);
        }
        User user = plugin.getUserManager().getUser(player);
        user.addStat(StatsStorage.StatisticType.DEATHS, 1);
        user.setSpectator(true);
        player.setGameMode(GameMode.ADVENTURE);
        user.setStat(StatsStorage.StatisticType.LOCAL_GOLD, 0);
        ArenaUtils.hidePlayer(player, arena);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.getInventory().clear();
        ChatManager.broadcastAction(arena, player, ChatManager.ActionType.DEATH);
        //we must call it ticks later due to instant respawn bug

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            e.getEntity().spigot().respawn();
            player.teleport(arena.getPlayerSpawnPoints().get(0));
            player.getInventory().setItem(0, new ItemBuilder(XMaterial.COMPASS.parseItem()).name(ChatManager.colorMessage("In-Game.Spectator.Spectator-Item-Name", player)).build());
            player.getInventory().setItem(4, new ItemBuilder(XMaterial.COMPARATOR.parseItem()).name(ChatManager.colorMessage("In-Game.Spectator.Settings-Menu.Item-Name", player)).build());
            player.getInventory().setItem(8, SpecialItemManager.getSpecialItem("Leave").getItemStack());
        }, 3);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        Arena arena = ArenaRegistry.getArena(player);
        if (arena == null) {
            return;
        }
        if (arena.getArenaState() == ArenaState.STARTING || arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS) {
            e.setRespawnLocation(arena.getLobbyLocation());
            return;
        } else if (arena.getArenaState() == ArenaState.ENDING || arena.getArenaState() == ArenaState.RESTARTING) {
            e.setRespawnLocation(arena.getEndLocation());
            return;
        }
        if (arena.getPlayers().contains(player)) {
            User user = plugin.getUserManager().getUser(player);
            if (player.getLocation().getWorld() == arena.getPlayerSpawnPoints().get(0).getWorld()) {
                e.setRespawnLocation(player.getLocation());
            } else {
                e.setRespawnLocation(arena.getPlayerSpawnPoints().get(0));
            }
            user.setSpectator(true);
            ArenaUtils.hidePlayer(player, arena);
            player.setCollidable(false);
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(true);
            player.setFlying(true);
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            user.setStat(StatsStorage.StatisticType.LOCAL_GOLD, 0);
        }
    }

    @EventHandler
    public void playerCommandExecution(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        if (e.getMessage().equalsIgnoreCase("/start")) {
            player.performCommand("mma forcestart");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPotion(PlayerItemConsumeEvent e) {
        User u = plugin.getUserManager().getUser(e.getPlayer());
        if (u.getPotion() != null && e.getItem().equals(u.getPotion()))  {
            e.setCancelled(true);
            PotionMeta pm = (PotionMeta) u.getPotion().getItemMeta();
            PotionEffect pe = pm.getCustomEffects().get(0);
            e.getPlayer().addPotionEffect(pe);
            ItemPosition.setItem(e.getPlayer(), ItemPosition.POTION, null);
            u.setPotion(null);
            u.setPickedPotion(false);

        }
    }

    @EventHandler
    public void locatorDistanceUpdate(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Arena arena = ArenaRegistry.getArena(player);
        if (arena == null) {
            return;
        }
        val user = plugin.getUserManager().getUser(player);
        if (user.isSpectator()) return;
        if (arena.getArenaState() == ArenaState.IN_GAME) {
            if (Role.isRole(Role.INNOCENT, player)) {
                if (player.getInventory().getItem(ItemPosition.BOW_LOCATOR.getOtherRolesItemPosition()) != null) {
                    ItemStack bowLocator = new ItemStack(Material.COMPASS, 1);
                    ItemMeta bowMeta = bowLocator.getItemMeta();
                    bowMeta.setDisplayName(ChatManager.colorMessage("In-Game.Bow-Locator-Item-Name", player) + " §7| §a" + (int) Math.round(player.getLocation().distance(player.getCompassTarget())));
                    bowLocator.setItemMeta(bowMeta);
                    ItemPosition.setItem(player, ItemPosition.BOW_LOCATOR, bowLocator);
                    return;
                }
            }
            if (arena.isMurdererLocatorReceived() && Role.isRole(Role.MURDERER, player) && arena.isMurderAlive(player)) {
                ItemStack innocentLocator = new ItemStack(Material.COMPASS, 1);
                ItemMeta innocentMeta = innocentLocator.getItemMeta();
                for (Player p : arena.getPlayersLeft()) {
                    if (Role.isRole(Role.INNOCENT, p) || Role.isRole(Role.ANY_DETECTIVE, p)) {
                        innocentMeta.setDisplayName(ChatManager.colorMessage("In-Game.Innocent-Locator-Item-Name", player) + " §7| §a" + (int) Math.round(player.getLocation().distance(p.getLocation())));
                        innocentLocator.setItemMeta(innocentMeta);
                        ItemPosition.setItem(player, ItemPosition.INNOCENTS_LOCATOR, innocentLocator);
                    }
                }
            }
        }
    }
}
