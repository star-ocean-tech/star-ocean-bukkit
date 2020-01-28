package xianxian.mc.starocean.upgradingtools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class UpgradingTools extends Module implements Listener {
    private static final String CURRENT_LEVEL_STRING = ChatColor.YELLOW + "当前等级: ";
    private static final String POINTS_STRING = ChatColor.AQUA + "强化点数: ";
    private static final String UPGRADE_COST_STRING = ChatColor.LIGHT_PURPLE + "升级还需: ";

    private final NamespacedKey CUSTOM_LEVEL_KEY;
    private final NamespacedKey CURRENT_LEVEL_KEY;
    private final NamespacedKey POINTS_KEY;
    private final NamespacedKey UPGRADE_COST_KEY;
    private final Messager messager;

    private UpgradingTypes upgradings;

    private Map<Player, UpgradingGUI> upgradingGUIs = new HashMap<Player, UpgradingGUI>();

    public UpgradingTools(AbstractPlugin plugin) {
        super(plugin);
        this.setDescription("工具升级系统");
        CUSTOM_LEVEL_KEY = new NamespacedKey(plugin, getIdentifiedName() + "-CustomLevel");
        CURRENT_LEVEL_KEY = new NamespacedKey(plugin, "CurrentLevel");
        POINTS_KEY = new NamespacedKey(plugin, "Points");
        UPGRADE_COST_KEY = new NamespacedKey(plugin, "UpgradeCost");

        messager = new Messager(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        UpgradingGUI gui = upgradingGUIs.get(event.getWhoClicked());
        if (gui != null) {
            gui.click(event.getSlot());
            event.setCancelled(true);
            return;
        }
        // TODO: implement this
        if (false && event.getClickedInventory() instanceof AnvilInventory) {
            ItemStack tool = event.getInventory().getItem(0);
            ItemStack ingredient = event.getInventory().getItem(1);
            if (ingredient == null)
                return;
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        UpgradingGUI gui = upgradingGUIs.remove(event.getPlayer());
        if (gui != null) {
            gui.close();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled() || !event.getPlayer().getGameMode().equals(GameMode.SURVIVAL))
            return;
        Player player = event.getPlayer();
        ItemStack tool = player.getInventory().getItemInMainHand();
        if (event.getBlock().getDrops(tool).size() > 0 && ToolType.contain(tool.getType())) {
            increaseExp(player, tool, 1);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!event.isCancelled() && event.getEntity() instanceof LivingEntity && event.getDamager() instanceof Player
                && event.getCause().equals(DamageCause.ENTITY_ATTACK)) {
            Player player = (Player) event.getDamager();
            LivingEntity entity = (LivingEntity) event.getEntity();

            if ((entity.getHealth() - event.getFinalDamage()) <= 0) {
                ItemStack tool = player.getInventory().getItemInMainHand();
                Material type = tool.getType();
                if (ToolType.SWORD.contains(type) || ToolType.CROSSBOW.contains(type) || ToolType.BOW.contains(type))
                    ;
                increaseExp(player, tool, 1);
            }
        }
    }

    public boolean increaseExp(Player player, ItemStack tool, int amount) {
        boolean isLevelUP = false;
        ItemMeta meta = tool.getItemMeta();
        ItemInfo info = new ItemInfo();
        info.read(tool);
        if (!info.isNoData()) {
            int currentLevel = info.getCurrentLevel();
            int points = info.getPoints();
            int upgradeCost = info.getUpgradeCost() - 1;
            if (upgradeCost <= 0) {
                currentLevel++;
                points++;
                if (currentLevel >= 0 && currentLevel <= 15) {
                    upgradeCost = 2 * currentLevel + 7;
                } else if (currentLevel >= 16 && currentLevel <= 30) {
                    upgradeCost = 5 * currentLevel - 38;
                } else if (currentLevel >= 31) {
                    upgradeCost = 9 * currentLevel - 158;
                }

                if (player != null) {
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 100, 1.0F);
                    this.messager.sendMessageTo(player,
                            new TextComponent(ChatColor.YELLOW + "恭喜!你的" + ChatColor.RESET
                                    + (meta.hasDisplayName() ? meta.getDisplayName() : getPlugin().getServer().getLocalization().getLocalizedItemName(tool))
                                    + ChatColor.RESET + ChatColor.YELLOW + "已经升级至第" + ChatColor.GREEN + ChatColor.BOLD
                                    + currentLevel + ChatColor.RESET + ChatColor.YELLOW + "级"));
                }
                isLevelUP = true;
            }
            info.setCurrentLevel(currentLevel);
            info.setPoints(points);
            info.setUpgradeCost(upgradeCost);
            info.apply(tool);
        } else {
            info.setCurrentLevel(0);
            info.setPoints(0);
            info.setUpgradeCost(6);
            info.apply(tool);
        }

        return isLevelUP;
    }

    public void openGUI(UpgradingGUI gui) {
        this.upgradingGUIs.put(gui.getPlayer(), gui);
    }

    public UpgradingTypes getUpgradings() {
        return upgradings;
    }

    public Messager getMessager() {
        return messager;
    }

    @Override
    public boolean checkIfCanLoad() {
        return true;
    }

    @Override
    public void prepare() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        CommandUpgrade upgrade = new CommandUpgrade(this);
        upgrade.registerDefaultPermission();
        plugin.getCommandManager().registerCommand(upgrade);
        upgradings = new UpgradingTypes(this);
        upgradings.load();
    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {

    }

    public class ItemInfo {
        private boolean noData = false;
        private int currentLevel = 0;
        private int points = 0;
        private int upgradeCost = 0;

        public boolean isNoData() {
            return noData;
        }

        public int getCurrentLevel() {
            return currentLevel;
        }

        public int getPoints() {
            return points;
        }

        public int getUpgradeCost() {
            return upgradeCost;
        }

        public void setCurrentLevel(int currentLevel) {
            this.currentLevel = currentLevel;
        }

        public void setPoints(int points) {
            this.points = points;
        }

        public void setUpgradeCost(int upgradeCost) {
            this.upgradeCost = upgradeCost;
        }

        public void read(ItemStack stack) {
            ItemMeta meta = stack.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            PersistentDataContainer customLevel = container.get(CUSTOM_LEVEL_KEY, PersistentDataType.TAG_CONTAINER);
            if (customLevel == null) {
                noData = true;
                return;
            }
            this.currentLevel = customLevel.get(CURRENT_LEVEL_KEY, PersistentDataType.INTEGER);
            this.points = customLevel.get(POINTS_KEY, PersistentDataType.INTEGER);
            this.upgradeCost = customLevel.get(UPGRADE_COST_KEY, PersistentDataType.INTEGER);
        }

        public void apply(ItemStack stack) {
            ItemMeta meta = stack.getItemMeta();
            PersistentDataContainer rootContainer = meta.getPersistentDataContainer();
            PersistentDataContainer container = rootContainer.get(CUSTOM_LEVEL_KEY, PersistentDataType.TAG_CONTAINER);
            if (container == null) {
                container = rootContainer.getAdapterContext().newPersistentDataContainer();
            }

            container.set(CURRENT_LEVEL_KEY, PersistentDataType.INTEGER, currentLevel);
            container.set(POINTS_KEY, PersistentDataType.INTEGER, points);
            container.set(UPGRADE_COST_KEY, PersistentDataType.INTEGER, upgradeCost);
            rootContainer.set(CUSTOM_LEVEL_KEY, PersistentDataType.TAG_CONTAINER, container);

            List<String> lore;
            if (meta.hasLore())
                lore = meta.getLore();
            else
                lore = new ArrayList<String>();
            if (lore.size() > 0) {
                int currentLevelIndex = -1;
                int pointsIndex = -1;
                int upgradeCostIndex = -1;
                for (int i = 0, size = lore.size(); i < size; i++) {
                    String s = lore.get(i);
                    if (s.startsWith(CURRENT_LEVEL_STRING)) {
                        lore.set(i, CURRENT_LEVEL_STRING + currentLevel);
                        currentLevelIndex = i;
                    } else if (s.startsWith(POINTS_STRING)) {
                        lore.set(i, POINTS_STRING + points);
                        pointsIndex = i;
                    } else if (s.startsWith(UPGRADE_COST_STRING)) {
                        lore.set(i, UPGRADE_COST_STRING + upgradeCost);
                        upgradeCostIndex = i;
                    }
                }
                if (currentLevelIndex < 0) {
                    lore.add(CURRENT_LEVEL_STRING + currentLevel);
                }
                if (pointsIndex < 0) {
                    lore.add(POINTS_STRING + points);
                }
                if (upgradeCostIndex < 0) {
                    lore.add(UPGRADE_COST_STRING + upgradeCost);
                }
            } else {
                lore.add(CURRENT_LEVEL_STRING + currentLevel);
                lore.add(POINTS_STRING + points);
                lore.add(UPGRADE_COST_STRING + upgradeCost);
            }
            meta.setLore(lore);
            stack.setItemMeta(meta);
        }
    }
}
