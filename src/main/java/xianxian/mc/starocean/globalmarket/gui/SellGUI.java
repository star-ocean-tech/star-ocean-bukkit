package xianxian.mc.starocean.globalmarket.gui;

import java.time.LocalDateTime;

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

public class SellGUI extends GUI {
    private final GlobalMarket module;
    private final GlobalMarketGUI parent;
    
    private Inventory inventory;
    
    private ItemStack borderLine;
    private ItemStack nextStep;
    private ItemStack cancel;
    private ItemStack info;
    
    private boolean dropItem = true;
    private boolean conversationBegan = false;

    public SellGUI(GlobalMarketGUI parent, GlobalMarket module, Player player) {
        super(module, player);
        this.module = module;
        this.parent = parent;
        prepare();
    }

    @Override
    public void prepare() {
        inventory = getModule().getPlugin().getServer().createInventory(getPlayer(), 27, ChatColor.GREEN + "卖出物品");
    
        {
            borderLine = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemMeta meta = borderLine.getItemMeta();
            meta.setDisplayName(" ");
            borderLine.setItemMeta(meta);
        }
        
        {
            nextStep = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
            ItemMeta meta = nextStep.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN + "下一步");
            nextStep.setItemMeta(meta);
        }
        
        {
            cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta meta = cancel.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "取消");
            cancel.setItemMeta(meta);
        }
        
        {
            info = new ItemStack(Material.PAPER);
            ItemMeta meta = info.getItemMeta();
            meta.setDisplayName(ChatColor.GRAY + "将想卖的物品放到下面");
            info.setItemMeta(meta);
        }
    }

    @Override
    public void refresh() {
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 3; y++) {
                if (x == 4 && y == 1) {
                    continue;
                } else if (x == 4 && y == 0) {
                    inventory.setItem(calculateIndex(x, y), info);
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
        
        if (x == 4 && y == 1) {
            event.setCancelled(false);
        } else if (x == 2 && y == 1) {
            ItemStack item = inventory.getItem(calculateIndex(4, 1));
            if (item == null || item.getType().isEmpty()) {
                module.getMessager().sendMessageTo(getPlayer(), ChatColor.RED + "请先放入想要卖的物品");
                return;
            }
            dropItem = false;
            conversationBegan = true;
            InputNumber inputNumber = new InputNumber(getModule(), getPlayer(), new InputNumber.Callback() {
                
                @Override
                public void onFinish(double number) {
                    module.getMarketManager().sell(getPlayer(), item, number);
                    module.getMessager().sendMessageTo(getPlayer(), ChatColor.GREEN + "成功上架物品");
                }
                
                @Override
                public void onCancelled() {
                    module.getMessager().sendMessageTo(getPlayer(), ChatColor.RED + "已取消上架物品");
                    ItemStack item = inventory.getItem(calculateIndex(4, 1));
                    
                    if (item != null && !item.getType().isEmpty()) {
                        inventory.setItem(calculateIndex(4, 1), null);
                        getPlayer().getInventory().addItem(item).values().forEach((i)->{
                            getPlayer().getLocation().getWorld().dropItem(getPlayer().getLocation(), i);
                        });
                        return;
                    }
                }
            });
            inputNumber.startConversation();
            getPlayer().closeInventory();
        } else if (x == 6 && y == 1) {
            dropItem = true;
            getPlayer().closeInventory();
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void destroy() {
        if (!dropItem)
            return;

        ItemStack item = inventory.getItem(calculateIndex(4, 1));
        
        if (item != null && !item.getType().isEmpty()) {
            this.inventory.setItem(calculateIndex(4, 1), null);
            getPlayer().getInventory().addItem(item).values().forEach((i)->{
                getPlayer().getLocation().getWorld().dropItem(getPlayer().getLocation(), i);
            });
            return;
        }
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
                return ChatColor.GREEN + "请输入物品单价, 或输入cancel取消";
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
