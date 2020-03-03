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
import xianxian.mc.starocean.GUI;
import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.globalmarket.GlobalMarket;
import xianxian.mc.starocean.globalmarket.gui.EditGUI.InputNumber.Callback;
import xianxian.mc.starocean.globalmarket.record.MarketRecord;

public class BuyGUI extends GUI {
    private final GlobalMarket module;
    private final GlobalMarketGUI parent;
    private final MarketRecord record;
    
    private Inventory inventory;
    
    private ItemStack borderLine;
    private ItemStack nextStep;
    private ItemStack cancel;
    private ItemStack displayItem;
    
    private boolean conversationBegan = false;

    public BuyGUI(GlobalMarketGUI parent, GlobalMarket module, Player player, MarketRecord record, ItemStack displayItem) {
        super(module, player);
        this.parent = parent;
        this.module = module;
        this.record = record;
        this.displayItem = displayItem;
        prepare();
    }

    @Override
    public void prepare() {
        inventory = getModule().getPlugin().getServer().createInventory(getPlayer(), 27, ChatColor.GREEN + "购买商品");
    
        {
            borderLine = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta meta = borderLine.getItemMeta();
            meta.setDisplayName(" ");
            borderLine.setItemMeta(meta);
        }
        
        {
            nextStep = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
            ItemMeta meta = nextStep.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "确定购买");
            nextStep.setItemMeta(meta);
        }
        
        {
            cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta meta = cancel.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "取消购买");
            cancel.setItemMeta(meta);
        }
    }

    @Override
    public void refresh() {
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 3; y++) {
                if (x == 4 && y == 1) {
                    inventory.setItem(calculateIndex(x, y), displayItem);
                } else if (x == 2 && y == 1) {
                    inventory.setItem(calculateIndex(x, y), nextStep);
                } else if (x == 6 && y == 1) {
                    inventory.setItem(calculateIndex(x, y), cancel);
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
            if (record.getItem().getAmount() == 1) {
                module.getMarketManager().buy(getPlayer(), record, 1);
            } else {
                conversationBegan = true;
                InputNumber inputNumber = new InputNumber(getModule(), getPlayer(), new InputNumber.Callback() {
                    
                    @Override
                    public void onFinish(int number) {
                        if (record.isAvailable())
                            module.getMarketManager().buy(getPlayer(), record, number);
                        else
                            module.getMessager().sendMessageTo(getPlayer(), ChatColor.RED + "此物品已经下架");
                    }
                    
                    @Override
                    public void onCancelled() {
                        module.getMessager().sendMessageTo(getPlayer(), ChatColor.RED + "已取消购买");
                    }
                }, record.getItem().getAmount());
                inputNumber.startConversation();
                
            }
            getPlayer().closeInventory();
        } else if (x == 6 && y == 1) {
            getPlayer().closeInventory();
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
    @Override
    public void destroy() {
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
                return ChatColor.GREEN + "请输入你想购买的物品数量, 最多为" + max + ", 或输入cancel取消";
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
