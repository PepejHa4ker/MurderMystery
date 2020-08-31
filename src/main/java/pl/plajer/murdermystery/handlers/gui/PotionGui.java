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
import pl.plajer.murdermystery.MurderMystery;
import pl.plajer.murdermystery.api.StatsStorage;
import pl.plajer.murdermystery.economy.PriceType;
import pl.plajer.murdermystery.handlers.ChatManager;
import pl.plajer.murdermystery.user.User;
import pl.plajer.murdermystery.utils.items.ItemBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PotionGui implements GuiComponent {

    /**
     * @author pepej
     * Created 15.06.2020
     */

    private final Player player;
    private static final MurderMystery plugin = JavaPlugin.getPlugin(MurderMystery.class);
    private Gui gui;

    public PotionGui(Player player) {
        this.player = player;
        prepareGui();
    }

    @Override
    public void injectComponents(StaticPane pane) {
        User user = plugin.getUserManager().getUser(player);
        pane.setOnClick(e -> e.setCancelled(true));
        pane.fillWith(new ItemBuilder(Material.STAINED_GLASS_PANE).name("&6").build());
        int cost = player.hasPermission("murder.potion") ? 0 : 300;
        pane.addItem(new GuiItem(new ItemBuilder(Material.POTION)
                .name("§dМистическое зелье..")
                .lore("&6Хочешь узнать его тайну?")
                .lore("&cЦена: &6" + cost + "&a монет &cили &650 &dкармы")
                .lore("&eЛКМ - &aмонеты")
                .lore("&eПКМ - &dкарма")
                .build(), e -> {
            e.getWhoClicked().closeInventory();
            if (!user.isPickedPotion()) {
                switch (e.getClick()) {
                    case LEFT:
                        if (plugin.getEconomy().getBalance(player) >= cost) {
                            processPotion(user, PriceType.COINS, cost);
                        } else
                            ChatManager.sendMessage(player, "&6На вашем счету недостаточно средств. Вы можете взять доверительный платёж нажав 1");
                        return;
                    case RIGHT:
                        if (user.getStat(StatsStorage.StatisticType.KARMA) >= 50) {
                            processPotion(user, PriceType.KARMA, 50);
                        } else ChatManager.sendMessage(player, "&6Недостаточно кармы(Попробуйте сделать доброе дело).");
                }
            } else ChatManager.sendMessage(player, "&6Вы уже выбрали зелье");
        }), 4, 0);
        pane.addItem(new GuiItem(new ItemBuilder(Material.EMPTY_MAP)
                .name("&aБаланс:")
                .lore("&6Монет: " + plugin.getEconomy().getBalance(player))
                .lore("&dКармы: " + user.getStat(StatsStorage.StatisticType.KARMA))
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
        ChatManager.sendMessage(player, "&6Вы успешно выбрали зелье");
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
        val potionInvis = new ItemBuilder(Material.POTION)
                .name("&dМистическое зелье...")
                .lore("&6Выпей, чтобы узнать его тайну");
        val potionJump = new ItemBuilder(Material.POTION)
                .name("&dМистическое зелье...")
                .lore("&6Выпей, чтобы узнать его тайну");
        val potionSpeed = new ItemBuilder(Material.POTION)
                .name("&dМистическое зелье...")
                .lore("&6Выпей, чтобы узнать его тайну");
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
}
