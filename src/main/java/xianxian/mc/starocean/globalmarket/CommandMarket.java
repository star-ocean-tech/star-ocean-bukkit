package xianxian.mc.starocean.globalmarket;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import net.md_5.bungee.api.ChatColor;
import xianxian.mc.starocean.Module;
import xianxian.mc.starocean.ModuleCommand;

@CommandAlias("market")
public class CommandMarket extends ModuleCommand {
    private final GlobalMarket module;

    protected CommandMarket(GlobalMarket module) {
        super(module);
        this.module = module;
        this.module.getPlugin().getCommandManager().getCommandContexts().registerContext(GlobalMarket.class, (s)->module);
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