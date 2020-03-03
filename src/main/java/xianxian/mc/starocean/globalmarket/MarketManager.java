package xianxian.mc.starocean.globalmarket;

import java.time.Instant;
import java.time.LocalDateTime;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import xianxian.mc.starocean.globalmarket.messages.Message;
import xianxian.mc.starocean.globalmarket.record.MarketRecord;
import xianxian.mc.starocean.globalmarket.record.MarketRecord.RecordType;

public class MarketManager {
    private final GlobalMarket market;
    private Paging<MarketRecord> pagingCache;

    public MarketManager(GlobalMarket market) {
        this.market = market;
        this.pagingCache = new Paging<>(36);
    }
    
    public void sell(Player player, ItemStack item, double price) {
        MarketRecord record = MarketRecord.from(player, RecordType.SELL, item, price, LocalDateTime.now());
        market.getStorage().addRecord(record);
        market.getStorage().addLog(Instant.now(), player.getUniqueId().toString()+"("+player.getName()+") SELL "+ record.toString());
    }
    
    @SuppressWarnings("deprecation")
    public void buy(Player player, MarketRecord record, int amount) {
        if (!record.isAvailable()) {
            market.getMessager().sendMessageTo(player, ChatColor.RED + "此物品已经下架");
            return;
        }
            
        Economy economy = market.getVault().getVaultEconomy();
        double price = record.getPrice() * amount;
        if (economy.has(player, price)) {
            if (!ensureResponse(economy.withdrawPlayer(player, price))) {
                market.getMessager().sendMessageTo(player, ChatColor.RED + "在付款时出现了异常");
                return;
            }
            
            if (market.isTaxEnabled()) {
                if (!ensureResponse(economy.depositPlayer(record.getOwnerPlayer(), (price * (1 - market.getTax()))))) {
                    market.getMessager().sendMessageTo(player, ChatColor.RED + "在付款时出现了异常");
                    return;
                }
                if (!ensureResponse(economy.depositPlayer(market.getTaxAccount(), (price * (market.getTax()))))) {
                    market.getMessager().sendMessageTo(player, ChatColor.RED + "在付款时出现了异常");
                    return;
                }
            } else {
                if (!ensureResponse(economy.depositPlayer(record.getOwnerPlayer(), price))) {
                    market.getMessager().sendMessageTo(player, ChatColor.RED + "在付款时出现了异常");
                    return;
                }
            }
            if (record.getItem().getAmount() - amount <= 0) {
                market.getStorage().removeRecord(record);
            } else {
                record.getItem().setAmount(record.getItem().getAmount() - amount);
                market.getStorage().updateRecord(record);
            }
            ItemStack result = record.getItem().clone();
            result.setAmount(amount);
            player.getInventory().addItem(result).values().forEach((i)->{
                player.getLocation().getWorld().dropItem(player.getLocation(), i);
            });
            ItemMeta meta = result.getItemMeta(); 
            String displayName = "";
            if (meta.hasDisplayName())
                displayName = meta.getDisplayName();
            else
                displayName = market.getPlugin().getServer().getLocalization().getLocalizedItemName(result);
            
            market.getStorage().addLog(Instant.now(), player.getUniqueId().toString()+"("+player.getName()+") BUY " + record.toString() + "x" + amount);
            market.getMessagesManager().send(new Message.Builder()
                    .from(player.getUniqueId())
                    .to(record.getOwner())
                    .read(false)
                    .content(player.getDisplayName()+"购买了你的"+displayName+"x"+amount)
                    .date(LocalDateTime.now())
                    .build());
            market.getMessager().sendMessageTo(player, ChatColor.GREEN + "成功花费了"+economy.format(price)+"购买物品");
        } else {
            market.getMessager().sendMessageTo(player, ChatColor.RED + "你没有足够的余额购买, 需要"+economy.format(price)+", 而你只有"+economy.format(economy.getBalance(player)));
        }
    }
    
    private boolean ensureResponse(EconomyResponse response) {
        if (!response.transactionSuccess())
            market.logger().severe("Transaction failed because " + response.errorMessage + ", amount: " + response.amount + ", balance: " + response.balance);
        return response.transactionSuccess();
    }

    public Paging<MarketRecord> getDefaultPaging() {
        return pagingCache;
    }

}
