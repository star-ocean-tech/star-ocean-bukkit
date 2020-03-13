package xianxian.mc.starocean.globalmarket;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import xianxian.mc.starocean.ModuleCommand;

@CommandAlias("market")
public class CommandMarket extends ModuleCommand<GlobalMarket> {

    protected CommandMarket(GlobalMarket module) {
        super(module);
    }
    
    @Subcommand("open")
    @CommandPermission("starocean.commands.market.open")
    public static void open(Player player, GlobalMarket module) {
        module.openGlobalMarketGUI(player);
    }
    
    @Subcommand("sell")
    @CommandPermission("starocean.commands.market.sell")
    public static void sell(Player player, GlobalMarket module, double price) {
        ItemStack item = player.getInventory().getItemInMainHand();
        player.getInventory().setItemInMainHand(null);
        if (price < 0) {
            return;
        }
        module.getMarketManager().sell(player, item, price);
    }
}
