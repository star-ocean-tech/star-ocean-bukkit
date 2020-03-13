package xianxian.mc.starocean.globalmarket.gui;

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.globalmarket.GlobalMarket;
import xianxian.mc.starocean.globalmarket.record.MarketRecord;
import xianxian.mc.starocean.gui.GUI;

public class EditGUI extends GUI {
    private final GlobalMarket module;
    private final MarketRecord record;
    private final GlobalMarketGUI parent;
    
    private Inventory inventory;
    
    private ItemStack borderLine;
    private ItemStack remove;
    private ItemStack changePrice;
    private ItemStack displayItem;
    
    private boolean conversationBegan = false;

    public EditGUI(GlobalMarketGUI parent, GlobalMarket module, Player player, MarketRecord record) {
        super(module, player);
        this.parent = parent;
        this.module = module;
        this.record = record;
        onCreate();
    }

    @Override
    public void onCreate() {
        inventory = getModule().getPlugin().getServer().createInventory(this, 27, ChatColor.GREEN + "编辑商品");
    
        {
            borderLine = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta meta = borderLine.getItemMeta();
            meta.setDisplayName(" ");
            borderLine.setItemMeta(meta);
        }
        
        {
            changePrice = new ItemStack(Material.NAME_TAG);
            ItemMeta meta = changePrice.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "修改价格");
            changePrice.setItemMeta(meta);
        }
        
        {
            remove = new ItemStack(Material.BARRIER);
            ItemMeta meta = remove.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "下架商品");
            remove.setItemMeta(meta);
        }
    }

    @Override
    public void refresh() {
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 3; y++) {
                if (x == 4 && y == 1) {
                    inventory.setItem(calculateIndex(x, y), record.getItem());
                } else if (x == 2 && y == 1) {
                    inventory.setItem(calculateIndex(x, y), changePrice);
                } else if (x == 6 && y == 1) {
                    inventory.setItem(calculateIndex(x, y), remove);
                } else {
                    inventory.setItem(calculateIndex(x, y), borderLine);
                }
            }
        }
    }

    @Override
    public void click(InventoryClickEvent event) {
        int slot = event.getSlot();
        int[] pos = calculatePosition(slot);
        int x = pos[0];
        int y = pos[1];
        if (x == 2 && y == 1) {
            conversationBegan = true;
            InputNumber inputNumber = new InputNumber(getModule(), getPlayer(), new InputNumber.Callback() {
                
                @Override
                public void onFinish(double number) {
                    if (!record.isAvailable()) {
                        module.getMessager().sendMessageTo(getPlayer(), ChatColor.RED + "此物品已经下架");
                        return;
                    }
                    record.setPrice(number);
                    module.getStorage().updateRecord(record);
                    module.getMessager().sendMessageTo(getPlayer(), ChatColor.GREEN + "成功更新价格");
                }
                
                @Override
                public void onCancelled() {
                    module.getMessager().sendMessageTo(getPlayer(), ChatColor.RED + "已取消更新价格");
                }
            });
            inputNumber.startConversation();
            getPlayer().closeInventory();
        } else if (x == 6 && y == 1) {
            if (!record.isAvailable()) {
                module.getMessager().sendMessageTo(getPlayer(), ChatColor.RED + "此物品已经下架");
                getPlayer().closeInventory();
                return;
            }
            module.getStorage().removeRecord(record);
            getPlayer().getInventory().addItem(record.getItem()).values().forEach((i)->{
                getPlayer().getLocation().getWorld().dropItem(getPlayer().getLocation(), i);
            });
            module.getMessager().sendMessageTo(getPlayer(), ChatColor.GREEN + "成功下架该物品");
            getPlayer().closeInventory();
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    @Override
    public void onDestroy() {
    }

    public static class InputNumber {
        private Module module;
        private Player player;
        private Callback callback;

        public InputNumber(Module module, Player player, Callback callback) {
            this.module = module;
            this.player = player;
            this.callback = callback;
        }
        
        public void startConversation() {
            Conversation conversation = new ConversationFactory(module.getPlugin())
                    .withFirstPrompt(new NumberValidatingPrompt())
                    .addConversationAbandonedListener((event)->{
                        Object oValid = event.getContext().getSessionData("Validated");
                        if (oValid != null && oValid.equals(true)) {
                            callback.onFinish((Double)event.getContext().getSessionData("Number"));
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
                return ChatColor.GREEN + "请输入新的价格, 或输入cancel取消";
            }

            @Override
            protected boolean isInputValid(@NotNull ConversationContext context, @NotNull String input) {
                try {
                    if (input.equals("cancel"))
                        return true;
                    double number = Double.valueOf(input);
                    if (number <= 0) {
                        if (context.getForWhom() instanceof CommandSender)
                            module.getMessager().sendMessageTo((CommandSender) context.getForWhom(), ChatColor.RED + "请输入正数");
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
            void onFinish(double number);
            void onCancelled();
        }
    }
}
