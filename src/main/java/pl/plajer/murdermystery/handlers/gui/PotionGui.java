package pl.plajer.murdermystery.handlers.gui;


import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.plajer.murdermystery.Main;
import pl.plajer.murdermystery.api.StatsStorage;
import pl.plajer.murdermystery.user.User;
import pl.plajerlair.commonsbox.minecraft.item.ItemBuilder;

import java.util.*;

public class PotionGui implements GuiComponent {

    /**
     * @author pepej
     * Created 15.06.2020
     */

    private Player player;
    private static Main plugin = JavaPlugin.getPlugin(Main.class);
    private Gui gui;

    public PotionGui(Player player) {
        this.player = player;
        prepareGui();
    }

    @Override
    public void injectComponents(StaticPane pane) {
        List<String> lore = new ArrayList<>();
        lore.add("§6Хочешь узнать его тайну?");
        int price = player.hasPermission("murder.potion") ? 0 : 300;
        lore.add("§cЦена: §6" + price + " §cмонет или §650 §dкармы");
        lore.add("§eЛКМ - Монеты");
        lore.add("§eПКМ - Карма");
        User user = plugin.getUserManager().getUser(player);
        pane.setOnClick(e -> e.setCancelled(true));
        pane.fillWith(new ItemBuilder(Material.STAINED_GLASS_PANE).name("§6").build());
        pane.addItem(new GuiItem(new ItemBuilder(Material.POTION).name("§dМистическое зелье..").lore(lore).build(), e -> {
            e.getWhoClicked().closeInventory();
            if (!user.isPickedPotion()) {
                switch (e.getClick()) {
                    case LEFT:
                        if (plugin.getEconomy().getBalance(player) >= price) {
                            processPotion(user, PriceType.COINS, price);
                        } else player.sendMessage("§6На вашем счету недостаточно средств. Вы можете взять доверительный платёж нажав 1");
                        return;
                    case RIGHT:
                        if (user.getStat(StatsStorage.StatisticType.KARMA) >= 50) {
                            processPotion(user, PriceType.KARMA, 50);
                        } else player.sendMessage("§6Недостаточно кармы.");
                }
            } else player.sendMessage("§6Вы уже выбрали зелье");
        }), 4, 0);
        pane.addItem(new GuiItem(new ItemBuilder(Material.EMPTY_MAP)
                .name("§aБаланс:")
                .lore("§6Монет: " + plugin.getEconomy().getBalance(player),
                        "§dКармы: " + user.getStat(StatsStorage.StatisticType.KARMA))
                .build()), 8, 0);
    }

    private void processPotion(User user, PriceType type, int price) {
        switch (type) {
            case COINS:
                plugin.getEconomy().withdrawPlayer(player, price);
                break;
            case KARMA:
                user.setStat(StatsStorage.StatisticType.KARMA, user.getStat(StatsStorage.StatisticType.KARMA) -price);
                break;
        }
        user.setPickedPotion(true);
        player.sendMessage("§6Вы успешно выбрали зелье");
        user.setPotion(getRandomPotions());
    }


    private void prepareGui() {
        this.gui = new Gui(plugin, 1, "Меню");
        this.gui.setOnGlobalClick(e -> e.setCancelled(true));
        StaticPane pane = new StaticPane(9, 1);
        this.gui.addPane(pane);
        injectComponents(pane);
    }

    @Override
    public void show(Player player) {
        gui.show(player);
    }

    private ItemStack getRandomPotions() {
        List<ItemStack> potions = new ArrayList<>();
        val potionInvis = new ItemBuilder(Material.POTION).name("§dМистическое зелье...").lore("§6Выпей, чтобы узнать его тайну");
        val potionJump = new ItemBuilder(Material.POTION).name("§dМистическое зелье...").lore("§6Выпей, чтобы узнать его тайну");
        val potionSpeed = new ItemBuilder(Material.POTION).name("§dМистическое зелье...").lore("§6Выпей, чтобы узнать его тайну");
        val invisMeta = (PotionMeta) potionInvis.build().getItemMeta();
        val jumpMeta = (PotionMeta) potionJump.build().getItemMeta();
        val speedMeta = (PotionMeta) potionSpeed.build().getItemMeta();
        invisMeta.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 300, 0), true);
        invisMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        speedMeta.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 300, 0), true);
        speedMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        jumpMeta.addCustomEffect(new PotionEffect(PotionEffectType.JUMP, 300, 1), true);
        jumpMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        potionInvis.build().setItemMeta(invisMeta);
        potionSpeed.build().setItemMeta(speedMeta);
        potionJump.build().setItemMeta(jumpMeta);
        potions.add(potionInvis.build());
        potions.add(potionJump.build());
        potions.add(potionSpeed.build());
        return potions.get(new Random().nextInt(potions.size()));
    }

    private enum PriceType {
        KARMA, COINS
    }
}
