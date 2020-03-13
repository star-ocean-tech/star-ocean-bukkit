package xianxian.mc.starocean.globalmarket.gui;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.globalmarket.GlobalMarket;
import xianxian.mc.starocean.globalmarket.Paging;
import xianxian.mc.starocean.globalmarket.messages.MessagesManager.MessagesUser;
import xianxian.mc.starocean.globalmarket.record.MarketRecord;
import xianxian.mc.starocean.gui.GUI;

public class GlobalMarketGUI extends GUI {
    private Inventory inventory;
    
    private int page = 0;
    
    private GlobalMarket module;
    
    private ItemStack playerInfo;
    private ItemStack messagesInfo;
    private ItemStack borderLine;
    private ItemStack nextPage;
    private ItemStack prevPage;
    private ItemStack currentPage;
    private ItemStack nextPageNotAvailable;
    private ItemStack prevPageNotAvailable;
    private ItemStack search;
    private ItemStack sell;
    
    private Paging<MarketRecord> paging;
    
    private MessagesUser messagesUser;
    
    private List<GUISlot> currentSlots = new ArrayList<GlobalMarketGUI.GUISlot>();
    private boolean prevPageAvailable;
    private boolean nextPageAvailable;
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public GlobalMarketGUI(GlobalMarket module, Player player) {
        super(module, player);
        this.module = module;
        this.paging = module.getMarketManager().getDefaultPaging();
    }
    
    public GlobalMarketGUI(GlobalMarket module, Player player, List<MarketRecord> records) {
        super(module, player);
        this.module = module;
        this.paging = new Paging<MarketRecord>(36);
        this.paging.page(records);
    }

    @Override
    public void onCreate() {
        inventory = getModule().getPlugin().getServer().createInventory(this, 54, ChatColor.AQUA + "全球商店");
        
        playerInfo = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta playerInfoMeta = playerInfo.getItemMeta();
        playerInfoMeta.setDisplayName(ChatColor.BLUE.toString() + ChatColor.BOLD + "玩家信息");
        if (playerInfoMeta instanceof SkullMeta) {
            SkullMeta meta = (SkullMeta) playerInfoMeta;
            meta.setOwningPlayer(getPlayer());
        }
        playerInfo.setItemMeta(playerInfoMeta);
        
        messagesInfo = new ItemStack(Material.BOOK);
        setItemDisplayName(messagesInfo, ChatColor.YELLOW + "查看信息");
        
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
        
        search = new ItemStack(Material.COMPASS);
        ItemMeta searchMeta = search.getItemMeta();
        searchMeta.setDisplayName(ChatColor.GREEN + "搜索");
        search.setItemMeta(searchMeta);
        
        {
            sell = new ItemStack(Material.ENDER_CHEST);
            setItemDisplayName(sell, ChatColor.LIGHT_PURPLE + "出售物品");
        }
        
        for (int i = 0; i < 9; i++) {
            if (i == 0 || i == 1 || i == 8)
                continue;
            inventory.setItem(calculateIndex(i, 0), borderLine);
        }
        
        for (int i = 0; i < 9; i++) {
            if (i == 0 || i == 4 || i == 8)
                continue;
            inventory.setItem(calculateIndex(i, 5), borderLine);
        }

        inventory.setItem(calculateIndex(8, 0), search);

        inventory.setItem(calculateIndex(7, 0), sell);
        
        this.messagesUser = module.getMessagesManager().getByUUID(getPlayer().getUniqueId());
    }

    @Override
    public void refresh() {
        int pages = paging.pages().size();
        
        List<String> playerInfoLores = new ArrayList<String>();
        playerInfoLores.add(" ");
        playerInfoLores.add(ChatColor.AQUA.toString() + ChatColor.BOLD + getPlayer().getDisplayName());
        playerInfoLores.add(" ");
        playerInfoLores.add(ChatColor.YELLOW + "总财产: " + module.getVault().getVaultEconomy().format(module.getVault().getVaultEconomy().getBalance(getPlayer())));
        playerInfo.setLore(playerInfoLores);
        inventory.setItem(calculateIndex(0, 0), playerInfo);
        
        {
            ItemMeta meta = messagesInfo.getItemMeta();
            if (messagesUser == null)
                meta.setLore(Lists.newArrayList(ChatColor.YELLOW + "你没有新的信息"));
            else {
                int size = messagesUser.getUnreadMessages().size();
                if (size > 0) {
                    meta.setLore(Lists.newArrayList(ChatColor.YELLOW + "你有"+size+"条新的信息"));
                } else {
                    meta.setLore(Lists.newArrayList(ChatColor.YELLOW + "你没有新的信息"));
                }
            }
            messagesInfo.setItemMeta(meta);
            inventory.setItem(calculateIndex(1, 0), messagesInfo);
        }
 
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
        
        if (page < paging.pages().size() - 1) {
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
        
        if (paging.pages().size() == 0) {
            return;
        }
        
        List<MarketRecord> currentRecords = paging.pages().get(page);
        
        for (int i = 0, size = currentRecords.size(); i < 36; i++) {
            if (i >= size) {
                inventory.setItem(i + 9, null);
            } else {
                MarketRecord record = currentRecords.get(i);
                GUISlot slot = new GUISlot(i + 9, record);
                ItemStack stack = slot.getDisplayItem().clone();
                ItemMeta meta = stack.getItemMeta();
                List<String> lore = meta.getLore();
                if (record.getOwner().equals(getPlayer().getUniqueId())) {
                    lore.add(ChatColor.YELLOW+"点击编辑");
                } else {
                    lore.add(ChatColor.YELLOW+"点击购买");
                }
                meta.setLore(lore);
                stack.setItemMeta(meta);
                inventory.setItem(slot.getPos(), stack);
                currentSlots.add(slot);
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
    
    private void setItemDisplayName(ItemStack stack, String displayName) {
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(displayName);
        stack.setItemMeta(meta);
    }

    @Override
    public void click(InventoryClickEvent event) {
        int slot = event.getSlot();
        int[] pos = calculatePosition(slot);
        int x = pos[0];
        int y = pos[1];
        
        if (x == 1 && y == 0) {
            module.getPlugin().getGUIManager().open(new MessagesGUI(this, module, getPlayer(), messagesUser));
        } else if (x == 0 && y == 5) {
            if (prevPageAvailable) {
                page -= 1;
                refresh();
            }
        } else if (x == 8 && y == 5) {
            if (nextPageAvailable) {
                page += 1;
                refresh();
            }
        } else if (x == 4 && y == 5) {
            int pages = paging.pages().size();
            InputNumber inputNumber = new InputNumber(getModule(), getPlayer(), new InputNumber.Callback() {
                
                @Override
                public void onFinish(int number) {
                    page = number - 1;
                    refresh();
                    getModule().getPlugin().getGUIManager().open(GlobalMarketGUI.this);
                }
                
                @Override
                public void onCancelled() {
                    getModule().getPlugin().getGUIManager().open(GlobalMarketGUI.this);
                }
            }, pages);
            inputNumber.startConversation();
            getPlayer().closeInventory();
        } else if (x == 7 && y == 0) {
            module.getPlugin().getGUIManager().open(new SellGUI(this, module, getPlayer()));
        } if (slot >= 9 && slot < 46) {
            GUISlot guiSlot = getBySlot(slot);
            if (guiSlot == null) {
                return;
            }
            if (guiSlot.getRecord().getOwner().equals(getPlayer().getUniqueId()))
                module.getPlugin().getGUIManager().open(new EditGUI(this, module, getPlayer(), guiSlot.record));
            else
                module.getPlugin().getGUIManager().open(new BuyGUI(this, module, getPlayer(), guiSlot.record, guiSlot.displayItem));
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub

    }

    private class GUISlot {
        private int pos;
        private MarketRecord record;
        private ItemStack displayItem;
        
        public GUISlot(int pos, MarketRecord record) {
            this.pos = pos;
            this.record = record;
            this.displayItem = record.getItem().clone();
            
            ItemMeta meta = this.displayItem.getItemMeta();
            
            String displayName = "";
            
            if (meta.hasDisplayName())
                displayName = meta.getDisplayName();
            else
                displayName = getModule().getPlugin().getServer().getLocalization().getLocalizedItemName(displayItem);
            
            String owner = record.getOwnerPlayer() == null ? record.getOwner().toString() : record.getOwnerPlayer().getName();
            
            switch (record.getRecordType()) {
                case SELL:
                    displayName = ChatColor.GREEN + "出售: " + ChatColor.RESET + displayName;
                    owner = ChatColor.LIGHT_PURPLE + "卖家: "+owner;
                    break;
                case BUY:
                    displayName = ChatColor.RED + "收购: " + ChatColor.RESET + displayName;
                    owner = ChatColor.LIGHT_PURPLE + "买家: "+owner;
                    break;
            }
            
            meta.setDisplayName(displayName);
            
            List<String> lore = new ArrayList<String>();
            
            if (meta.hasLore())
                lore.addAll(meta.getLore());
            
            lore.add(" ");
            lore.add(ChatColor.YELLOW + "单价: " + module.getVault().getVaultEconomy().format(record.getPrice()));
            lore.add(owner);
            lore.add(ChatColor.GRAY+"上架时间: " + DATE_TIME_FORMATTER.format(record.getTime()));
            
            meta.setLore(lore);
            
            this.displayItem.setItemMeta(meta);
        }

        public int getPos() {
            return pos;
        }

        public MarketRecord getRecord() {
            return record;
        }

        public ItemStack getDisplayItem() {
            return displayItem;
        }
        
    }
    public static class InputNumber {
        private Module module;
        private Player player;
        private Callback callback;
        private int max;

        public InputNumber(Module module, Player player, Callback callback, int max) {
            this.module = module;
            this.player = player;
            this.callback = callback;
            this.max = max;
        }
        
        public void startConversation() {
            Conversation conversation = new ConversationFactory(module.getPlugin())
                    .withFirstPrompt(new NumberValidatingPrompt())
                    .addConversationAbandonedListener((event)->{
                        Object oValid = event.getContext().getSessionData("Validated");
                        if (oValid != null && oValid.equals(true)) {
                            callback.onFinish((Integer)event.getContext().getSessionData("Number"));
                        } else {
                            callback.onCancelled();
                        }
                    })
                    .buildConversation(player);
            conversation.begin();
        }
        
        public class NumberValidatingPrompt extends ValidatingPrompt {

            @Override
            @NotNull
            public String getPromptText(@NotNull ConversationContext context) {
                return ChatColor.GREEN + "请输入你想跳转至的页码, 最大为" + max + ", 或输入cancel取消";
            }

            @Override
            protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String input) {
                try {
                    if (input.equals("cancel"))
                        return true;
                    int number = Integer.valueOf(input);
                    if (number <= 0 || number > max) {
                        if (context.getForWhom() instanceof CommandSender)
                            module.getMessager().sendMessageTo((CommandSender) context.getForWhom(), ChatColor.RED + "请输入正数, 并且不大于" + max);
                        return false;
                    }
                    context.setSessionData("Number", number);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            protected @Nullable Prompt acceptValidatedInput(@NotNull ConversationContext context, @NotNull String input) {
                if (context.getSessionData("Number") != null)
                    context.setSessionData("Validated", true);
                return Prompt.END_OF_CONVERSATION;
            }
        }

        public interface Callback {
            void onFinish(int number);
            void onCancelled();
        }
    }
}
