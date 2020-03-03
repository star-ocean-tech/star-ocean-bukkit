package xianxian.mc.starocean.vault;

import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import xianxian.mc.starocean.AbstractPlugin;
import xianxian.mc.starocean.Module;

public class VaultFeatures extends Module {
    private Economy vaultEconomy;
    private Permission vaultPermission;
    private Chat vaultChat;

    public VaultFeatures(AbstractPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean checkIfCanLoad() {
        if (!plugin.getServer().getPluginManager().isPluginEnabled("Vault")) {
            logger().severe("Vault found but disabled, won't enable Vault features");
            return false;
        }
        
        logger().info("Vault found, enabling Vault Features");
        
        RegisteredServiceProvider<Economy> economyProvider = getPlugin().getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider == null) {
            logger().severe("No vault economy service found");
            return false;
        } else {
            vaultEconomy = economyProvider.getProvider();
        }
        
        RegisteredServiceProvider<Permission> permissionProvider = getPlugin().getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider == null) {
            logger().severe("No vault permission service found");
            return false;
        } else {
            vaultPermission = permissionProvider.getProvider();
        }
        
        RegisteredServiceProvider<Chat> chatProvider = getPlugin().getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider == null) {
            logger().severe("No vault chat service found");
            return false;
        } else {
            vaultChat = chatProvider.getProvider();
        }
        
        return true;
    }
    
    public Economy getVaultEconomy() {
        return vaultEconomy;
    }

    public Permission getVaultPermission() {
        return vaultPermission;
    }

    public Chat getVaultChat() {
        return vaultChat;
    }

    @Override
    public void prepare() {
        
        if (getVaultPermission() != null) {
            CommandBuyPermission buypermission = new CommandBuyPermission(this);
            getPlugin().getCommandManager().registerCommand(buypermission);
        }
    }

    @Override
    public void disable() {
    }

    @Override
    public void reload() {
    }

}
