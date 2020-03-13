package xianxian.mc.starocean.dailyrewards;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.gui.GUI;

public class DailyRewardsGUI extends GUI {
    private DailyRewards module;
    private DailyRewardsPlayer playerInfo;
    private Player player;
    private Inventory inventory;
    private final ItemStack borderLine = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
    private final ItemStack claimed = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
    private final ItemStack claimable = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
    private final ItemStack unclaimable = new ItemStack(Material.RED_STAINED_GLASS_PANE);

    private int claimableDay = 0;

    public DailyRewardsGUI(DailyRewards module, Player player, DailyRewardsPlayer playerInfo) {
        super(module, player);
        this.module = module;
        this.player = player;
        this.playerInfo = playerInfo;
        onCreate();
    }

    public void onDestroy() {
        this.module = null;
        this.player = null;
        this.playerInfo = null;
    }

    public void click(InventoryClickEvent event) {
        int index = event.getSlot();
        int[] pos = calculatePosition(index);

        int x = pos[0];
        int y = pos[1];

        if (x == claimableDay && y == 1 && x != 0 && x != 8) {
            Reward reward = module.claim(player, playerInfo, claimableDay);
            ItemStack info = claimed.clone();
            ItemMeta infoMeta = info.getItemMeta();
            infoMeta.setLore(reward.getRewardInfo());
            info.setItemMeta(infoMeta);
            setItemAtPosition(x, y, info);
        }
    }

    private void setItemAtPosition(int x, int y, ItemStack stack) {
        inventory.setItem(calculateIndex(x, y), stack);
    }

    @Override
    public void refresh() {

        for (int i = 0; i < 9; i++) {
            setItemAtPosition(i, 0, borderLine);
            setItemAtPosition(i, 2, borderLine);
        }
        setItemAtPosition(0, 1, borderLine);
        setItemAtPosition(8, 1, borderLine);

        int next = playerInfo.getNext();
        
        for (int i = 1; i <= 7; i++) {
            Reward reward = module.getReward(i);
            if (i < next) {
                ItemStack info = claimed.clone();
                ItemMeta infoMeta = info.getItemMeta();
                infoMeta.setLore(reward.getRewardInfo());
                info.setItemMeta(infoMeta);
                setItemAtPosition(i, 1, info);
            } else if (i > next) {
                ItemStack info = unclaimable.clone();
                ItemMeta infoMeta = info.getItemMeta();
                infoMeta.setLore(reward.getRewardInfo());
                info.setItemMeta(infoMeta);
                setItemAtPosition(i, 1, info);
            } else if (i == next) {
                if (playerInfo.isNextClaimable()) {
                    ItemStack info = claimable.clone();
                    ItemMeta infoMeta = info.getItemMeta();
                    infoMeta.setLore(reward.getRewardInfo());
                    info.setItemMeta(infoMeta);
                    claimableDay = i;
                    setItemAtPosition(i, 1, info);
                } else {
                    ItemStack info = unclaimable.clone();
                    ItemMeta infoMeta = info.getItemMeta();
                    infoMeta.setLore(reward.getRewardInfo());
                    info.setItemMeta(infoMeta);
                    setItemAtPosition(i, 1, info);
                }
            }
        }

    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onCreate() {
        inventory = module.getPlugin().getServer().createInventory(this, 27, ChatColor.AQUA + "星海签到");
    
        ItemMeta borderLineMeta = borderLine.getItemMeta();
        borderLineMeta.setDisplayName(" ");
        borderLine.setItemMeta(borderLineMeta);

        ItemMeta claimedMeta = claimed.getItemMeta();
        claimedMeta.setDisplayName(ChatColor.GRAY + "" + ChatColor.BOLD + "已领取");
        claimed.setItemMeta(claimedMeta);

        ItemMeta claimableMeta = claimable.getItemMeta();
        claimableMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "可领取");
        claimable.setItemMeta(claimableMeta);

        ItemMeta unclaimableMeta = unclaimable.getItemMeta();
        unclaimableMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "不可领取");
        unclaimable.setItemMeta(unclaimableMeta);
    }

}
