package org.staroceanmc.bukkit.utils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ItemStackBuilder {
    private static final Logger LOGGER = Logger.getLogger("StarOcean-ItemStackBuilder");
    private Material material;
    private int amount = 1;
    private Component displayName;
    private List<Component> lore;
    private Map<Enchantment, Integer> enchantments;
    private List<ItemFlag> itemFlags;
    private Multimap<Attribute, AttributeModifier> attributeModifiers;
    private NBTContainer nbt;

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

    public ItemStackBuilder nbt(String s) {
        return nbt(new NBTContainer(s));
    }

    public ItemStackBuilder nbt(NBTContainer container) {
        this.nbt = container;
        return this;
    }

    public static ItemStackBuilder fromConfiguration(ConfigurationSection section) {
        ItemStackBuilder builder = new ItemStackBuilder();
        Material material = EnumMatcher.matchIgnoreCase(Material::values, section.getString("material", Material.AIR.name()));
        String displayName = section.getString("display-name");
        int amount = Math.max(section.getInt("amount"), 1);
        List<String> lore = section.getStringList("lore");
        List<String> flags = section.getStringList("flags");
        ConfigurationSection enchantments = section.getConfigurationSection("enchantments");
        ConfigurationSection attributeModifiers = section.getConfigurationSection("attribute-modifiers");
        String nbtString = section.getString("nbt");

        if (material == null) {
            material = Material.AIR;
            LOGGER.severe("Invalid item material: " + section.getString("material"));
        }

        builder.material(material);
        builder.amount(amount);

        if (displayName != null) {
            builder.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(displayName).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        }

        if (!lore.isEmpty()) {
            lore.forEach((line) -> {
                builder.addLore(LegacyComponentSerializer.legacyAmpersand().deserialize(line).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
            });
        }

        if (!flags.isEmpty()) {
            flags.forEach((flag) -> {
                ItemFlag flag1 = EnumMatcher.matchIgnoreCase(ItemFlag::values, flag);
                if (flag1 != null) {
                    builder.addFlags(flag1);
                } else {
                    LOGGER.severe("Invalid item flag: " + flag);
                }
            });
        }

        if (enchantments != null) {
            enchantments.getKeys(false).forEach((enchantKey) -> {
                NamespacedKey key = NamespacedKey.fromString(enchantKey);

                if (key == null) {
                    LOGGER.severe("Invalid enchantment: " + enchantKey);
                    return;
                }

                Enchantment enchantment = Enchantment.getByKey(key);
                if (enchantment == null) {
                    LOGGER.severe("Invalid enchantment: " + enchantKey);
                    return;
                }

                builder.addEnchantment(enchantment, enchantments.getInt(enchantKey));
            });
        }

        if (attributeModifiers != null) {
            attributeModifiers.getKeys(false).forEach((attributeKey) -> {
                Attribute attribute = EnumMatcher.matchIgnoreCase(Attribute::values, attributeKey);

                AttributeModifier modifier = attributeModifiers.getObject(attributeKey, AttributeModifier.class);

                if (modifier == null) {
                    LOGGER.severe("Invalid attribute modifier: " + attributeKey);
                    return;
                }

                builder.addAttributeModifier(attribute, modifier);
            });
        }

        if (nbtString != null) {
            try {
                builder.nbt(nbtString);
            } catch (Exception e) {
                LOGGER.severe("Invalid nbt tag: " + nbtString);
                e.printStackTrace();
            }
        }

        return builder;
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

        if (nbt != null) {
            NBTItem nbtItem = new NBTItem(item);
            nbtItem.mergeCompound(nbt);
            item = nbtItem.getItem();
        }

        return item;
    }
}
