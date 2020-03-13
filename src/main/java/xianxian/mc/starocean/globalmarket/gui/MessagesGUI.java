package xianxian.mc.starocean.globalmarket.gui;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.globalmarket.GlobalMarket;
import xianxian.mc.starocean.globalmarket.Paging;
import xianxian.mc.starocean.globalmarket.messages.Message;
import xianxian.mc.starocean.globalmarket.messages.MessagesManager.MessagesUser;
import xianxian.mc.starocean.gui.GUI;

public class MessagesGUI extends GUI {
    private final GlobalMarket module;
    private final GlobalMarketGUI parent;
    private final MessagesUser user;
    
    private Inventory inventory;
    
    private int page = 0;
    
    private ItemStack borderLine;
    private ItemStack nextPage;
    private ItemStack prevPage;
    private ItemStack currentPage;
    private ItemStack nextPageNotAvailable;
    private ItemStack prevPageNotAvailable;
    private ItemStack deleteAll;
    private ItemStack markAllRead;
    
    private Paging<Message> paging;
    
    private List<GUISlot> currentSlots = new ArrayList<GUISlot>();
    private boolean prevPageAvailable;
    private boolean nextPageAvailable;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public MessagesGUI(GlobalMarketGUI parent, GlobalMarket module, Player player, MessagesUser user) {
        super(module, player);
        this.module = module;
        this.parent = parent;
        this.user = user;
        if (user != null)
            this.paging = user.getPaging();
        onCreate();
    }

    @Override
    public void onCreate() {
        inventory = getModule().getPlugin().getServer().createInventory(this, 54, ChatColor.AQUA + "信息");
        
        borderLine = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta borderLineMeta = borderLine.getItemMeta();
        borderLineMeta.setDisplayName(" ");
        borderLine.setItemMeta(borderLineMeta);
        
        nextPage = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta nextPageMeta = nextPage.getItemMeta();
        nextPageMeta.setDisplayName(ChatColor.GREEN + "下一页");
        nextPage.setItemMeta(nextPageMeta);
        
        prevPage = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta prevPageMeta = prevPage.getItemMeta();
        prevPageMeta.setDisplayName(ChatColor.GREEN + "上一页");
        prevPage.setItemMeta(prevPageMeta);
        
        nextPageNotAvailable = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta nextPageNotAvailableMeta = nextPageNotAvailable.getItemMeta();
        nextPageNotAvailableMeta.setDisplayName(ChatColor.RED + "已经是最后一页了");
        nextPageNotAvailable.setItemMeta(nextPageNotAvailableMeta);
        
        prevPageNotAvailable = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        setItemDisplayName(prevPageNotAvailable, ChatColor.RED + "已经是第一页了");
        
        currentPage = new ItemStack(Material.PAPER);
        ItemMeta currentPageMeta = currentPage.getItemMeta();
        currentPageMeta.setDisplayName(ChatColor.YELLOW + "当前页");
        currentPageMeta.setLore(Lists.newArrayList(ChatColor.GRAY + "点击跳页"));
        currentPage.setItemMeta(currentPageMeta);
        
        deleteAll = new ItemStack(Material.REDSTONE_TORCH);
        setItemDisplayName(deleteAll, ChatColor.RED + "删除全部");
        
        {
            markAllRead = new ItemStack(Material.ENDER_CHEST);
            setItemDisplayName(markAllRead, ChatColor.GREEN + "标记所有为已读");
        }
        
        for (int i = 0; i < 9; i++) {
            if (i == 7 || i == 8)
                continue;
            inventory.setItem(calculateIndex(i, 0), borderLine);
        }
        
        for (int i = 0; i < 9; i++) {
            if (i == 0 || i == 4 || i == 8)
                continue;
            inventory.setItem(calculateIndex(i, 5), borderLine);
        }

        inventory.setItem(calculateIndex(8, 0), deleteAll);

        inventory.setItem(calculateIndex(7, 0), markAllRead);
    }

    @Override
    public void refresh() {
        int pages = paging == null ? 0 : paging.pages().size();
        
        if (page != 0) {
            List<String> lore = new ArrayList<String>();
            lore.add(ChatColor.GRAY + "点击上一页");
            ItemMeta meta = prevPage.getItemMeta();
            meta.setLore(lore);
            meta.setDisplayName(ChatColor.GREEN + "第" + (page) + "页");
            prevPage.setItemMeta(meta);
            
            inventory.setItem(calculateIndex(0, 5), prevPage);
            prevPageAvailable = true;
        } else {
            inventory.setItem(calculateIndex(0, 5), prevPageNotAvailable);
            prevPageAvailable = false;
        }
        
        {
            ItemMeta meta = currentPage.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "第" + (page + 1) + "页, 共" + (pages == 0 ? 1 : pages) + "页");
            currentPage.setItemMeta(meta);
            inventory.setItem(calculateIndex(4, 5), currentPage);
        }
        
        if (page < pages - 1) {
            List<String> lore = new ArrayList<String>();
            lore.add(ChatColor.GRAY + "点击下一页");
            ItemMeta meta = nextPage.getItemMeta();
            meta.setLore(lore);
            meta.setDisplayName(ChatColor.GREEN + "第" + (page + 2) + "页");
            nextPage.setItemMeta(meta);
            
            inventory.setItem(calculateIndex(8, 5), nextPage);
            nextPageAvailable = true;
        } else {
            inventory.setItem(calculateIndex(8, 5), nextPageNotAvailable);
            nextPageAvailable = false;
        }
        
        currentSlots.clear();
        
        if (pages == 0) {
            return;
        }
        
        List<Message> currentMessages = paging.pages().get(page);
        
        for (int i = 0, size = currentMessages.size(); i < 36; i++) {
            if (i >= size) {
                inventory.setItem(i + 9, null);
            } else {
                Message record = currentMessages.get(i);
                GUISlot slot = new GUISlot(i + 9, record);
                inventory.setItem(slot.getPos(), slot.getDisplayItem());
                currentSlots.add(slot);
            }
        }
    }

    @Override
    public void click(InventoryClickEvent event) {
        int slot = event.getSlot();
        int[] pos = calculatePosition(slot);
        int x = pos[0];
        int y = pos[1];
        if (user == null)
            return;
        if (x == 0 && y == 5) {
            if (prevPageAvailable) {
                page -= 1;
                refresh();
            }
        } else if (x == 8 && y == 5) {
            if (nextPageAvailable) {
                page += 1;
                refresh();
            }
        } else if (x == 7 && y == 0) {
            user.getUnreadMessages().forEach((message)->{
                message.setRead(true);
                module.getPlugin().newTaskChain().async(()->module.getStorage().updateMessage(message)).execute();
            });
            user.getUnreadMessages().clear();
            refresh();
        } else if (x == 8 && y == 0) {
            user.getMessages().forEach((message)->{
                module.getPlugin().newTaskChain().async(()->module.getStorage().removeMessage(message)).execute();
            });
            user.getMessages().clear();
            refresh();
        } else if (slot >= 9 && slot < 46) {
            GUISlot guiSlot = getBySlot(slot);
            if (guiSlot == null) {
                return;
            }
            switch (event.getClick()) {
                case LEFT:
                    getModule().getMessager().sendMessageTo(getPlayer(), ChatColor.YELLOW + guiSlot.getMessage().getContent());
                    if (guiSlot.getMessage().isRead())
                        return;
                    user.getUnreadMessages().remove(guiSlot.getMessage());
                    guiSlot.getMessage().setRead(true);
                    module.getPlugin().newTaskChain().async(()->module.getStorage().updateMessage(guiSlot.getMessage())).execute();;
                    guiSlot.refreshItem();
                    break;
                case RIGHT:
                    inventory.setItem(guiSlot.pos, null);
                    this.currentSlots.remove(guiSlot);
                    user.getMessages().remove(guiSlot.getMessage());
                    if (!guiSlot.getMessage().isRead()) {
                        user.getUnreadMessages().remove(guiSlot.getMessage());
                    }
                    module.getPlugin().newTaskChain().async(()->module.getStorage().removeMessage(guiSlot.getMessage()));
                    refresh();
                    break;
                default:
                    break;
            }
            
        }
    }
    
    private GUISlot getBySlot(int slot) {
        int guiSlot = slot - 9;
        int size = currentSlots.size();
        if (size <= guiSlot)
            return null;
        return currentSlots.get(guiSlot);
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onDestroy() {
        
    }
    
    private void setItemDisplayName(ItemStack stack, String displayName) {
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(displayName);
        stack.setItemMeta(meta);
    }
    
    private class GUISlot {
        private int pos;
        private Message message;
        private ItemStack displayItem;
        
        public GUISlot(int pos, Message message) {
            this.pos = pos;
            this.message = message;
            refreshItem();
        }
        
        public void refreshItem() {
            if (message.isRead())
                this.displayItem = new ItemStack(Material.BOOK);
            else 
                this.displayItem = new ItemStack(Material.ENCHANTED_BOOK);
            
            ItemMeta meta = this.displayItem.getItemMeta();
            
            if (message.isRead()) {
                meta.setDisplayName(ChatColor.GRAY+"已读");
            } else {
                meta.setDisplayName(ChatColor.YELLOW+"未读");
            }
            
            List<String> lore = new ArrayList<String>();
            
            lore.add(message.getContent());
            
            lore.add(" ");
            lore.add(ChatColor.GRAY + "时间: " + DATE_TIME_FORMATTER.format(message.getDate()));
            lore.add(ChatColor.YELLOW + "左键标记为已读并输出到聊天栏");
            lore.add(ChatColor.YELLOW + "右键删除");
            
            meta.setLore(lore);
            
            this.displayItem.setItemMeta(meta);
        }

        public int getPos() {
            return pos;
        }

        public Message getMessage() {
            return message;
        }

        public ItemStack getDisplayItem() {
            return displayItem;
        }
    }
}
