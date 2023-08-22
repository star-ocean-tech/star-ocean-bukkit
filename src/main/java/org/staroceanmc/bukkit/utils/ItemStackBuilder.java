package org.staroceanmc.bukkit.utils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ItemStackBuilder {
    private Material material;
    private int amount = 1;
    private Component displayName;
    private List<Component> lore;
    private Map<Enchantment, Integer> enchantments;
    private List<ItemFlag> itemFlags;
    private Multimap<Attribute, AttributeModifier> attributeModifiers;

    public ItemStackBuilder() {

    }

    public ItemStackBuilder material(@NotNull Material material) {
        this.material = material;

        return this;
    }

    public ItemStackBuilder amount(int amount) {
        if (amount <= 0) {
            throw new IndexOutOfBoundsException(amount);
        }

        return this;
    }

    public ItemStackBuilder displayName(Component displayName) {
        this.displayName = displayName;
        return this;
    }

    public ItemStackBuilder addLore(Component line, Component... lines) {
        if (this.lore == null) {
            this.lore = new ArrayList<>();
        }

        lore.add(line);

        if (lines.length > 0) {
            lore.addAll(List.of(lines));
        }

        return this;
    }

    public ItemStackBuilder addLore(List<Component> lines) {
        if (this.lore == null) {
            this.lore = new ArrayList<>();
        }

        lore.addAll(lines);

        return this;
    }

    public ItemStackBuilder addEnchantment(Enchantment enchantment, int level) {
        if (this.enchantments == null) {
            this.enchantments = new HashMap<>();
        }

        this.enchantments.put(enchantment, level);

        return this;
    }

    public ItemStackBuilder addFlags(ItemFlag flag, ItemFlag... flags) {
        if (itemFlags == null) {
            this.itemFlags = new ArrayList<>();
        }

        itemFlags.add(flag);

        if (flags.length > 0) {
            itemFlags.addAll(List.of(flags));
        }

        return this;
    }

    public ItemStackBuilder addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
        if (attributeModifiers == null) {
            attributeModifiers = HashMultimap.create();
        }

        attributeModifiers.put(attribute, modifier);

        return this;
    }

    public ItemStack build() {
        if (material == null) {
            throw new IllegalArgumentException("material is null");
        }

        ItemStack item = new ItemStack(material);
        item.setAmount(amount);

        ItemMeta meta = item.getItemMeta();

        if (displayName != null) {
            meta.displayName(displayName);
        }

        if (lore != null) {
            meta.lore(lore);
        }

        if (itemFlags != null) {
            meta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
        }

        if (enchantments != null) {
            enchantments.forEach((enchantment, level) -> {
                meta.addEnchant(enchantment, level, true);
            });
        }

        if (attributeModifiers != null) {
            attributeModifiers.forEach(meta::addAttributeModifier);
        }

        item.setItemMeta(meta); // may be not necessary // yes its necessary

        return item;
    }
}
