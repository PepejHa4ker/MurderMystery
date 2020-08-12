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

package pl.plajer.murdermystery.events;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import pl.plajer.murdermystery.ConfigPreferences;
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.api.StatsStorage;
import pl.plajer.murdermystery.api.events.player.MMPlayerStatisticChangeEvent;
import pl.plajer.murdermystery.arena.*;
import pl.plajer.murdermystery.arena.role.Role;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.handlers.gui.PerkGui;
import pl.plajer.murdermystery.handlers.gui.PotionGui;
import pl.plajer.murdermystery.handlers.gui.StartGui;
import pl.plajer.murdermystery.handlers.items.SpecialItemManager;
import pl.plajer.murdermystery.user.User;
import pl.plajer.murdermystery.utils.compat.XMaterial;
import pl.plajer.murdermystery.utils.number.Maths;
import pl.plajer.murdermystery.utils.Utils;
import pl.plajer.murdermystery.utils.message.type.TitleMessage;


/**
 * @author Plajer
 * <p>
 * Created at 05.08.2018
 */
@FieldDefaults(makeFinal=true, level= AccessLevel.PRIVATE)

public class Events implements Listener {

    MurderMystery plugin;

    public Events(MurderMystery plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onItemSwap(PlayerSwapHandItemsEvent e) {
        if (ArenaRegistry.isInArena(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Arena arena = ArenaRegistry.getArena(event.getPlayer());
        if (arena == null) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onArenaInteract(PlayerInteractEvent e) {
        if (ArenaRegistry.getArena(e.getPlayer()) == null) return;
        Arena arena = ArenaRegistry.getArena(e.getPlayer());
        User user = plugin.getUserManager().getUser(e.getPlayer());
        if (arena.getArenaState() != ArenaState.IN_GAME) {
            e.setCancelled(true);
            return;
        }
        if (user.isSpectator()) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSwordThrow(PlayerInteractEvent e) {
        Arena arena = ArenaRegistry.getArena(e.getPlayer());
        if (arena == null) {
            return;
        }
        if (!Role.isRole(Role.MURDERER, e.getPlayer())) {
            return;
        }
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.PHYSICAL) {
            return;
        }
        Player attacker = e.getPlayer();
        User attackerUser = plugin.getUserManager().getUser(attacker);
        if (attacker.getInventory().getItemInMainHand().getType() != plugin.getConfigPreferences().getMurdererSword().getType()) {
            return;
        }
        if (attackerUser.getCooldown("sword_shoot") > 0) {
            return;
        }
        attackerUser.setCooldown("sword_shoot", plugin.getConfig().getInt("Murderer-Sword-Fly-Cooldown", 5));
        attacker.setCooldown(plugin.getConfigPreferences().getMurdererSword().getType(), 20 * (plugin.getConfig().getInt("Murderer-Sword-Attack-Cooldown", 1)));
        createFlyingSword(arena, attacker, attackerUser);
        Utils.applyActionBarCooldown(attacker, plugin.getConfig().getInt("Murderer-Sword-Fly-Cooldown", 5));
    }

    private void createFlyingSword(Arena arena, Player attacker, User attackerUser) {
        Location loc = attacker.getLocation();
        Vector vec = attacker.getLocation().getDirection();
        vec.normalize().multiply(0.80);
        Location standStart = Maths.rotateAroundAxisY(new Vector(.75D, 0.0D, 0.0D), loc.getYaw()).toLocation(attacker.getWorld()).add(loc);
        standStart.setYaw(loc.getYaw());
        ArmorStand stand = (ArmorStand) attacker.getWorld().spawnEntity(standStart, EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setInvulnerable(true);
        stand.setItemInHand(plugin.getConfigPreferences().getMurdererSword());
        stand.setRightArmPose(new EulerAngle(Math.toRadians(350.0), Math.toRadians(attacker.getLocation().getPitch() * -1.0), Math.toRadians(90.0)));
        stand.setCollidable(false);
        stand.setSilent(true);
        stand.setGravity(false);
        stand.setRemoveWhenFarAway(true);
        stand.setMarker(true);
        Location initialise = Maths.rotateAroundAxisY(new Vector(-0.8D, 1.45D, 0.0D),
                loc.getYaw())
                .toLocation(attacker.getWorld())
                .add(standStart)
                .add(Maths.rotateAroundAxisY(
                        Maths.rotateAroundAxisX(
                                new Vector(0.0D,
                                        0.0D,
                                        1.0D),
                                loc.getPitch()),
                        loc.getYaw()));
        int maxRange = 50;
        double maxHitRange = 0.6;
        new BukkitRunnable() {
            @Override
            public void run() {
                stand.teleport(standStart.add(vec));
                initialise.add(vec);
                initialise.getWorld().getNearbyEntities(initialise, maxHitRange, maxHitRange, maxHitRange).forEach(entity -> {
                    if (entity instanceof Player) {
                        Player victim = (Player) entity;
                        if (ArenaRegistry.isInArena(victim) && !plugin.getUserManager().getUser(victim).isSpectator()) {
                            if (!victim.equals(attacker)) {
                                killBySword(arena, attackerUser, victim);
                            }
                        }
                    }
                });
                if (loc.distance(initialise) > maxRange || initialise.getBlock().getType().isSolid()) {
                    this.cancel();
                    stand.remove();
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        new TitleMessage("§cДобро пожаловать!", 10, 40, 10).send(e.getPlayer());
        plugin.getUserManager().getUser(e.getPlayer()).loadRank();
    }

    @EventHandler
    public void onStatChange(MMPlayerStatisticChangeEvent e) {
        if (e.getStatisticType() != StatsStorage.StatisticType.HIGHEST_SCORE) return;
        plugin.getUserManager().getUser(e.getPlayer()).loadRank();
    }
    private void killPlayer(Player player, Arena arena) {
        player.damage(1000);
        player.teleport(arena.getPlayerSpawnPoints().get(0));
        for (Player p : arena.getPlayers()) {
            p.playSound(p.getLocation(), Sound.ENTITY_WITCH_HURT, 1f, 1f);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_DEATH, 1f, 1f);
        }
    }

    private void killBySword(Arena arena, User attackerUser, Player victim) {
        //check if victim is murderer
        if (Role.isRole(Role.MURDERER, victim)) {
            return;
        }
        victim.playSound(victim.getLocation(), Sound.ENTITY_ENDERDRAGON_SHOOT, 1f, 1f);
        attackerUser.getPlayer().playSound(attackerUser.getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
        killPlayer(victim, arena);
        new BukkitRunnable() {
            int timer = 0;

            @Override
            public void run() {
                timer++;
                attackerUser.getPlayer().playSound(attackerUser.getPlayer().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
                if (timer == 5) this.cancel();
            }
        }.runTaskTimer(plugin, 0, 1);

        victim.sendTitle(ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Titles.Died", victim),
                ChatManager.colorMessage("In-Game.Messages.Game-End-Messages.Subtitles.Murderer-Killed-You", victim), 5, 40, 5);
        attackerUser.addStat(StatsStorage.StatisticType.LOCAL_KILLS, 1);
        attackerUser.addStat(StatsStorage.StatisticType.KILLS, 1);
        ArenaUtils.addScore(attackerUser, ArenaUtils.ScoreAction.KILL_PLAYER, 0);
        if (Role.isRole(Role.ANY_DETECTIVE, victim) && arena.lastAliveDetective()) {
            plugin.getEconomy().depositPlayer(attackerUser.getPlayer(), 75);
            attackerUser.sendMessage("&6Вы получили &a75 &6монет за убийство детектива");
            if (Role.isRole(Role.FAKE_DETECTIVE, victim)) {
                arena.setCharacter(Arena.CharacterType.FAKE_DETECTIVE, null);
            }
            ArenaUtils.dropBowAndAnnounce(arena, victim);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandExecute(PlayerCommandPreprocessEvent event) {
        Arena arena = ArenaRegistry.getArena(event.getPlayer());
        if (arena == null) {
            return;
        }
        if (!plugin.getConfig().getBoolean("Block-Commands-In-Game", true)) {
            return;
        }
        for (String msg : plugin.getConfig().getStringList("Whitelisted-Commands")) {
            if (event.getMessage().contains(msg)) {
                return;
            }
        }
        if (event.getPlayer().isOp() || event.getPlayer().hasPermission("murdermystery.admin") || event.getPlayer().hasPermission("murdermystery.command.bypass")) {
            return;
        }
        if (event.getMessage().startsWith("/mm") || event.getMessage().startsWith("/murdermystery")
                || event.getMessage().startsWith("/murdermysteryadmin") || event.getMessage().contains("leave")
                || event.getMessage().contains("stats") || event.getMessage().startsWith("/mma")) {
            return;
        }
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatManager.PLUGIN_PREFIX + ChatManager.colorMessage("In-Game.Only-Command-Ingame-Is-Leave"));
    }

    @EventHandler
    public void onInGameInteract(PlayerInteractEvent event) {
        Arena arena = ArenaRegistry.getArena(event.getPlayer());
        if (arena == null || event.getClickedBlock() == null) {
            return;
        }
        if (event.getClickedBlock().getType() == XMaterial.PAINTING.parseMaterial() || event.getClickedBlock().getType() == XMaterial.FLOWER_POT.parseMaterial()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInGameBedEnter(PlayerBedEnterEvent event) {
        Arena arena = ArenaRegistry.getArena(event.getPlayer());
        if (arena == null) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler
    public void onLeave(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.PHYSICAL) {
            return;
        }
        Arena arena = ArenaRegistry.getArena(event.getPlayer());
        ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        if (arena == null || !Utils.isNamed(itemStack)) {
            return;
        }
        String key = SpecialItemManager.getRelatedSpecialItem(itemStack);
        if (key == null) {
            return;
        }
        if (SpecialItemManager.getRelatedSpecialItem(itemStack).equalsIgnoreCase("Leave")) {
            event.setCancelled(true);
            if (plugin.getConfigPreferences().getOption(ConfigPreferences.Option.BUNGEE_ENABLED)) {
                plugin.getBungeeManager().connectToHub(event.getPlayer());
            } else {
                ArenaManager.leaveAttempt(event.getPlayer(), arena);
            }
        }
    }

    @EventHandler
    public void onStart(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.PHYSICAL) {
            return;
        }
        Arena arena = ArenaRegistry.getArena(e.getPlayer());
        ItemStack itemStack = e.getPlayer().getInventory().getItemInMainHand();
        if (arena == null || !Utils.isNamed(itemStack)) {
            return;
        }
        String key = SpecialItemManager.getRelatedSpecialItem(itemStack);
        if (key == null) {
            return;
        }

        if (SpecialItemManager.getRelatedSpecialItem(itemStack).equalsIgnoreCase("Start")) {
            e.setCancelled(true);
            if (arena.getArenaState() == ArenaState.STARTING) {
                new StartGui(e.getPlayer()).show(e.getPlayer());
            } else e.getPlayer().sendMessage("§6Арена должна начаться, чтобы её ускорить");

        }
    }

    @EventHandler
    public void onPerk(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.PHYSICAL) {
            return;
        }
        Arena arena = ArenaRegistry.getArena(e.getPlayer());
        ItemStack itemStack = e.getPlayer().getInventory().getItemInMainHand();
        if (arena == null || !Utils.isNamed(itemStack)) {
            return;
        }
        String key = SpecialItemManager.getRelatedSpecialItem(itemStack);
        if (key == null) {
            return;
        }
        if (SpecialItemManager.getRelatedSpecialItem(itemStack).equalsIgnoreCase("Perks")) {
            e.setCancelled(true);
            Player p = e.getPlayer();
            if (arena.getArenaState() != ArenaState.STARTING && arena.getArenaState() != ArenaState.WAITING_FOR_PLAYERS) {
                p.sendMessage("§6Опоздал :(");
                return;
            }
            new PerkGui(p).show(p);
        }
    }


    @EventHandler
    public void onMenu(PlayerInteractEvent e) {
        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.PHYSICAL) {
            return;
        }
        Arena arena = ArenaRegistry.getArena(e.getPlayer());
        ItemStack itemStack = e.getPlayer().getInventory().getItemInMainHand();
        if (arena == null || !Utils.isNamed(itemStack)) {
            return;
        }
        String key = SpecialItemManager.getRelatedSpecialItem(itemStack);
        if (key == null) {
            return;
        }
        if (SpecialItemManager.getRelatedSpecialItem(itemStack).equalsIgnoreCase("Menu")) {
            e.setCancelled(true);
            Player p = e.getPlayer();
            if (arena.getArenaState() != ArenaState.STARTING && arena.getArenaState() != ArenaState.WAITING_FOR_PLAYERS) {
                p.sendMessage("§6Опоздал :(");
                return;
            }
            new PotionGui(p).show(p);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER && ArenaRegistry.isInArena((Player) event.getEntity())) {
            event.setFoodLevel(20);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    //highest priority to fully protect our game (i didn't set it because my test server was destroyed, n-no......)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (!ArenaRegistry.isInArena(event.getPlayer())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    //highest priority to fully protect our game (i didn't set it because my test server was destroyed, n-no......)
    public void onBuild(BlockPlaceEvent event) {
        if (!ArenaRegistry.isInArena(event.getPlayer())) {
            return;
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    //highest priority to fully protect our game (i didn't set it because my test server was destroyed, n-no......)
    public void onHangingBreakEvent(HangingBreakByEntityEvent event) {
        if (event.getEntity() instanceof ItemFrame || event.getEntity() instanceof Painting) {
            if (event.getRemover() instanceof Player && ArenaRegistry.isInArena((Player) event.getRemover())) {
                event.setCancelled(true);
                return;
            }
            if (!(event.getRemover() instanceof Arrow)) {
                return;
            }
            Arrow arrow = (Arrow) event.getRemover();
            if (arrow.getShooter() instanceof Player && ArenaRegistry.isInArena((Player) arrow.getShooter())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onArmorStandDestroy(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof LivingEntity)) {
            return;
        }
        final LivingEntity livingEntity = (LivingEntity) e.getEntity();
        if (!livingEntity.getType().equals(EntityType.ARMOR_STAND)) {
            return;
        }
        if (e.getDamager() instanceof Player && ArenaRegistry.isInArena((Player) e.getDamager())) {
            e.setCancelled(true);
        } else if (e.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getDamager();
            if (arrow.getShooter() instanceof Player && ArenaRegistry.isInArena((Player) arrow.getShooter())) {
                e.setCancelled(true);
                return;
            }
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteractWithArmorStand(PlayerArmorStandManipulateEvent event) {
        if (ArenaRegistry.isInArena(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCraft(PlayerInteractEvent event) {
        if (!ArenaRegistry.isInArena(event.getPlayer())) {
            return;
        }
        if (event.getPlayer().getTargetBlock(null, 7).getType() == XMaterial.CRAFTING_TABLE.parseMaterial()) {
            event.setCancelled(true);
        }
    }

}
