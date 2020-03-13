package xianxian.mc.starocean.vault;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;
import xianxian.mc.starocean.ModuleCommand;

@CommandAlias("buypermission|buyperm")
public class CommandBuyPermission extends ModuleCommand<VaultFeatures> {
    
    public CommandBuyPermission(VaultFeatures module) {
        super(module);
    }
    
    @Default
    @Syntax("<player> <permission> <price>")
    @CommandCompletion("@players")
    @Subcommand("buy")
    @Description("Buy permission for a player")
    public static void buy(CommandSender sender, VaultFeatures module, OnlinePlayer onlinePlayer, String permission, double price) {
        if (price <= 0) {
            module.getMessager().sendMessageTo(sender, ChatColor.RED + "输入的价格无效");
            return;
        }
        
        Player player = onlinePlayer.getPlayer();
        if (player == null) {
            module.getMessager().sendMessageTo(sender, ChatColor.RED + "该玩家未在线或不存在");
            return;
        }
        if (permission.isEmpty()) {
            module.getMessager().sendMessageTo(sender, ChatColor.RED + "该权限无效");
            return;
        }
        
        Permission vaultPermission = module.getVaultPermission();
        if (vaultPermission.has(player, permission)) {
            module.getMessager().sendMessageTo(player, ChatColor.RED + "你已拥有该权限，请勿重复购买");
            return;
        }
        
        Economy economy = module.getVaultEconomy();
        double balance = economy.getBalance(player);
        if (balance >= price) {
            EconomyResponse response = economy.withdrawPlayer(player, price);
            
            if (response.transactionSuccess()) {
                vaultPermission.playerAdd(null, player, permission);
                module.getMessager().sendMessageTo(player, ChatColor.GREEN + "成功花费" + economy.format(price) + ChatColor.RESET + ChatColor.GREEN + "购买该权限");   
            }
            else
                module.getMessager().sendMessageTo(player, ChatColor.RED + "购买失败，因为:"+response.errorMessage);
        } else {
            module.getMessager().sendMessageTo(player, ChatColor.RED + "你没有足够的余额来购买此权限");
        }
    }
}
