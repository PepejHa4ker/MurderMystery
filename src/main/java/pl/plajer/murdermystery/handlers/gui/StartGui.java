package pl.plajer.murdermystery.handlers.gui;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import pl.plajer.murdermystery.Main;
import pl.plajer.murdermystery.api.StatsStorage;
import pl.plajer.murdermystery.arena.Arena;
import pl.plajer.murdermystery.arena.ArenaRegistry;
import pl.plajer.murdermystery.arena.ArenaState;
import pl.plajer.murdermystery.utils.items.ItemBuilder;
import pl.plajer.murdermystery.utils.items.ItemPosition;

public class StartGui implements GuiComponent {

    private final Player player;
    private static final Main plugin = JavaPlugin.getPlugin(Main.class);
    private Gui gui;

    /**
     * @author pepej
     * Created 15.06.2020
     */

    public StartGui(Player player) {
        this.player = player;
        prepareGui();
    }

    @Override
    public void injectComponents(StaticPane pane) {
        pane.setOnClick(e -> e.setCancelled(true));
        pane.addItem(new GuiItem(new ItemBuilder(Material.IRON_INGOT)
                .name("§eОбычное ускорение")
                .lore("§6Доступно от §aVIP",
                        "§6Позволяет сократить время запуска арены до 5 секунд и дополнительно получить 1 золото в игре.",
                        "§3(Работает только для Вас)")
                .colorizeItem()
                .build(), e -> {
            val arena = ArenaRegistry.getArena(player);
            if (arena == null) {
                e.getWhoClicked().closeInventory();
                return;
            }
            startArena(player, arena, 5);
            e.getWhoClicked().closeInventory();
            new BukkitRunnable() {
                int timer = 200;
                @Override
                public void run() {
                    timer--;
                    if(timer == 20) {
                        plugin.getUserManager().getUser(player).addStat(StatsStorage.StatisticType.LOCAL_GOLD, 1);
                        ItemPosition.addItem(player, ItemPosition.GOLD_INGOTS, new ItemStack(Material.GOLD_INGOT, 1));
                    }
                    if(timer == 0 || arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS) this.cancel();
                }
            }.runTaskTimer(plugin, 0,1);
        }), 0, 0);
        if(player.hasPermission("murder.pstart")) {
            pane.addItem(new GuiItem(new ItemBuilder(Material.EMERALD)
                    .name("§eМоментальное ускорение")
                    .lore("§6Доступно от §bPREMIUM",
                            "§6Позволяет моментально запустить игру и дополнительно получить 3 золота в игре",
                            "§3(Работает только для Вас)")
                    .colorizeItem()
                    .build(), e -> {
                val arena = ArenaRegistry.getArena(player);
                if (arena == null) {
                    e.getWhoClicked().closeInventory();
                    return;
                }
                startArena(player, arena, 0);
                e.getWhoClicked().closeInventory();
                new BukkitRunnable() {
                    int timer = 100;
                    @Override
                    public void run() {
                        timer--;
                        if(timer == 20) {
                            plugin.getUserManager().getUser(player).addStat(StatsStorage.StatisticType.LOCAL_GOLD, 3);
                            ItemPosition.addItem(player, ItemPosition.GOLD_INGOTS, new ItemStack(Material.GOLD_INGOT, 3));
                        }
                        if(timer == 0 || arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS) this.cancel();
                    }
                }.runTaskTimer(plugin, 0,1);
            }), 4, 0);
        }
        if(player.hasPermission("murder.gstart")) {
            pane.addItem(new GuiItem(new ItemBuilder(Material.DIAMOND)
                    .name("§eСУПЕР УСКОРЕНИЕ")
                    .lore("§6Доступно §eGRAND",
                            "§6Позволяет запустить игру моментально и дополнительно получить 5 золота в игре",
                            "§3(Работает только для Вас)")
                    .colorizeItem()
                    .build(), e -> {
                val arena = ArenaRegistry.getArena(player);
                if (arena == null) {
                    e.getWhoClicked().closeInventory();
                    return;
                }
                startArena(player, arena, 0);
                e.getWhoClicked().closeInventory();
                new BukkitRunnable() {
                    int timer = 100;
                    @Override
                    public void run() {
                        timer--;
                        if(timer == 20) {
                            plugin.getUserManager().getUser(player).addStat(StatsStorage.StatisticType.LOCAL_GOLD, 5);
                            ItemPosition.addItem(player, ItemPosition.GOLD_INGOTS, new ItemStack(Material.GOLD_INGOT, 5));
                        }
                        if(timer == 0 || arena.getArenaState() == ArenaState.WAITING_FOR_PLAYERS) this.cancel();
                    }
                }.runTaskTimer(plugin, 0,1);
            }), 8, 0);
        }
    }

    private void prepareGui() {
        this.gui = new Gui(plugin, 1, "Запуск игры");
        StaticPane pane = new StaticPane(9, 1);
        this.gui.addPane(pane);
        injectComponents(pane);
    }

    private void startArena(Player player, Arena arena, int timer) {
        if (arena.getPlayers().size() < arena.getMinimumPlayers()) {
            player.sendMessage("§cНедостаточно игроков для начала! Требуется §6" + arena.getMinimumPlayers() + "§c игроков для начала игры");
            return;
        }
        if(!arena.isCanForceStart()) {
            player.sendMessage("§6Арена уже начинается!");
            player.closeInventory();
            return;
        }
        arena.setTimer(timer);
        arena.setCanForceStart(false);
        for (Player p : ArenaRegistry.getArena(player).getPlayers()) {
            p.sendMessage("§eИгрок §b" + player.getName() + " §eускорил запуск игры!");
        }
    }

    @Override
    public void show(Player player) {
        gui.show(player);
    }
}
