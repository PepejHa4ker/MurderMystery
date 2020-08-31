package pl.plajer.murdermystery.utils.items;

import lombok.val;
import lombok.var;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ItemBuilder {

    private final ItemStack itemStack;

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilder type(Material material) {
        this.itemStack.setType(material);
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder color(int color) {
        this.itemStack.setDurability((short) color);
        return this;
    }

    public ItemBuilder name(String name) {
        val meta = this.itemStack.getItemMeta();
        meta.setDisplayName(name.replace('&', '§'));
        this.itemStack.setItemMeta(meta);
        return this;
    }


    public ItemBuilder enchantment(Enchantment enchantment) {
        this.itemStack.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        this.itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder lore(String... name) {
        return this.lore(Arrays.asList(name));
    }

    public ItemBuilder lore(List<String> name) {
        val meta = this.itemStack.getItemMeta();
        var lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        name = name.stream()
                   .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                   .collect(toList());

        lore.addAll(name);
        meta.setLore(lore);
        this.itemStack.setItemMeta(meta);
        return this;
    }

    public ItemStack build() {
        return this.itemStack;
    }
}

