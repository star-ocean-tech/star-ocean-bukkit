package xianxian.mc.starocean.upgradingtools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class UpgradingTypes {
    private UpgradingTools module;
    private FileConfiguration translationConfig;
    private FileConfiguration upgradingConfig;
    private Map<String, Upgrading> upgradingMap = new HashMap<>();
    private Map<ToolType, List<Upgrading>> supportedUpgradings = new HashMap<>();

    private final NamespacedKey CUSTOM_UPGRADING;
    private final NamespacedKey ALL_UPGRADINGS;

    public UpgradingTypes(UpgradingTools module) {
        this.module = module;
        CUSTOM_UPGRADING = new NamespacedKey(module.getPlugin(), "CustomUpgrading");
        ALL_UPGRADINGS = new NamespacedKey(module.getPlugin(), "AllUpgradings");
    }

    public void load() {
        upgradingConfig = this.module.loadConfig("upgradings.yml");
        translationConfig = this.module.loadConfig("translation.yml");

        Enchantment[] enchantments = Enchantment.values();

        for (int i = 0; i < enchantments.length; i++) {
            Enchantment enchantment = enchantments[i];

            NamespacedKey key = enchantment.getKey();

            String translationKey = key.getNamespace() + "." + key.getKey();

            if (!translationConfig.isSet(translationKey)) {
                translationConfig.set(translationKey, key.getKey());
            }

            if (enchantment.equals(Enchantment.VANISHING_CURSE) || enchantment.equals(Enchantment.BINDING_CURSE)) {
                continue;
            }

            Upgrading upgrading = new Upgrading(key, translationConfig.getString(translationKey), 3);
            upgrading.setApplyAction((s, l) -> upgrading.applyEnchant(enchantment, s, l, 3));
            this.upgradingMap.put(key.toString(), upgrading);
            EnchantmentTarget target = enchantment.getItemTarget();
            for (ToolType type : ToolType.values()) {
                if (target.equals(EnchantmentTarget.ALL) || target.equals(EnchantmentTarget.BREAKABLE)
                        || target.equals(type.getEnchantmentTarget())) {
                    List<Upgrading> upgradings = this.supportedUpgradings.get(type);
                    if (upgradings == null) {
                        upgradings = new ArrayList<UpgradingTypes.Upgrading>();
                        upgradings.add(upgrading);
                        this.supportedUpgradings.put(type, upgradings);
                    } else {
                        upgradings.add(upgrading);
                    }
                }
            }
        }

        this.module.saveConfig(translationConfig, "translation.yml");

        for (ToolType type : ToolType.values()) {
            String name = type.name();
            ConfigurationSection section = upgradingConfig.getConfigurationSection(name);
            if (section == null) {
                section = upgradingConfig.createSection(name);
                List<Upgrading> upgradings = new ArrayList<UpgradingTypes.Upgrading>();
                upgradings.addAll(this.supportedUpgradings.get(type));
                List<String> upgradingKeys = new ArrayList<String>();
                upgradings.forEach((u) -> {
                    upgradingKeys.add(u.getID().toString());
                });
                section.set("support-upgrading", upgradingKeys);
            } else {
                List<String> upgradingKeys = section.getStringList("support-upgrading");
                List<Upgrading> upgradings = new ArrayList<Upgrading>();

                upgradingKeys.forEach((s) -> {
                    Upgrading upgrading = upgradingMap.get(s);
                    if (upgrading != null) {
                        upgradings.add(upgrading);
                    } else {
                        module.logger().severe("Can't find " + s + " when loading type " + name);
                    }
                });
                this.supportedUpgradings.put(type, upgradings);
            }
        }

        this.module.saveConfig(upgradingConfig, "upgradings.yml");
    }

    public void addCustomUpgrading(Upgrading upgrading) {
        this.upgradingMap.put(upgrading.getID().toString(), upgrading);
    }

    public List<Upgrading> getSupportedUpgradings(Material tool) {
        ToolType type = null;

        for (ToolType t : ToolType.values()) {
            if (t.contains(tool)) {
                type = t;
                break;
            }
        }

        if (type != null) {
            List<Upgrading> upgradings = supportedUpgradings.get(type);
            return upgradings == null ? Arrays.asList() : upgradings;
        }

        return Arrays.asList();
    }

    public Map<Upgrading, Integer> getUpgradings(ItemStack stack) {

        if (!ToolType.contain(stack.getType())) {
            return null;
        }

        Map<Upgrading, Integer> upgradings = new HashMap<UpgradingTypes.Upgrading, Integer>();

        ItemMeta meta = stack.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        PersistentDataContainer customUpgradings = container.get(CUSTOM_UPGRADING, PersistentDataType.TAG_CONTAINER);
        List<Upgrading> supportedUpgradings = getSupportedUpgradings(stack.getType());
        supportedUpgradings.forEach((u) -> {
            if (customUpgradings == null) {
                upgradings.put(u, 0);
            } else {
                upgradings.put(u, customUpgradings.getOrDefault(u.getID(), PersistentDataType.INTEGER, 0));
            }
        });
        return upgradings;
    }

    public NamespacedKey getCustomUpgradingKey() {
        return CUSTOM_UPGRADING;
    }

    public NamespacedKey getAllUpgradingsKey() {
        return ALL_UPGRADINGS;
    }

    public class Upgrading {
        private NamespacedKey id;
        private String name;
        private int pointsPerLevel;
        private BiFunction<ItemStack, Integer, ItemStack> onApply;

        private Upgrading(NamespacedKey id, String name, int pointsPerLevel) {
            this.id = id;
            this.name = name;
            this.pointsPerLevel = pointsPerLevel;
        }

        public NamespacedKey getID() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getPointsPerLevel() {
            return pointsPerLevel;
        }

        public void setApplyAction(BiFunction<ItemStack, Integer, ItemStack> onApply) {
            this.onApply = onApply;
        }

        public ItemStack apply(ItemStack stack, int level) {
            if (this.onApply != null)
                stack = onApply.apply(stack, level);
            ItemMeta meta = stack.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            PersistentDataContainer customUpgradings = container.has(CUSTOM_UPGRADING, PersistentDataType.TAG_CONTAINER)
                    ? container.get(CUSTOM_UPGRADING, PersistentDataType.TAG_CONTAINER)
                    : container.getAdapterContext().newPersistentDataContainer();
            customUpgradings.set(id, PersistentDataType.INTEGER, level);
            container.set(CUSTOM_UPGRADING, PersistentDataType.TAG_CONTAINER, customUpgradings);
            stack.setItemMeta(meta);
            return stack;
        }

        public ItemStack applyEnchant(Enchantment enchantment, ItemStack stack, int level, int levelPerTier) {
            int enchantLevel = level == 0 ? 0 : ((level / 3) + 1);
            if (enchantLevel == 0)
                return stack;
            ItemMeta meta = stack.getItemMeta();
            if (meta.getEnchantLevel(enchantment) >= enchantLevel)
                return stack;
            meta.addEnchant(enchantment, enchantLevel, true);
            stack.setItemMeta(meta);
            return stack;
        }
    }
}
