package xianxian.mc.starocean.upgradingtools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.upgradingtools.UpgradingTools.ItemInfo;
import xianxian.mc.starocean.upgradingtools.UpgradingTypes.Upgrading;

public class UpgradingGUI {
    private final UpgradingTools module;
    private final Inventory inventory;
    private final ItemStack stack;
    private final Player player;
    private final List<UpgradingSlot> slots;

    private ItemStack border;
    private ItemStack upgradable;
    private ItemStack unupgradable;

    private UpgradingSlot previousClicked;

    public UpgradingGUI(UpgradingTools module, Player player, ItemStack stack) {
        this.module = module;
        this.player = player;
        this.stack = stack;
        this.inventory = module.getPlugin().getServer().createInventory(player, 36, ChatColor.DARK_RED + "强化");
        this.slots = new ArrayList<UpgradingSlot>();
    }

    public void create() {
        border = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        borderMeta.setDisplayName("");
        border.setItemMeta(borderMeta);
        upgradable = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta upgradableMeta = upgradable.getItemMeta();
        upgradableMeta.setDisplayName("");
        for (int i = 0; i < 9; i++) {
            setItemAtPosition(i, 0, border);
            setItemAtPosition(i, 3, border);
        }
        setItemAtPosition(0, 1, border);
        setItemAtPosition(0, 2, border);
        setItemAtPosition(8, 1, border);
        setItemAtPosition(8, 2, border);

        setItemAtPosition(0, 0, stack);

        AtomicInteger currentX = new AtomicInteger(1);
        AtomicInteger currentY = new AtomicInteger(1);

        Map<Upgrading, Integer> allUpgradings = module.getUpgradings().getUpgradings(stack);
        allUpgradings.keySet().forEach((u) -> {
            int points = allUpgradings.get(u);
            ItemStack info = upgradable.clone();
            UpgradingSlot slot = new UpgradingSlot(stack, info, currentX.getAndIncrement(), currentY.get(), u, points);
            slot.applyInfo();
            slots.add(slot);
            if (currentX.get() >= 8) {
                currentX.set(1);
                currentY.getAndIncrement();
            }
        });
    }

    public void open() {
        player.openInventory(inventory);
        module.openGUI(this);
    }

    public void close() {

    }

    public void click(int index) {
        int[] pos = convertToPosition(index);
        int x = pos[0];
        int y = pos[1];

        UpgradingSlot slot = null;
        for (int i = 0, size = slots.size(); i < size; i++) {
            UpgradingSlot s = slots.get(i);
            if (s.getX() == x && s.getY() == y) {
                slot = s;
                break;
            }
        }

        if (slot == null) {
            return;
        }

        if (previousClicked == slot) {
            slot.click();
        } else {
            module.getMessager().sendMessageTo(player, ChatColor.YELLOW + "请再次点击来确认是否强化");
        }

        previousClicked = slot;
    }

    public UpgradingTools getModule() {
        return module;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ItemStack getStack() {
        return stack;
    }

    public Player getPlayer() {
        return player;
    }

    private int[] convertToPosition(int index) {
        return new int[] { index % 9, index / 9 };
    }

    private int convertToIndex(int x, int y) {
        return x + (9 * y);
    }

    public void setItemAtPosition(int x, int y, ItemStack stack) {
        inventory.setItem(convertToIndex(x, y), stack);
    }

    public class UpgradingSlot {
        private ItemStack item;
        private ItemStack infoPanel;
        private int x;
        private int y;
        private Upgrading upgrading;
        private int points;
        private int nextLevel;
        private int pointsNeeded;
        private int pointsPerLevel;

        public UpgradingSlot(ItemStack item, ItemStack infoPanel, int x, int y, Upgrading upgrading, int points) {
            this.item = item;
            this.infoPanel = infoPanel;
            this.x = x;
            this.y = y;
            this.upgrading = upgrading;
            this.points = points;
            pointsPerLevel = upgrading.getPointsPerLevel();
            nextLevel = (points < pointsPerLevel - 1 ? (points == 0 ? 1 : pointsPerLevel) : points) / pointsPerLevel
                    + 1;
            nextLevel = points >= pointsPerLevel ? nextLevel + 1 : nextLevel;
            pointsNeeded = nextLevel == 1 ? 1 : pointsPerLevel - points % pointsPerLevel;

        }

        public void applyInfo() {
            ItemMeta meta = infoPanel.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "可升级");
            meta.setLore(
                    Arrays.asList(ChatColor.YELLOW + "" + ChatColor.ITALIC + String.format("再花费%d点强化点数", pointsNeeded),
                            ChatColor.YELLOW + "" + ChatColor.ITALIC + "可获得以上属性"));
            infoPanel.setItemMeta(meta);
            upgrading.apply(infoPanel, nextLevel * pointsPerLevel - 1);
            UpgradingGUI.this.setItemAtPosition(x, y, infoPanel);
            UpgradingGUI.this.setItemAtPosition(0, 0, stack);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Upgrading getUpgrading() {
            return upgrading;
        }

        public int getPoints() {
            return points;
        }

        public void click() {
            ItemInfo itemInfo = module.new ItemInfo();
            itemInfo.read(item);
            if (itemInfo.getPoints() == 0) {
                module.getMessager().sendMessageTo(player, ChatColor.RED + "你没有足够的强化点数");
                return;
            }

            itemInfo.setPoints(itemInfo.getPoints() - 1);
            itemInfo.apply(item);

            this.points++;
            nextLevel = (points < pointsPerLevel - 1 ? (points == 0 ? 1 : pointsPerLevel) : points) / pointsPerLevel
                    + 1;
            nextLevel = points >= pointsPerLevel ? nextLevel + 1 : nextLevel;
            pointsNeeded = nextLevel == 1 ? 1 : pointsPerLevel - points % pointsPerLevel;
            ItemMeta meta = item.getItemMeta();

            if (pointsNeeded == pointsPerLevel) {
                module.getMessager().sendMessageTo(player, ChatColor.GREEN + "成功升级了" + ChatColor.RESET
                        + (meta.hasDisplayName() ? meta.getDisplayName() : item.getType().name()) + ChatColor.RESET
                        + ChatColor.GREEN + "，当前等级为" + nextLevel + "，还需" + pointsNeeded + "点数可升级至下一级");

            } else {
                module.getMessager().sendMessageTo(player,
                        ChatColor.GREEN + "成功强化了" + ChatColor.RESET
                                + (meta.hasDisplayName() ? meta.getDisplayName() : item.getType().name())
                                + ChatColor.RESET + ChatColor.GREEN + "，还需" + pointsNeeded + "点数可升级至下一级");
            }

            this.upgrading.apply(item, points);

            this.applyInfo();
        }

    }
}
